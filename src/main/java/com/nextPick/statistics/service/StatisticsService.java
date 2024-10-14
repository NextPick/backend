package com.nextPick.statistics.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.statistics.entity.Statistics;
import com.nextPick.statistics.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsRepository statisticsRepository;

    public void countChange(String string,String status){
        Statistics statistics = statisticsRepository.findByDescription(string)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.STATISTICS_NOT_FOUND));
        switch (status) {
            case "add":
                statistics.setCount(statistics.getCount()+1); break;
            case "minus":
                statistics.setCount(statistics.getCount()-1); break;
            case "clear":
                statistics.setCount(0); break;
        }
        statisticsRepository.save(statistics);
    }

    public List<Statistics> findAllStatisticsByType(String type) {
        return statisticsRepository.findByType(type);
    }

}
