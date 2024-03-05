package com.example.board.service;

import com.example.board.domain.member.LoginDto;
import com.example.board.domain.member.Member;
import com.example.board.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean join(Member member) {

        Optional<Member> findMember = memberRepository.getMemberById(member.getId());

        // 아이디가 중복이면 false 리턴
        if (findMember.isEmpty() == false) {
            return false;
        }

        log.info("join member");
        memberRepository.join(member);
        return true;
    }


    public Member login(LoginDto loginDto) {
        Optional<Member> findMember = memberRepository.getMemberById(loginDto.getId());

        //아이디가 존재하지 않으면
        if (findMember.isEmpty()) {
            log.info("findMember.isEmpty()");
            return null;
        } else if (findMember.get().getPass().equals(loginDto.getPass())) {  //비밀번호 비교
            return findMember.get();
        }
        return null;
    }

}
