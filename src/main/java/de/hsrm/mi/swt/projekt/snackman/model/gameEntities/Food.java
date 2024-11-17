package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Food` class represents a non-player Object in the game with
 * an initial position on a plane.
 * 
 * 
 */
public class Food {
    
    /** Counter for the unique identifier for the food object */
    private static int counter = 0;

    /** The unique identifier for the food */
    private final int id;

    /** The x-coordinate of the food */
    private float x;

    /** The y-coordinate of the food */
    private float y;

    /** The calorie count of the food */
    private int calories;

    /**
     * Constructs a new Food with the specified position and
     * initial calorie count.
     * 
     * @param x        the initial x-coordinate of the Food
     * @param y        the initial y-coordinate of the Food       
     * @param calories  the initial number of calories of the Food
     */
    public Food(float x, float y, int calories) {
        this.id = ++counter;
        this.x = x; 
        this.y = y; 
        this.calories = calories;
    }       

    /**
     * Returns the number of calories of the food.
     * 
     * @return the current calories
     */
    public int getCalories() {
        return calories;
    }
    
    /**
     * Returns the unique identifier of the food.
     * 
     * @return the `id` of the game object
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the food.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the food.
     * 
     * @param newX the new x-coordinate to set
     */
    public void setX(float newX) {
        this.x = newX;
    }

    /**
     * Returns the y-coordinate of the food.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the food.
     * 
     * @param newY the new y-coordinate to set
     */
    public void setY(float newY) {
        this.y = newY;
    }
}
