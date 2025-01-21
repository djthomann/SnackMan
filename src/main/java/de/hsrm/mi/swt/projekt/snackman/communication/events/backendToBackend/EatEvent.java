package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;

public class EatEvent extends ApplicationEvent {

    private final GameManager gameManager;
    private final Food food;
    private final long gameId;

    /**
     * Event that is called upon when a snackman collides with food.
     * @param source the object on which the event occurred => object that eats
     * @param food the food that was eaten.
     * @param id id of the game the event belongs to.
     */
    public EatEvent(Object source, Food food, long id, GameManager gameManager) {
        super(source);
        this.food = food;
        this.gameId = id;
        this.gameManager = gameManager;
    }

    public Food getFood() {
        return food;
    }

    public long getGameId() {
        return gameId;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

}