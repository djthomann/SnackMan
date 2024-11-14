package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `GameObject` class represents a character in a game with unique `id` and coordinates (`x`, `y`) . 
 * 
 * This class provides methods to get and set the coordinates of the Game object.
 * 
 * 
 */
public abstract class GameObject {

    /** counter for The unique identifier for the game object */
    private static int counter = 0;

    /** The unique identifier for the game object */
    private final int id;

    /** The x-coordinate of the game object */
    private float x;

    /** The y-coordinate of the game object */
    private float y;

    /**
     * Constructs a new `GameObject` with the specified starting coordinates.
     * 
     * @param x the initial x-coordinate of the game object
     * @param y the initial y-coordinate of the game object
     */
    public GameObject(float x, float y) {
        this.id = ++counter;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the unique identifier of the game object.
     * 
     * @return the `id` of the game object
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the game object.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the game object.
     * 
     * @param newX the new x-coordinate to set
     */
    public void setX(float newX) {
        this.x = newX;
    }

    /**
     * Returns the y-coordinate of the game object.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the game object.
     * 
     * @param newY the new y-coordinate to set
     */
    public void setY(float newY) {
        this.y = newY;
    }

}
