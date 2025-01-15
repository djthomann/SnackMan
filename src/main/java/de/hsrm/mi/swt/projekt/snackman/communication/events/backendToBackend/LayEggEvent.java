package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;

public class LayEggEvent extends ApplicationEvent {


    private Food food;
    private long gameId;

    /**
     * Event that is called upon when a chicken lays an egg.
     * @param source the object on which the event occurred initially or an object with which the event is associated
     * @param food the food that was eaten.
     * @param id id of the game the event belongs to.
     */
    public LayEggEvent(Object source, Food food, long id) {
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