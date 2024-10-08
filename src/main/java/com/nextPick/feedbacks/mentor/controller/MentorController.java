package com.nextPick.feedbacks.mentor.controller;


import com.nextPick.dto.MultiResponseDto;
import com.nextPick.feedbacks.mentor.dto.MentorDto;
import com.nextPick.feedbacks.mentor.entity.Mentor;
import com.nextPick.feedbacks.mentor.mapper.MentorMapper;
import com.nextPick.feedbacks.mentor.service.MentorService;
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
@RequestMapping("/mentor/feedback")
@RequiredArgsConstructor
public class MentorController {
    private final static String MENTOR_DEFAULT_URL = "/mentor/feedback";
    private final MentorMapper mentorMapper;
    private final MentorService service;

    @PostMapping("/{room-Id}/{mentor-id}")
    public ResponseEntity createMentor(@PathVariable("room-id") @Positive long roomId,
                                          @PathVariable("mentor-id") @Positive long mentorId,
                                          @Valid @RequestBody MentorDto.Post requestBody) {
        Mentor mentor = mentorMapper.mentorPostDtoToMentor(requestBody);
        service.createMentor(mentor,roomId,mentorId);
        URI location = UriCreator.createUri(MENTOR_DEFAULT_URL, mentor.getMentorFeedbackId());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{mentor-feedback-id}")
    public ResponseEntity deleteReport(@PathVariable("mentor-feedback-id") @Positive long mentorFeedbackId) {
        service.deleteMentorForAdmin(mentorFeedbackId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity getQuestionLists(@Positive @RequestParam int page,
                                           @Positive @RequestParam int size){
        Page<Mentor> pageMentor =
                service.getMentorPage(page - 1, size,false);
        List<Mentor> MentorList = pageMentor.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(mentorMapper.mentorListToMentorResponsesDto(MentorList), pageMentor),
                HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity getQuestionListsForAdmin(@Positive @RequestParam int page,
                                                   @Positive @RequestParam int size){
        Page<Mentor> pageMentor =
                service.getMentorPage(page - 1, size,true);
        List<Mentor> MentorList = pageMentor.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(mentorMapper.mentorListToMentorResponsesDtoForAdmin(MentorList), pageMentor),
                HttpStatus.OK);
    }
}
