package com.nextPick.eventListener;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EventCaseEnum {
    @Getter
    @AllArgsConstructor
    public enum EventCase{
        STATISTICS_COUNT_CHANGE(1,"StatisticsCountChange"),
        EVENT_CASE_2(2,"EVENT_CASE_2"),
        EVENT_CASE_3(3, "EVENT_CASE_3"),
        EVENT_CASE_4(4,"EVENT_CASE_4");

        private int statusNumber;

        private String statusDescription;
    }
}
