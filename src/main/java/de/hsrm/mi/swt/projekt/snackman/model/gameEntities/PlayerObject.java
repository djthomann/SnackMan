package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `PlayerObject` class represents a moveable object in a game, which is controlled by the Player 
 * 
 * 
 */
public abstract class PlayerObject implements Moveable {

    /** The x-coordinate of the game object */
    private float x;

    /** The y-coordinate of the game object */
    private float y; 

    /**
     * Constructs a new `PlayerObject` with the specified starting coordinates.
     * 
     * @param x the initial x-coordinate of the player object
     * @param y the initial y-coordinate of the player object
     */
    public PlayerObject(float x, float y) {
        this.x = x; 
        this.y = y; 
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
