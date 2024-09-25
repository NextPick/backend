package com.nextPick.eventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceEventListener {

    @EventListener
    public void handleMyCustomEvent(CustomEvent event) {
        switch (event.getMethodName()) {
            case EVENT_CASE_1:
                log.debug("Event : EVENT_CASE_1");
                break;
            case EVENT_CASE_2:
                log.debug("Event : EVENT_CASE_2");
                break;
            case EVENT_CASE_3:
                log.debug("Event : EVENT_CASE_3");
                break;
            case EVENT_CASE_4:
                log.debug("Event : EVENT_CASE_4");
                break;
        }
    }
}
