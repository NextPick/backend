package com.nextPick.eventListener;

import static com.nextPick.eventListener.EventCaseEnum.*;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CustomEvent extends ApplicationEvent {
    private EventCase methodName;
    private String description;
    private String type;

    public CustomEvent(Object source, EventCase methodName,String description, String type) {
        super(source);
        this.methodName = methodName;
        this.description = description;
        this.type = type;
    }
}
