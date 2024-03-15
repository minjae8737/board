package com.example.board;

import com.example.board.repository.board.BoardRepository;
import com.example.board.repository.board.JpaBoardRepository;
import com.example.board.repository.member.JpaMemberRepository;
import com.example.board.repository.member.MemberRepository;
import com.example.board.service.BoardService;
import com.example.board.service.MemberService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {

    private final EntityManager em;

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new JpaMemberRepository(em);
    }

    @Bean
    public BoardService boardService() {
        return new BoardService(boardRepository());
    }

    @Bean
    public BoardRepository boardRepository() {
        return new JpaBoardRepository(em);
    }
}
