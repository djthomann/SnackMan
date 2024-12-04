package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;

public class EatEvent extends ApplicationEvent {


    private Food food;
    private long gameId;

    /**
     * Event that is called upon when a ghost collides with a snackman.
     * @param source the object on which the event occurred => object that eats
     * @param food the food that was eaten.
     * @param gameId id of the game the event belongs to.
     */
    public EatEvent(Object source, Food food, long id) {
        super(source);
        this.food = food;
        this.gameId = id;
    }

    public Food getFood() {
        return food;
    }

    public long getGameId() {
        return gameId;
    }

}