package com.nextPick.solves.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.questionCategory.repository.QuestionCategoryRepository;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.solves.entity.Solves;
import com.nextPick.solves.repository.SolvesRepository;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SolvesService extends ExtractMemberAndVerify {
    private final SolvesRepository solvesRepository;
    private final MemberRepository memberRepository;
    private final QuestionCategoryRepository questionCategoryRepository;

    public long createOrUpdateSolves(QuestionList questionList, Member member,
                                     boolean correct, String myAnswer) {

        Solves solves = solvesRepository.findByMemberAndQuestionList(member, questionList)
                .orElse(new Solves());
        solves.setQuestionList(questionList);
        solves.setMember(member);
        solves.setCorrect(correct);
        solves.setMyAnswer(myAnswer);
        Solves savedSolves = solvesRepository.save(solves);
        return savedSolves.getSolvesId();
    }

    public Solves getSolves(long solvesId) {
        Solves findSolves = solvesRepository.findById(solvesId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.SOLVE_NOT_FOUND));
        return findSolves;
    }



    public List<Solves> getSolveList(List<Long> solvesIds) {
        Member member = extractMemberFromPrincipal(memberRepository);
        List<Solves> findSolveList = solvesRepository.findAllBySolvesIdAndMember(solvesIds,member);
        return findSolveList;
    }



    public Page<Solves> getSolvesPage(int page, int size, Long questionCategoryId,
                                                String keyword, String sort, boolean correct) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Sort sortBy;
        Pageable pageable;
        if(questionCategoryId == -1)
            questionCategoryId = null;
        else
            questionCategoryRepository.findById(questionCategoryId)
                    .orElseThrow(()-> new BusinessLogicException(ExceptionCode.QUESTION_CATEGORY_NOT_FOUND));
        switch (sort) {
            case "correct_percent_asc":
                sortBy = Sort.by("questionList.correctRate").ascending();
                break;
            case "correct_percent_desc":
                sortBy = Sort.by("questionList.correctRate").descending();
                break;
            case "recent":
                sortBy = Sort.by("solvesId").descending();
                break;
            default:
                throw new IllegalArgumentException("Invalid sort type: " + sort);
        }
        pageable = PageRequest.of(page, size, sortBy);
        return solvesRepository.findByManyFilterAndSortCorrectPercent(questionCategoryId,keyword,correct,member,pageable);
    }
}
