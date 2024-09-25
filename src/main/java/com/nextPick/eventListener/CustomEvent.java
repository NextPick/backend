package com.nextPick.eventListener;

import static com.nextPick.eventListener.EventCaseEnum.*;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CustomEvent extends ApplicationEvent {
    private EventCase methodName;
    private String description;
    private int count;

    public CustomEvent(Object source, EventCase methodName,String description, int count) {
        super(source);
        this.methodName = methodName;
        this.description = description;
        this.count = count;
    }
}
