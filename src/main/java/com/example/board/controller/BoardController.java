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

    @GetMapping("/{boardName}")
    public String showPostList(@ModelAttribute(name = "postSearchDto") PostSearchDto postSearchDto,
                               @ModelAttribute(name = "boardName") String boardName,
                               @RequestParam(name = "currentPage", defaultValue = "0") long currentPage,
                               @SessionAttribute(name = "loginMember", required = false) Member loginedMember,
                               Model model) {
        //페이징
        int postSize = 5; // 한 페이지에 보여줄 post 개수

        List<Post> posts = boardService.findPosts(boardName, postSearchDto);

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
        model.addAttribute("boardName", boardName);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("yesterdayTime", LocalDateTime.now().minus(24, ChronoUnit.HOURS));

        boolean isLogined = loginedMember != null;
        model.addAttribute("isLogined", isLogined);

        return "boardmainpage";
    }

    @GetMapping("/{boardName}/{postId}")
    public String showPost(@ModelAttribute("boardName") String boardName,
                           @PathVariable("postId") long postId,
                           @SessionAttribute(name = "loginMember", required = false) Member loginedMember,
                           Model model) {
        Post findPost = boardService.findById(boardName, postId).get();
        List<Comment> comments = boardService.findAllCommentsByPostId(boardName, postId);

        int findPostHits = findPost.getHits() + 1;
        boardService.addHits(boardName, postId, findPostHits);
        findPost.setHits(findPostHits);

        //post 작성자와 로그인된 멤버가 같은사람인지
        boolean isLogined = loginedMember != null;

        model.addAttribute("post", findPost);
        model.addAttribute("comments", comments);
        model.addAttribute("isLogined", isLogined);
        model.addAttribute("yesterdayTime", LocalDateTime.now().minus(24, ChronoUnit.HOURS));

        if (isLogined) {
            boolean isWriter = findPost.getNickname().equals(loginedMember.getNickname());
            model.addAttribute("loginedNickname", loginedMember.getNickname()); //댓글 비교시
            model.addAttribute("isWriter", isWriter);
        }

        return "viewpost";
    }

    @GetMapping("/{boardName}/write")
    public String showWritePostForm(@ModelAttribute("boardName") String boardName, Model model) {
        return "writepost";
    }

    @PostMapping("/{boardName}/write")
    public String writePost(@ModelAttribute("boardName") String boardName,
                            @ModelAttribute("post") Post post,
                            @SessionAttribute(name = "loginMember", required = false) Member loginedMember,
                            RedirectAttributes redirectAttributes) {
        Post savedPost = boardService.savePost(boardName, post, loginedMember);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        return "redirect:/{boardName}/{postId}";
    }

    @GetMapping("/{boardName}/{postId}/edit")
    public String showEditPostForm(@ModelAttribute("boardName") String boardName,
                                   @PathVariable("postId") long postId,
                                   Model model) {
        Post findPost = boardService.findById(boardName, postId).get();
        model.addAttribute("post", findPost);
        return "editpost";
    }

    @PostMapping("/{boardName}/{postId}/edit")
    public String editPost(@ModelAttribute("boardName") String boardName,
                           @PathVariable("postId") long postId,
                           @Validated @ModelAttribute UpdatePostDto updateParam) {
        boardService.updatePost(boardName, postId, updateParam);
        return "redirect:/{boardName}/{postId}";
    }

    @PostMapping("/{boardName}/delete")
    public String deletePost(@ModelAttribute("boardName") String boardName,
                             @RequestParam("postId") long postId) {
        boardService.deleteById(boardName, postId);
        return "redirect:/{boardName}";
    }

    @PostMapping("/{boardName}/{postId}/comment/write")
    public String writeComment(@ModelAttribute("boardName") String boardName,
                               @PathVariable("postId") long postId,
                               @ModelAttribute("commentContent") Comment comment,
                               @SessionAttribute(name = "loginMember", required = false) Member loginedMember,
                               Model model) {
        log.info("--------------------------------------writeComment()-------------------------------------------");
        log.info("comment content={} ", comment.getCommentContent());
        boardService.saveComment(boardName, postId, loginedMember, comment);
        return "redirect:/board/{boardName}/{postId}";
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
