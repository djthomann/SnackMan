package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.springframework.context.ApplicationEvent;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;

public class EatEvent extends ApplicationEvent {

    private Food food;

    /**
     * Event that is called upon when a ghost collides with a snackman.
     * @param source the object on which the event occurred => object that eats
     * @param food the food that was eaten
     */
    public EatEvent(Object source, Food food) {
        super(source);
        this.food = food;
    }
}