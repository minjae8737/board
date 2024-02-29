package com.example.board.controller;

import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final BoardService boardService;

    @GetMapping
    public String showBoardList(Model model) {
        List<String> boardList = boardService.findBoardList();

        log.info("boardList.size()={}", boardList.size());

        model.addAttribute("boardList", boardList);
        return "homepage";
    }
}
