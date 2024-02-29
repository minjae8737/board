package com.example.board.repository.member;

import com.example.board.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JDBCMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate template;

    @Override
    public void join(Member member) {
        String sql = "insert into member (id, pass, nickname) " +
                "values (:id, :pass, :nickname)";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", member.getId())
                .addValue("pass", member.getPass())
                .addValue("nickname", member.getNickName());

        template.update(sql, param);
    }

    @Override
    public Optional<Member> getMemberById(String id) {
        String sql = "select id, pass, nickname " +
                "from member " +
                "where id = :id";

        try {
            SqlParameterSource param = new MapSqlParameterSource()
                    .addValue("id", id);
            Member member = template.queryForObject(sql, param, memberRowMapper());

            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<Member> memberRowMapper() {
        return BeanPropertyRowMapper.newInstance(Member.class);
    }
}

