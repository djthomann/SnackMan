package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class ScareEvent extends ApplicationEvent {

    private SnackMan scaredSnackMan;

    /**
     * Event that is called upon when a ghost collides with a snackman.
     * @param source the object on which the event occurred initially or an object with which the event is associated
     * @param scaredSnackMan the snackman that collided with the ghost
     */
    public ScareEvent(Object source, SnackMan scaredSnackMan) {
        super(source);
        this.scaredSnackMan = scaredSnackMan;
    }
}
