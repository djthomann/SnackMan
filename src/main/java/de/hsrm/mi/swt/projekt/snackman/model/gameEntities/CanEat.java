package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The CanEat interface represents an entity capable of consuming calories.
 * Classes implementing this interface can perform an action to "eat" food and gain calories.
 * 
 * 
 */
public interface CanEat {

    void eat(Food food);
     
}
