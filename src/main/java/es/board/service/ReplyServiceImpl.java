package es.board.service;

import es.board.model.req.ReplyRequest;
import es.board.model.res.ReplyCreateResponse;
import es.board.repository.dao.ReplyDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements  ReplyService{

    private final ReplyDAO replyDAO;

    @Override
    public List<ReplyRequest> getPartialReply(String id){
        ReplyRequest request=new ReplyRequest();

        return request.ReplyListToDTO(replyDAO.findPartialReply(id));
    }
    @Override
    public  void  saveReply(ReplyCreateResponse response){
        replyDAO.saveReply(response);
    }
}
