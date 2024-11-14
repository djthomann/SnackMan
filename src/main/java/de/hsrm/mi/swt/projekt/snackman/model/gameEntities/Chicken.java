package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Chicken` class represents a non-player character in the game with
 * an initial position on a plane.
 * 
 * This class extends `NonPlayerObject` and inherits its properties for
 * coordinates management.
 * 
 * 
 */
public class Chicken extends NonPlayerObject implements Moveable, CanEat {

    private int gainedCalories;

    /**
     * Constructs a new Chicken with the specified starting position and
     * initial calorie count.
     * 
     * @param x        the initial x-coordinate of the Chicken
     * @param y        the initial y-coordinate of the Chicken       
     * @param gainedCalories  the initial number of calories of the Chicken
     */
    public Chicken(float x, float y, int gainedCalories) {
        super(x, y);
        this.gainedCalories = gainedCalories;
    }

     /**
     * Returns the number of calories of the food.
     * 
     * @return the current calories
     */
    public int getGainedCalories() {
        return gainedCalories;
    }

    /**
     * Sets the number of calories of the food.
     * 
     * @param gainedCalories the new calories to set
     */
    public void setGainedCalories(int gainedCalories) {
        this.gainedCalories = gainedCalories;
    }

    /**
     * moves chicken to new coords
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    @Override
    public void move(float x, float y) {
    }
    
    /**
     * Consumes the food, affecting the weight (calories) of the entity
     *
     * @param food the calorie resource to be consumed by the entity
     */
    @Override
    public void eat(Food food) {
    } 
    
    /*      
    @Override
    public void handle(Event event) {
    } */
    
}
