package com.example.board.controller;

import com.example.board.domain.member.LoginDto;
import com.example.board.domain.member.Member;
import com.example.board.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String showLoginForm(Model mode, HttpServletRequest request) {
        String preUrl = request.getHeader("Referer");
        HttpSession session = request.getSession();

        // 이전화면이 로그인 화면이 아니라면
        if (preUrl.contains("/login") == false) {
            session.setAttribute("preUrl", preUrl);
        }

        log.info("url={}", preUrl);

        return "loginpage";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginDto") LoginDto loginDto, HttpSession session) {

        log.info("id={} pass={}", loginDto.getId(), loginDto.getPass());

        String preUrl = (String) session.getAttribute("preUrl");
        boolean couldLogin = memberService.canLogin(loginDto);

        log.info("couldLogin={}", couldLogin);

        return "redirect:" + (couldLogin ? preUrl : "/login");
    }

    @GetMapping("/join")
    public String showJoinForm() {
        return "joinpage";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute Member member,HttpSession session) {
        log.info("id={} pass={} nickName={}", member.getId(), member.getPass(), member.getNickName());
        boolean isJoined = memberService.join(member);

        String preUrl = (String) session.getAttribute("preUrl");
        log.info("url={}", preUrl);

        return "redirect:" + (isJoined ? preUrl : "/join");
    }
}
