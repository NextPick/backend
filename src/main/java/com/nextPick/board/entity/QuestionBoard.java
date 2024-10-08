package com.nextPick.board.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Q")  // 면접 질문 게시판
@Getter
@Setter
public class QuestionBoard extends Board {

}
