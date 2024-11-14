package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `PlayerObject` class represents an moveable object in a game with an `id`
 * and coordinates (`x`, `y`) on a plane. 
 * 
 * This class provides methods to get and set the coordinates of the player object.
 * 
 * 
 * @author Mohamad Hajjar
 */
public abstract class PlayerObject implements Moveable {

    /** The unique identifier for the player object */
    private int id;

    /** The x-coordinate of the player object */
    private float x;

    /** The y-coordinate of the player object */
    private float y;

    /**
     * Constructs a new `PlayerObject` with the specified starting coordinates.
     * 
     * @param startX the initial x-coordinate of the player object
     * @param startY the initial y-coordinate of the player object
     */
    public PlayerObject(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the unique identifier of the player object.
     * 
     * @return the `id` of the player object
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the player object.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the player object.
     * 
     * @param newX the new x-coordinate to set
     */
    public void setX(float newX) {
        this.x = newX;
    }

    /**
     * Returns the y-coordinate of the player object.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the player object.
     * 
     * @param newY the new y-coordinate to set
     */
    public void setY(float newY) {
        this.y = newY;
    }
}
