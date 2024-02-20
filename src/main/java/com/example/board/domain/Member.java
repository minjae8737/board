package com.example.board.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member {

    private String id; //아이디
    private String pass; //비밀번호
    private String nickName; //닉네임

}
