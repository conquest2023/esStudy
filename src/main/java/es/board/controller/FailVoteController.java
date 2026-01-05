//package es.board.controller;
//
//
//import es.board.config.jwt.JwtTokenProvider;
//import es.board.controller.model.dto.feed.VoteDTO;
//import es.board.ex.TokenValidator;
//import es.board.service.VoteService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//@RequestMapping("/api")
//public class VoteController {
//
//    private final VoteService voteService;
//
//    private  final TokenValidator tokenValidator;
//
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @PostMapping("/save/vote")
//    @ResponseBody
//    public ResponseEntity<?> saveVote(@RequestBody VoteDTO.Request vote, @RequestHeader(value = "Authorization", required = false) String token) {
//        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
//        if (tokenCheckResponse == null) {
//            return tokenCheckResponse;
//        }
//        token=token.substring(7);
//        voteService.saveVote(vote, jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
//        Map<String, Object> response = new HashMap<>();
//        return ResponseEntity.ok(response);
//    }
//
//
//    @PostMapping("/save/user/vote")
//    @ResponseBody
//    public ResponseEntity<?> saveUserVote(@RequestBody VoteDTO.Request vote, @RequestHeader(value = "Authorization") String token) {
//        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
//        if (tokenCheckResponse == null) {
//            return tokenCheckResponse;
//        }
//        token=token.substring(7);
//        voteService.createdFeedVote(vote, jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
//        Map<String, Object> response = new HashMap<>();
//        return ResponseEntity.ok(response);
//    }
//
//
//    @PostMapping("/save/ticket/vote")
//    @ResponseBody
//    public ResponseEntity<?> saveFeedVote(@RequestBody VoteDTO.Request vote, @RequestHeader(value = "Authorization") String token) {
//        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
//        if (tokenCheckResponse == null) {
//            return tokenCheckResponse;
//        }
//        token=token.substring(7);
//        voteService.saveFeedTicket(vote,jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
//        Map<String, Object> response = new HashMap<>();
//        return ResponseEntity.ok(response);
//    }
//    @PostMapping("/save/aggregation/vote")
//    public ResponseEntity<?> saveAggregationVote(@RequestBody VoteDTO.Request vote,
//                                                 @RequestHeader(value = "Authorization", required = false) String token) {
//        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
//        if (tokenCheckResponse == null) {
//            return tokenCheckResponse;
//        }
//        token=token.substring(7);
//        voteService.saveAgreeVote(vote, jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "투표 완료");
//        response.put("voteStatus", "completed");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/get/aggregation/vote/{id}")
//    public ResponseEntity<?> getAggregationVote(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id) {
//        Map<String, Object> aggregation = voteService.getVoteAggregation(id);
//        Map<String, Object> response = new HashMap<>();
//        response.put("totalVotes", aggregation.get("totalVotes"));
//        response.put("upvotes", aggregation.get("upvotes"));
//        response.put("downvotes", aggregation.get("downvotes"));
//        List<String> userIds = (List<String>) aggregation.get("userIds");
//        if (!token.isEmpty()) {
//            token = token.substring(7);
//            if (jwtTokenProvider.validateToken(token)) {
//                boolean hasVoted = userIds.contains(jwtTokenProvider.getUserId(token));
//                response.put("hasVoted", hasVoted);
//                return ResponseEntity.ok(response);
//            }
//        }
//        response.put("hasVoted", false);
//        return ResponseEntity.ok(response);
//    }
//    @GetMapping("/search/view/vote/page")
//    public ResponseEntity<?> getMostViewFeed(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
////        log.info(voteService.getVotePageFeed(page,size).toString().get);
//        Map<String,Object> result= voteService.getVotePageMainFeed(page, size);
//
//        List<VoteDTO> voteRequests = (List<VoteDTO>) result.get("data");
//        return ResponseEntity.ok(Map.of(
//                "data", voteRequests,
//                "totalPage", result.get("totalPage")));
//
//    }
//    @GetMapping("/get/vote")
//    public ResponseEntity<?> getVote() {
//
//        return ResponseEntity.ok(voteService.getVoteContent());
//    }
//
//    @GetMapping("/get/ticket/vote")
//    public ResponseEntity<?> getTicketVote(
//            @RequestHeader(value = "Authorization", required = false) String token,
//            @RequestParam String id) {
//        Map<String, Object> objectMap = voteService.getVoteTicketAll(id);
//
//        if (token == null) {
//            return ResponseEntity.ok(Map.of(
//                    "hasVoted", false,
//                    "selectOption", objectMap.get("selectOption")
//            ));
//        }
//        token = token.substring(7);
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Token"));
//        }
//        Object userIds = objectMap.get("users");
//        return ResponseEntity.ok(Map.of(
//                "hasVoted", checkTicketOwner(userIds,jwtTokenProvider.getUserId(token)),
//                "selectOption", objectMap.get("selectOption")
//        ));
//    }
//
//    @GetMapping("/get/vote/all")
//    public ResponseEntity<?> getVoteAll() {
//        return ResponseEntity.ok(Map.of(
//                "vote", voteService.getVoteAll()));
//    }
//    @PostMapping("/search/view/vote/delete")
//    @ResponseBody
//    public ResponseEntity<?> deleteVoteFeed(
//            @RequestBody Map<String, String> requestData, @RequestHeader(value = "Authorization") String token) {
//        String id = requestData.get("id");
//        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
//        if (tokenCheckResponse != null) {
//            return tokenCheckResponse;
//        }
//        token=token.substring(7);
//        voteService.deleteVoteFeed(id, jwtTokenProvider.getUserId(token));
//
//        Map<String, String> response = new HashMap<>();
//        response.put("redirectUrl", "/");
//        return ResponseEntity.ok(response);
//    }
//    @GetMapping("/get/user/vote")
//    public ResponseEntity<?> getUserVote() {
//        return ResponseEntity.ok((Map.of("data", voteService.getVoteUserAll())));
//        }
//
//    private static boolean checkTicketOwner(Object userIds, String userId) {
//        boolean hasVoted=false;
//        if (userIds instanceof Set<?>) {
//            Set<String> userSet = (Set<String>) userIds;
//            hasVoted = userSet.contains(userId);
//        }
//        return hasVoted;
//    }
//
//}
//
////    @GetMapping("/get/user/vote/details")
////    public ResponseEntity<?> getUserVoteDetail(@RequestParam String feedUID) {
////        return ResponseEntity.ok((Map.of("data", voteService.getVoteUserAll())));
////       }
////    }