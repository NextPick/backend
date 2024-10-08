package com.nextPick.report.controller;


import com.nextPick.dto.MultiResponseDto;
import com.nextPick.dto.SingleResponseDto;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.report.dto.ReportDto;
import com.nextPick.report.entity.Report;
import com.nextPick.report.mapper.ReportMapper;
import com.nextPick.report.service.ReportService;
import com.nextPick.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final static String REPORTS_DEFAULT_URL = "/reports";
    private final ReportMapper reportMapper;
    private final ReportService service;

    @PostMapping
    public ResponseEntity userReport(@Valid @RequestBody ReportDto.Post requestBody) {
        Report report = reportMapper.reportPostDtoToReport(requestBody);
        service.createReport(report);
        URI location = UriCreator.createUri(REPORTS_DEFAULT_URL, report.getReportId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{report-id}")
    public ResponseEntity getReport(@PathVariable("report-id") @Positive long reportId) {
        Report report = service.getReport(reportId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(reportMapper.reportToReportResponseDto(report)), HttpStatus.OK);
    }

    @DeleteMapping("/{report-id}")
    public ResponseEntity deleteReport(@PathVariable("report-id") @Positive long reportId) {
        service.deleteReport(reportId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity getQuestionLists(@Positive @RequestParam int page,
                                           @Positive @RequestParam int size,
                                           @RequestParam(name = "type") String type){
        Page<Report> pageReport =
                service.getReportPage(page - 1, size, type);
        List<Report> ReportLists = pageReport.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(reportMapper.reportListToReportResponsesDto(ReportLists), pageReport),
                HttpStatus.OK);
    }
}
