package com.nextPick.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class MultiResponseDto<T> {
    private List<T> data;
    private PageInfo pageInfo;

    public MultiResponseDto(List<T> data, Page page) {
        this.data = data;
        if (page != null) {
            this.pageInfo = new PageInfo(page.getNumber() + 1,
                    page.getSize(), page.getTotalElements(), page.getTotalPages());
        } else {
            this.pageInfo = null; // 페이지네이션이 없으면 pageInfo를 null로 설정
        }

    }
}
