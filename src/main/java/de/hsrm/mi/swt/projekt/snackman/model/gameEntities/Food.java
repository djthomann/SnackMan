package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Food` class represents a NP Object in the game.
 * 
 * 
 */
public class Food {

    /** The unique identifier for the Food */
    private final long id;

    /** The x-coordinate of the Food */
    private float x;

    /** The y-coordinate of the Food */
    private float y;

    /** The calorie count contained in the Food */
    private int calories;

    /**
     * Constructs a new Food with the specified position and
     * initial calorie count.
     * 
     * @param id        the unique identifier of the Food
     * @param x         the initial x-coordinate of the Food
     * @param y         the initial y-coordinate of the Food       
     * @param calories  the initial number of calories of the Food
     */
    public Food(long id, float x, float y, int calories) {
        this.id = id;
        this.x = x; 
        this.y = y; 
        this.calories = calories;
    }       
    
    /**
     * Returns the unique identifier of the Food.
     * 
     * @return the `id` of the Food
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the Food.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the Food.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the number of contained Calories in the Food.
     * 
     * @return the current contained calories
     */
    public int getCalories() {
        return calories;
    }

}
