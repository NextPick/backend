//package com.nextPick.keyword.entity;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.nextPick.questionCategory.entity.QuestionCategory;
//import com.nextPick.questionList.entity.QuestionList;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@Entity
//public class Keyword {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long keywordId;
//
//    @Column(name = "word", nullable = false)
//    private String word;
//
//    @Column(name = "word_explain", nullable = true)
//    private String wordExplain;
//
////    @ManyToOne
////    @JoinColumn(name = "question_list_id")
////    @JsonManagedReference
////    private QuestionList questionList;
//}
