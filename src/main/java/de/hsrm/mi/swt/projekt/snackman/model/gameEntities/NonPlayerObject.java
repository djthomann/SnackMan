package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `NonPlayerObject` class represents a NPC object in a game with an `id`
 * and coordinates (`x`, `y`) on a plane.
 * 
 * This class provides methods to get and set the coordinates of the non-player object.
 * 
 * 
 * @author Mohamad Hajjar
 */
public abstract class NonPlayerObject {

    /** The unique identifier for the non-player object */
    private int id;

    /** The x-coordinate of the non-player object */
    private float x;

    /** The y-coordinate of the non-player object */
    private float y;

    /** The calorie count of the non-player object */
    private int calories;

    /**
     * Constructs a new `PlayerObject` with the specified starting coordinates.
     * 
     * @param startX the initial x-coordinate of the non-player object
     * @param startY the initial y-coordinate of the non-player object
     * @param calories the initial number of calories of non-player object
     */
    public NonPlayerObject(float x, float y, int calories) {
        this.x = x;
        this.y = y;
        this.calories = calories;
    }

    /**
     * Returns the unique identifier of the non-player object.
     * 
     * @return the `id` of the non-player object
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the non-player object.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the non-player object.
     * 
     * @param newX the new x-coordinate to set
     */
    public void setX(float newX) {
        this.x = newX;
    }

    /**
     * Returns the y-coordinate of the non-player object.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the non-player object.
     * 
     * @param newY the new y-coordinate to set
     */
    public void setY(float newY) {
        this.y = newY;
    }

    /**
     * Returns the number of calories of the non-player object.
     * 
     * @return the current calories
     */
    public int getCalories() {
        return calories;
    }

    /**
     * Sets the number of calories of the non-player object.
     * 
     * @param newY the new calories to set
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }

}
