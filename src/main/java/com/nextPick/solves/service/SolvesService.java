package com.nextPick.solves.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
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

@Service
@Transactional
@RequiredArgsConstructor
public class SolvesService extends ExtractMemberAndVerify {
    private final SolvesRepository solvesRepository;
    private final MemberRepository memberRepository;

    public void createOrUpdateSolves(QuestionList questionList, Member member,
                                     boolean correct, String myAnswer) {

        Solves solves = solvesRepository.findByMemberAndQuestionList(member, questionList)
                .orElse(new Solves());
        solves.setQuestionList(questionList);
        solves.setMember(member);
        solves.setCorrect(correct);
        solves.setMyAnswer(myAnswer);
        solvesRepository.save(solves);
    }

    public Solves getSolves(long solvesId) {
        Solves findSolves = solvesRepository.findById(solvesId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.SOLVE_NOT_FOUND));
        return findSolves;
    }



    public Page<Solves> getSolvesPage(int page, int size, long questionCategoryId,
                                                String keyword, String sort, boolean correct) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Sort sortBy;
        Pageable pageable;
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
