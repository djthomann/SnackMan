package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import org.springframework.context.ApplicationEvent;

public class InternalMoveEvent extends ApplicationEvent {

    private final GameManager gameManager;

    /**
     * Event that is called upon when an object moves.
     * @param source the object on which the event occurs => the object that moves.
     * * @param gameId objectId of the game the event belongs to.
     */
    public InternalMoveEvent(Object source, GameManager gameManager) {
        super(source);
        this.gameManager = gameManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}