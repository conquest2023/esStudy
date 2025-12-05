package es.board.infrastructure.es.document;

import java.util.List;

public interface FeedDAO {


    List<Feed> findSearchContent(String content);

    List<Feed> findSearchTitle(String title);

    List<Feed> findSearchPost(String text);


    List<Feed> findSearchUserPost(String username);
}
