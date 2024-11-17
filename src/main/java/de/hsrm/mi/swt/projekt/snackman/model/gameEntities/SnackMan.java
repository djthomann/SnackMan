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
     * @param id       the unique identifier of the `SnackMan`
     * @param x        the initial x-coordinate of the `SnackMan`
     * @param y        the initial y-coordinate of the `SnackMan`       
     */
    public SnackMan(int id, float x, float y) {
        super(id, x, y);
        this.gainedCalories = 0;
    }

    /**
     * Resets the gainedCalories for the `SnackMan` to 0.
     * 
     */
    public void resetGainedCalories() {
        this.gainedCalories = 0;
    }
    
    /**
     * moves SnackMan to new coords
     * 
     * @param newX the x-coordinate to move the `SnackMan` to
     * @param newY the y-coordinate to move the `SnackMan` to
     */
    @Override
    public void move(float newX, float newY) {
    }

    /**
     * method to Consume Food
     *
     * @param food the calorie resource to be consumed by the `SnackMan`. 
     */
    @Override
    public void eat(Food food) {
    }

    /**
     * Returns the current gainedCalories count of the `SnackMan`.
     * 
     * @return the current gainedCalories count
     */
    public int getGainedCalories() {
        return gainedCalories;
    }

    /*
    @Override
    public void handle(Event event) {}
    */

}
