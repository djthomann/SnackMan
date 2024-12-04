package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class ScareEvent extends ApplicationEvent {

    private SnackMan scaredSnackMan;
    private long gameId;

    /**
     * Event that is called upon when a ghost collides with a snackman.
     * @param source the object on which the event occurred initially or an object with which the event is associated
     * @param scaredSnackMan the snackman that collided with the ghost.
     * * @param gameId id of the game the event belongs to.
     */
    public ScareEvent(Object source, SnackMan scaredSnackMan, long id) {
        super(source);
        this.scaredSnackMan = scaredSnackMan;
        this.gameId = id;
    }

    public long getGameId() {
        return gameId;
    }

    public SnackMan getScaredSnackMan() {
        return scaredSnackMan;
    }

}
