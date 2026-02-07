package es.board.domain.feed;

import es.board.controller.model.dto.PostDetailResponse;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.poll.PollDto;
import es.board.mapper.entity.PostDomainMapper;
import es.board.domain.feed.event.PostCreatedEvent;
import es.board.infrastructure.entity.feed.PostImage;
import es.board.infrastructure.entity.poll.PollEntity;
import es.board.infrastructure.entity.user.User;
import es.board.infrastructure.mq.PostEventPublisher;
import es.board.infrastructure.mq.ViewEventPublisher;
import es.board.repository.entity.repository.PostImageRepository;
import es.board.infrastructure.feed.PostQueryRepository;
import es.board.domain.PostRepository;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.poll.PollRepository;
import es.board.domain.Post;
import es.board.domain.poll.PollService;
import es.board.repository.entity.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static es.board.util.ResizeImage.heightFrom;
import static es.board.util.ResizeImage.widthFrom;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {


    private final ApplicationEventPublisher eventPublisher;

    private final PostEventPublisher postEventPublisher;

    private final ViewEventPublisher viewEventPublisher;

    private final PollService pollService;

    private final UserRepository userRepository;

    private final PostImageRepository imageRepository;

    private final PostRepository postRepository;

    private final PollRepository pollRepository;

    private final PostQueryRepository queryRepository;

    @Override
    public List<PostDTO.Response> getRecommendPost(List<Integer> ids) {


        List<PostEntity> recommendPost = queryRepository.findRecommendPost(ids);
        return PostDomainMapper.toResponse(recommendPost);
    }

    @Override
    @Transactional
    public void incrementViewCount(int postId,String userId) {

        postRepository.increaseViewCount(postId);

        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent()) {
            Optional<PostEntity> entity = postRepository.findById(postId);
            viewEventPublisher.publishFeedViewed(entity.get(), user.get());
        }
    }

    @Override
    public void savePost(String userId, PostDTO.Request req) {

        Post post = PostDomainMapper.toDomain(userId, req);
        PostEntity postEntity = postRepository.savePost(Post.toEntity(post));
        int postId = postEntity.getId();
        imageUploadAndRewriteHtml(req.getDescription(), postId);
        eventPublisher.publishEvent(new PostCreatedEvent(postId,userId,req));
        postEventPublisher.publishFeedCreated(postEntity);
    }
    @Override
    @Transactional
    public PostDTO.Response updatePost(int id, String userId, PostDTO.Update update) {

        PostEntity postEntity = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Post not found"));
        postEntity.applyFrom(update.getTitle(), update.getDescription(), LocalDateTime.now());
        Post domain = Post.toDomain(postEntity);
        PostEntity entity = Post.toEntity(domain);
        postEventPublisher.publishFeedUpdated(entity);
        return PostDomainMapper.toResponse(postEntity.getUserId(), domain);

    }


    @Override
    public Page<Integer> findIds(int page, int size) {
        return queryRepository.findIds(page,size);
    }

    @Override
    public Page<PostEntity> findCategoryPost(String category, int page, int size) {

        return  queryRepository.findByCategoryPosts(category,page,size);
    }

    @Override
    public Page<PostEntity> findPostPagingList(int page, int size) {
        Page<PostEntity> byPagePosts = queryRepository.findByPagePosts(page,size);
        return byPagePosts;
    }

    @Override
    public PostDetailResponse findPostDetail(String userId, int postId) {
        Optional<PollEntity> pollOpt = pollRepository.findByPostId(postId);
        PostEntity postEntity = queryRepository.findPostDetail(postId);
        Post post = Post.toDomain(postEntity);
        PostDTO.Response postDto = PostDomainMapper.toResponse(userId, post);
        if (pollOpt.isEmpty()) {
            return PostDetailResponse.builder()
                    .post(postDto)
                    .poll(null)
                    .hasPoll(false)
                    .build();
        }
        PollDto.Response pollDto = pollService.getPollDetail(postId);
        return PostDetailResponse.builder()
                .post(postDto)
                .poll(pollDto)
                .hasPoll(true)
                .build();
    }

    @Override
    public void deletePost(int id) {
        postRepository.deletePost(id);
        postEventPublisher.publishFeedDeleted(id);
    }


    private void imageUploadAndRewriteHtml(String descriptionHtml, int postId) {

        String safeHtml = Jsoup.clean(descriptionHtml,
                Safelist.basicWithImages()
                        .addAttributes("img", "width", "height", "alt", "style"));
        Document doc = Jsoup.parseBodyFragment(safeHtml);
        Elements imgs = doc.select("img");
        if (imgs.isEmpty()) {
            return;
        }
        int seq = 1;
        for (Element img : imgs) {
            String url = img.attr("src");
            Integer w = widthFrom(img);
            Integer h = heightFrom(img);
            PostImage postImage = PostImage.builder()
                    .postId(postId)
                    .imageUrl(url)
                    .seq(seq++)
                    .displayWidth(w)
                    .displayHeight(h)
                    .createdAt(LocalDateTime.now())
                    .build();
            log.info(postImage.toString());
            imageRepository.save(postImage);
        }
        // 0) 업로드 (files 없으면 빈 리스트)
//        List<String> uploadUrls = (files == null || files.isEmpty())
//                ? List.of()
//                : s3Uploader.upload(files); // 파일 순서 == 업로드 URL 순서

        // 1) sanitize 한 번만
//        String safeHtml = Jsoup.clean(descriptionHtml,
//                Safelist.basicWithImages()
//                        .addAttributes("img", "width", "height", "alt", "loading", "srcset", "data-src", "style"));
//
//        // 2) 파싱 한 번만
//        Document doc = Jsoup.parseBodyFragment(safeHtml);
//        Elements imgs = doc.select("img");
//
//        int seq = 1;
//        int upIdx = 0; // uploadUrls 소비 인덱스
//
//        for (Element img : imgs) {
//            String src = ResizeImage.pickSrc(img);
//            boolean hasHttp = src != null && (src.startsWith("http://") || src.startsWith("https://"));
//
//            // A) 이미 절대 URL인 경우: 업로드 스킵, 메타만 저장
//            if (hasHttp) {
//                Integer w = widthFrom(img);
//                Integer h = heightFrom(img);
//                imageRepository.save(PostImage.builder()
//                        .postId(postId)
//                        .imageUrl(src)
//                        .seq(seq++)
//                        .displayWidth(w)
//                        .displayHeight(h)
//                        .createdAt(LocalDateTime.now())
//                        .build());
//                continue;
//            }
//
//            // B) 로컬/미리보기 이미지인 경우: 업로드 URL로 치환
//            if (upIdx >= uploadUrls.size()) {
//                // 매칭 실패: 로컬 이미지가 더 많은 상황 → 스킵하거나 예외 처리 선택
//                continue;
//            }
//            String uploadedUrl = uploadUrls.get(upIdx++);
//            // HTML 치환
//            img.attr("src", uploadedUrl);
//            img.removeAttr("data-src");
//            img.removeAttr("srcset");
//            if (!img.hasAttr("loading")) img.attr("loading", "lazy");
//
//            Integer w = widthFrom(img);
//            Integer h = heightFrom(img);
//            imageRepository.save(PostImage.builder()
//                    .postId(postId)
//                    .imageUrl(uploadedUrl)
//                    .seq(seq++)
//                    .displayWidth(w)
//                    .displayHeight(h)
//                    .createdAt(LocalDateTime.now())
//                    .build());
//        }
    }
}
