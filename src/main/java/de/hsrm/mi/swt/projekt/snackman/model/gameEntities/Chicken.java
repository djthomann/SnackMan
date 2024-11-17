package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Chicken` class represents a non-player character in the game with
 * an initial position on a plane.
 * 
 * 
 */
public class Chicken implements Moveable, CanEat {
    
    /** Counter for the unique identifier for the chicken */
    private static int counter = 0;

    /** The unique identifier for the chicken */
    private final int id;

    /** The x-coordinate of the chicken */
    private float x;

    /** The y-coordinate of the chicken */
    private float y;

    /** The calorie count of the chicken */
    private int gainedCalories;

    /**
     * Constructs a new Chicken with the specified starting position and
     * initial calorie count.
     * 
     * @param x        the initial x-coordinate of the Chicken
     * @param y        the initial y-coordinate of the Chicken       
     * @param gainedCalories  the initial number of gainedCalories by the Chicken
     */
    public Chicken(float x, float y) {
        this.id = ++counter; 
        this.x = x; 
        this.y = y; 
        this.gainedCalories = 0;
    }

    /**
     * Moves chicken to new coords
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
    
    /**
     * Returns the number of gainedCalories.
     * 
     * @return the current calories
     */
    public int getGainedCalories() {
        return gainedCalories;
    }

    /**
     * Reset the number of gainedCalories auf 0.
     * 
     */
    public void resetGainedCalories() {
        this.gainedCalories = 0;
    }

    /**
     * Returns the unique identifier of the chicken.
     * 
     * @return the `id` of the chicken
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the chicken.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the chicken.
     * 
     * @param newX the new x-coordinate to set
     */
    public void setX(float newX) {
        this.x = newX;
    }

    /**
     * Returns the y-coordinate of the chicken.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the chicken.
     * 
     * @param newY the new y-coordinate to set
     */
    public void setY(float newY) {
        this.y = newY;
    }

    /*      
    @Override
    public void handle(Event event) {
    } */
}
