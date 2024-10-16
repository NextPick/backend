package com.nextPick.statistics.controller;

import com.nextPick.dto.SingleResponseDto;
import com.nextPick.statistics.entity.Statistics;
import com.nextPick.statistics.mapper.StatisticsMapper;
import com.nextPick.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
@Validated
public class StatisticsController {
    private final static String STATISTICS_DEFAULT_URL = "/statistics";
    private final StatisticsService service;
    private final StatisticsMapper mapper;

    @GetMapping("/question")
    public ResponseEntity getStatisticsFridge(){
        List<Statistics> statisticsList = service.findAllStatisticsByType();
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.statisticsToStatisticsResponseDto(statisticsList)), HttpStatus.OK);
    }

}
