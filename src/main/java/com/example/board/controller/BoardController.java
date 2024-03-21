package com.example.board.controller;

import com.example.board.domain.board.Comment;
import com.example.board.domain.board.Post;
import com.example.board.domain.board.PostSearchDto;
import com.example.board.domain.board.UpdatePostDto;
import com.example.board.domain.member.Member;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/{boardId}")
    public String showPostList(@ModelAttribute(name = "postSearchDto") PostSearchDto postSearchDto,
                               @ModelAttribute(name = "boardId") String boardId,
                               @RequestParam(name = "currentPage", defaultValue = "0") long currentPage,
                               @SessionAttribute(name = "loginMember", required = false) Member loginedMember,
                               Model model) {
        //페이징
        int postSize = 5; // 한 페이지에 보여줄 post 개수

        List<Post> posts = boardService.findPosts(boardId, postSearchDto);

        int postCount = posts.size();  // board 총 개수
        //totalPages : 페이지 총 개수
        int totalPages = ((float) postCount % (float) postSize == 0f) ? postCount / postSize : postCount / postSize + 1;

//        log.info("postCount={} totalPages={}", postCount, totalPages);

        int startBoardIndex = (int) currentPage * postSize;
        //endBoardIndex : 마지막 페이지에서 board개수가 boardSize보다 작으면 boardCount 같으면 (int) (page + 1) * boardSize
        int endBoardIndex = Math.min(postCount, (int) (currentPage + 1) * postSize);

        int startPage = getStartPage((int) currentPage + 1);
        int endPage = getEndPage((int) currentPage + 1, totalPages);

//        log.info("startPage={} endPage={}", startPage, endPage);
//        log.info("currentPage={} totalPages={}", currentPage, totalPages);

        posts = posts.subList(startBoardIndex, endBoardIndex);  //현재 page에 표시할 board 리스트

        model.addAttribute("posts", posts);
        model.addAttribute("boardId", boardId);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("yesterdayTime", LocalDateTime.now().minus(24, ChronoUnit.HOURS));

        boolean isLogined = loginedMember != null;
        model.addAttribute("isLogined", isLogined);

        return "boardmainpage";
    }

    @GetMapping("/{boardId}/{postId}")
    public String showPost(@ModelAttribute("boardId") String boardId,
                           @PathVariable("postId") long postId,
                           @SessionAttribute(name = "loginMember", required = false) Member loginedMember,
                           Model model) {
        Post findPost = boardService.findById(postId).get();
        List<Comment> comments = boardService.findAllCommentsByPostId(boardId, postId);

        int findPostHits = findPost.getHits() + 1;
        boardService.addHits(boardId, postId, findPostHits);
        findPost.setHits(findPostHits);

        //post 작성자와 로그인된 멤버가 같은사람인지
        boolean isLogined = loginedMember != null;

        model.addAttribute("post", findPost);
        model.addAttribute("comments", comments);
        model.addAttribute("isLogined", isLogined);
        model.addAttribute("yesterdayTime", LocalDateTime.now().minus(24, ChronoUnit.HOURS));

        if (isLogined) {
            boolean isPostWriter = findPost.getMemberNickname().equals(loginedMember.getNickname());
            log.info("");
            model.addAttribute("loginedNickname", loginedMember.getNickname()); //댓글 비교시
            model.addAttribute("isPostWriter", isPostWriter);
        }

        return "viewpost";
    }

    @GetMapping("/{boardId}/write")
    public String showWritePostForm(@ModelAttribute("boardId") String boardId, Model model) {
        return "writepost";
    }

    @PostMapping("/{boardId}/write")
    public String writePost(@ModelAttribute("boardId") String boardId,
                            @ModelAttribute("post") Post post,
                            @SessionAttribute(name = "loginMember", required = false) Member loginedMember,
                            RedirectAttributes redirectAttributes) {
        Post savedPost = boardService.savePost(boardId, post, loginedMember);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        return "redirect:/board/{boardId}/{postId}";
    }

    @GetMapping("/{boardId}/{postId}/edit")
    public String showEditPostForm(@ModelAttribute("boardId") String boardId,
                                   @PathVariable("postId") long postId,
                                   Model model) {
        Post findPost = boardService.findById(postId).get();
        model.addAttribute("post", findPost);
        return "editpost";
    }

    @PostMapping("/{boardId}/{postId}/edit")
    public String editPost(@ModelAttribute("boardId") String boardId,
                           @PathVariable("postId") long postId,
                           @Validated @ModelAttribute UpdatePostDto updateParam) {
        boardService.updatePost(postId, updateParam);
        return "redirect:/board/{boardId}/{postId}";
    }

    @PostMapping("/{boardId}/delete")
    public String deletePost(@ModelAttribute("boardId") String boardId,
                             @RequestParam("postId") long postId) {
        boardService.deleteById(boardId, postId);
        return "redirect:/board/board/{boardId}";
    }

    @PostMapping("/{boardId}/{postId}/comment/write")
    public String writeComment(@ModelAttribute("boardId") String boardId,
                               @PathVariable("postId") long postId,
                               @ModelAttribute("commentContent") Comment comment,
                               @SessionAttribute(name = "loginMember", required = false) Member loginedMember,
                               Model model) {
        log.info("--------------------------------------writeComment()-------------------------------------------");
        log.info("comment content={} ", comment.getCommentContent());
        boardService.saveComment(boardId, postId, loginedMember, comment);
        return "redirect:/board/{boardId}/{postId}";
    }

    @GetMapping("/{boardId}/{postId}/comment/{commentId}/edit")
    public String editComment(@ModelAttribute("boardId") String boardId,
                              @PathVariable("postId") long postId,
                              @PathVariable("commentId") long commentId,
                              Model model) {
        log.info("commentId={}", commentId);

        return "redirect:/board/{boardId}/{postId}";
    }

    @GetMapping("/{boardId}/{postId}/comment/{commentId}/delete")
    public String deleteComment(@ModelAttribute("boardId") String boardId,
                                @PathVariable("postId") long postId,
                                @PathVariable("commentId") long commentId,
                                Model model) {
        boardService.deleteCommentById(boardId, commentId);
        return "redirect:/board/{boardId}/{postId}";
    }

    private static int getStartPage(int currentPage) {
        return Math.max(1, currentPage - 4);
    }

    private static int getEndPage(int currentPage, int totalPages) {
        if (totalPages < 10) {
            return Math.max(currentPage, totalPages);
        } else {
            return (currentPage + 4 <= 10) ? 10 : Math.min(currentPage + 4, totalPages);
        }
    }

}
