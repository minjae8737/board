package com.example.board;

import com.example.board.repository.board.BoardRepository;
import com.example.board.repository.board.JdbcBoardRepository;
import com.example.board.repository.member.JdbcMemberRepository;
import com.example.board.repository.member.MemberRepository;
import com.example.board.service.BoardService;
import com.example.board.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcConfig {

    private final DataSource dataSource;
    private final NamedParameterJdbcTemplate template;

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new JdbcMemberRepository(template);
    }

    @Bean
    public BoardService boardService() {
        return new BoardService(boardRepository());
    }

    @Bean
    public BoardRepository boardRepository() {
        return new JdbcBoardRepository(dataSource);
    }
}
