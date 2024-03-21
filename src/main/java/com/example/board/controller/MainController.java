package com.example.board.controller;

import com.example.board.domain.board.Board;
import com.example.board.domain.member.Member;
import com.example.board.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final BoardService boardService;

    @GetMapping
    public String showBoardList(@SessionAttribute(name = "loginMember", required = false) Member loginedMember, Model model) {
        List<Board> boardList = boardService.findBoardList();
        boolean isLogined = loginedMember != null;
//        log.info("boardList.size()={}", boardList.size());

        model.addAttribute("boardList", boardList);
        model.addAttribute("isLogined", isLogined);
        return "homepage";
    }
}
