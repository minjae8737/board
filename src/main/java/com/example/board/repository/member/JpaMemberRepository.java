package com.example.board.repository.member;

import com.example.board.domain.member.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class JpaMemberRepository implements MemberRepository{

    private final EntityManager em;

    @Override
    public void join(Member member) {
        em.persist(member);
    }

    @Override
    public Optional<Member> getMemberById(String id) {
        Member findMember = em.find(Member.class, id);
        return Optional.ofNullable(findMember);
    }

}
