package com.nextPick.synonyms.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Synonyms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long SynonymsId;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "mean", nullable = false)
    private String mean;
}
