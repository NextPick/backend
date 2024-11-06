package com.nextPick.statistics.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistics_id")
    private Long id;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private StatisticsType type = StatisticsType.NONE;

    @Column(name = "description")
    private String description;

    @Column(name = "count")
    private int count;

    @Getter
    public enum StatisticsType {
        NONE("미정"),
        Q_BE("backend 문제"),
        Q_FE("frontend 문제"),
        Q_CS("computer science 문제");

        private final String status;

        StatisticsType(String status) {
            this.status = status;
        }
    }
}
