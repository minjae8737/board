package com.example.board.domain.board;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


/*
create table board
     (
         id varchar(20) not null,
         name varchar(20) not null,
         primary key (id),
     );
*/

@Data
@Entity
public class Board {

    @Id
    private String id; //영문이니셜 url 용도
    private String name; //한글

    public Board() {
    }
}
