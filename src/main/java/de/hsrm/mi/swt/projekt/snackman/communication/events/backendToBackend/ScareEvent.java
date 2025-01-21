package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;

public class ScareEvent extends ApplicationEvent {

    private final GameManager gameManager;
    private final long gameId;

    /**
     * Event that is called upon when a ghost collides with a snackman.
     * @param source the object on which the event occurred initially or an object with which the event is associated (Object that got scared)
     * * @param gameId objectId of the game the event belongs to.
     */
    public ScareEvent(Object source, long id, GameManager gameManager) {
        super(source);
        this.gameId = id;
        this.gameManager = gameManager;
    }

    public long getGameId() {
        return gameId;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

}
