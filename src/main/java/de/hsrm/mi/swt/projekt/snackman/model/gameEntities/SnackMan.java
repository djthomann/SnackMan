package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `SnackMan` class represents a player character in the game who has an
 * initial position on a plane and has a calorie count.
 * 
 * This class extends `PlayerObject` and adds the ability to track and modify
 * the calorie count of the `SnackMan`.
 * 
 * 
 * @author Mohamad Hajjar
 * 
 */
public class SnackMan extends PlayerObject implements CanEat {

    /** The calorie count of the SnackMan */
    private int calories;

    /**
     * Constructs a new `SnackMan` with the specified starting position and
     * initial calorie count.
     * 
     * @param x        the initial x-coordinate of the `SnackMan`
     * @param y        the initial y-coordinate of the `SnackMan`       
     * @param calories the initial number of calories of the `SnackMan`
     */
    public SnackMan(int x, int y, int calories) {
        super(x, y);
        this.calories = calories;
    }

    /**
     * Returns the current calorie count of the `SnackMan`.
     * 
     * @return the current calorie count
     */
    public int getCalories() {
        return calories;
    }

    /**
     * Sets a new calorie count for the `SnackMan`.
     * 
     * @param calories the new calorie count to set
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }

    /**
     * moves SnackMan to new coords
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    @Override
    public void move(int x, int y) {
    }

    /**
     * Consumes the food, for example to be able ready to jump
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