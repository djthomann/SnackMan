package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

@Service
public class EventService implements ApplicationEventPublisherAware {

    private static EventService instance;

    ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public EventService() {
        instance = this; 
    }

    public static EventService getInstance() {
        return instance;
    }

}
