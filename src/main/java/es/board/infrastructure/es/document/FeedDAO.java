package es.board.infrastructure.es.document;

import java.util.List;

public interface FeedDAO {




    List<Feed> findSearchPost(String text);


    List<Feed> findSearchUserPost(String username);
}
