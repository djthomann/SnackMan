package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;

/**
 * The `Chicken` class represents a NPC in the game.
 * 
 * 
 */
public class Chicken extends GameObject implements CanEat, MovableAndSubscribable {

    /** The gainedCalorie count of the Chicken */
    private int gainedCalories;

    /**
     * Constructs a new Chicken with the id, and specified starting Coords.
     *
     * @param x        the initial x-coordinate of the Chicken
     * @param y        the initial y-coordinate of the Chicken       
     * @param z        the initial z-coordinate of the Chicken       
     */
    public Chicken(float x, float y, float z) {
        super(x, y, z);
        this.gainedCalories = 0;
    }

    /**
     * Moves chicken to new coords
     * 
     * @param newX the x-coordinate to move the Chicken to
     * @param newY the y-coordinate to move the Chicken to
     * @param newZ the z-coordinate (remains the same)
     */
    @Override
    public void move(float newX, float newY, float newZ) {
        x = newX; 
        y = newY; 
        z = newZ; 
    }

    
    /**
     * Consumes the food, make the Chicken gain Calories.
     *
     * @param food the calorie resource to be consumed by the entity
     */
    @Override
    public void eat(Food food) {
    } 

    /**
     * Resets the gainedCalories for the Chicken to 0.
     * 
     */
    public void resetGainedCalories() {
        this.gainedCalories = 0;
    }    
    
    /**
     * Returns the number of gainedCalories.
     * 
     * @return the current gainedCalories
     */
    public int getGainedCalories() {
        return gainedCalories;
    }

    @Override
    public void handle(Event event) {
    }

}
