package com.example.board.repository.member;

import com.example.board.domain.member.Member;

import java.util.Optional;

public interface MemberRepository {

    public void join(Member member);

    public Optional<Member> getMemberById(String id);
}
