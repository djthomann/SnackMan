package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `SnackMan` class represents a player character in the game who has an
 * initial position on a plane and has a calorie count.
 * 
 * This class extends `PlayerObject` and adds the ability to track and modify
 * the calorie count of the `SnackMan`.
 * 
 * 
 */
public class SnackMan extends PlayerObject implements CanEat {

    /** The calorie count of the SnackMan */
    private int gainedCalories;

    /**
     * Constructs a new `SnackMan` with the specified starting position and
     * initial calorie count.
     * 
     * @param x        the initial x-coordinate of the `SnackMan`
     * @param y        the initial y-coordinate of the `SnackMan`       
     * @param gainedCalories the initial number of gainedCalories of the `SnackMan`
     */
    public SnackMan(float x, float y, int gainedCalories) {
        super(x, y);
        this.gainedCalories = gainedCalories;
    }

    /**
     * Returns the current gained calorie count of the `SnackMan`.
     * 
     * @return the current gained calorie count
     */
    public int getGainedCalories() {
        return gainedCalories;
    }

    /**
     * Resets the  gainedCalories count for the `SnackMan` to 0.
     * 
     * @param gainedCalories the new calorie count to set
     */
    public void resetGainedCalories(int gainedCalories) {
        this.gainedCalories = 0;
    }

    /**
     * moves SnackMan to new coords
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    @Override
    public void move(float x, float y) {
    }

    /**
     * method to Consume Food
     *
     * @param food the calorie resource to be consumed by the entity
     */
    @Override
    public void eat(Food food) {
    } 

    // /**
    //  * Handles a specified event (e.g. jump event, move event).
    //  *
    //  * @param event the event to handle
    //  */
/*
    @Override
    public void handle(Event event) {
    }
 */

}