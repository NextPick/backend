package com.nextPick.statistics.repository;

import com.nextPick.statistics.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    Optional<Statistics> findByDescription(String description);

    List<Statistics> findAllByType(Statistics.StatisticsType type);
}

