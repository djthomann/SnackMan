package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

public class InternalMoveEvent extends ApplicationEvent {

    /**
     * Event that is called upon when a ghost collides with a snackman.
     * @param source the object on which the event occurrs => the object that moves
     */
    public InternalMoveEvent(Object source) {
        super(source);
    }
}