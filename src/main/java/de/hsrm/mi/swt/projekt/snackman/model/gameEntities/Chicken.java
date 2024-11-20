package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Chicken` class represents a NPC in the game.
 * 
 * 
 */
public class Chicken implements Moveable, CanEat, Subscribable {

    /** The unique identifier for the Chicken */
    private final int id;

    /** The x-coordinate of the Chicken */
    private float x;

    /** The y-coordinate of the Chicken */
    private float y;

    /** The gainedCalorie count of the Chicken */
    private int gainedCalories;

    /**
     * Constructs a new Chicken with the id, and specified starting Coords.
     * 
     * @param id       the unique identifier of the Chicken
     * @param x        the initial x-coordinate of the Chicken
     * @param y        the initial y-coordinate of the Chicken       
     */
    public Chicken(int id,float x, float y) {
        this.id = id; 
        this.x = x; 
        this.y = y; 
        this.gainedCalories = 0;
    }

    /**
     * Moves chicken to new coords
     * 
     * @param newX the x-coordinate to move the Chicken to
     * @param newY the y-coordinate to move the Chicken to
     */
    @Override
    public void move(float newX, float newY) {
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

    /**
     * Returns the unique identifier of the Chicken.
     * 
     * @return the `id` of the Chicken
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the Chicken.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the Chicken.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }

    // TODO implement handle, change event type
    @Override
    public void handle(String type, int objectId) {
    }

}
