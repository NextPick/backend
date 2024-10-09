package com.nextPick.feedbacks.interview.contorller;


import com.nextPick.dto.MultiResponseDto;
import com.nextPick.feedbacks.interview.dto.InterviewDto;
import com.nextPick.feedbacks.interview.entity.Interview;
import com.nextPick.feedbacks.interview.mapper.InterviewMapper;
import com.nextPick.feedbacks.interview.service.InterviewService;
import com.nextPick.report.entity.Report;
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
@RequestMapping("/mentee/feedback")
@RequiredArgsConstructor
public class InterviewController {
    private final static String INTERVIEW_DEFAULT_URL = "/mentee/feedback";
    private final InterviewMapper interviewMapper;
    private final InterviewService service;

    @PostMapping("/{room-id}/{mentee-id}")
    public ResponseEntity createInterview(@PathVariable("room-id") @Positive long roomId,
                                          @PathVariable("mentee-id") @Positive long menteeId,
                                          @Valid @RequestBody InterviewDto.Post requestBody) {
        Interview interview = interviewMapper.interviewPostDtoToInterview(requestBody);
        service.createInterview(interview,roomId,menteeId);
        URI location = UriCreator.createUri(INTERVIEW_DEFAULT_URL, interview.getInterviewFeedbackId());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{interview-feedback-id}")
    public ResponseEntity deleteReport(@PathVariable("interview-feedback-id") @Positive long interviewFeedbackId) {
        service.deleteInterviewForAdmin(interviewFeedbackId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity getQuestionLists(@Positive @RequestParam int page,
                                           @Positive @RequestParam int size){
        Page<Interview> pageInterview =
                service.getInterviewPage(page - 1, size,false);
        List<Interview> InterviewList = pageInterview.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(interviewMapper.interviewListToInterviewResponsesDto(InterviewList), pageInterview),
                HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity getQuestionListsForAdmin(@Positive @RequestParam int page,
                                                   @Positive @RequestParam int size){
        Page<Interview> pageInterview =
                service.getInterviewPage(page - 1, size,true);
        List<Interview> InterviewList = pageInterview.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(interviewMapper.interviewListToInterviewResponsesDtoForAdmin(InterviewList), pageInterview),
                HttpStatus.OK);
    }
}
