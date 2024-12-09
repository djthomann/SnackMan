package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

public class InternalMoveEvent extends ApplicationEvent {

    private long gameId;

    /**
     * Event that is called upon when an object moves.
     * @param source the object on which the event occurrs => the object that moves.
     * * @param gameId id of the game the event belongs to.
     */
    public InternalMoveEvent(Object source, long id) {
        super(source);
        this.gameId = id;
    }

    public long getGameId() {
        return gameId;
    }
}