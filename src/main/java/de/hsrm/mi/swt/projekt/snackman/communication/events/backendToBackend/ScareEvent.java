package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class ScareEvent extends ApplicationEvent {

    private final GameManager gameManager;
    private final SnackMan scaredSnackMan;
    private final long gameId;

    /**
     * Event that is called upon when a ghost collides with a snackman.
     * @param source the object on which the event occurred initially or an object with which the event is associated
     * @param scaredSnackMan the snackman that collided with the ghost.
     * * @param gameId objectId of the game the event belongs to.
     */
    public ScareEvent(Object source, SnackMan scaredSnackMan, long id, GameManager gameManager) {
        super(source);
        this.scaredSnackMan = scaredSnackMan;
        this.gameId = id;
        this.gameManager = gameManager;
    }

    public long getGameId() {
        return gameId;
    }

    public SnackMan getScaredSnackMan() {
        return scaredSnackMan;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

}
