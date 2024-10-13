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

//    @PatchMapping("/{menu-id}")
//    public ResponseEntity patchStatistics(@PathVariable("menu-id") @Positive long menuId,
//                                          Authentication authentication) {
//        boolean result = service.increaseCookCount(menuId,authentication);
//        if(result)
//            return new ResponseEntity(HttpStatus.OK);
//        else
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//    }

    @GetMapping("/{type}")
    public ResponseEntity getStatisticsFridge(@PathVariable("type") String type){
        List<Statistics> statisticsList = service.findAllStatisticsByType(type);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.statisticsToStatisticsResponseDto(statisticsList)), HttpStatus.OK);
    }

}
