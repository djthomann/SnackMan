package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The CanEat interface represents an entity capable of consuming calories.
 * Classes implementing this interface can perform an action to "eat" food and gain calories.
 * 
 */

public interface CanEat {

    /**
     * Consumes the food, potentially affecting the state & attributes of the entity
     *
     * @param food the calorie resource to be consumed by the entity
     */
    void eat(Food food); 
}
