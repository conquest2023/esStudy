package es.board.infrastructure.es;

import es.board.controller.model.dto.feed.PostDTO;

import java.util.List;

public interface SearchIndexService {




    List<PostDTO.Response> getSearchPost(String text);


    List<PostDTO.Response> getSearchTitle(String title);

    List<PostDTO.Response> getSearchContent(String content);


    List<PostDTO.Response> getSearchComment();


    List<PostDTO.Response> getSearchReply();

    List<PostDTO.Response> getSearchUser(String username);
}
