package es.board.infrastructure.es;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.mapper.document.FeedDocumentMapper;
import es.board.infrastructure.es.document.Feed;
import es.board.infrastructure.es.document.FeedDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SearchIndexServiceImpl implements SearchIndexService{


    private final FeedDAO feedDAO;

    @Override
    public List<PostDTO.Response> getSearchTitle(String title) {
        List<Feed> searchTitle = feedDAO.findSearchTitle(title);
        return FeedDocumentMapper.fromPostDtoList(searchTitle);
    }

    @Override
    public List<PostDTO.Response> getSearchContent(String content) {
        List<Feed> searchContent = feedDAO.findSearchContent(content);
        return FeedDocumentMapper.fromPostDtoList(searchContent);
    }

    @Override
    public List<PostDTO.Response> getSearchPost(String text) {
        List<Feed> searchPost = feedDAO.findSearchPost(text);
       return FeedDocumentMapper.fromPostDtoList(searchPost);
    }

    @Override
    public List<PostDTO.Response> getSearchComment() {
        return null;
    }

    @Override
    public List<PostDTO.Response> getSearchReply() {
        return null;
    }

    @Override
    public List<PostDTO.Response> getSearchUser(String username) {
        List<Feed> searchUserPost = feedDAO.findSearchUserPost(username);
         return FeedDocumentMapper.fromPostDtoList(searchUserPost);
    }
}
