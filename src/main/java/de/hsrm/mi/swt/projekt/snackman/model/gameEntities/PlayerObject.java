package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `PlayerObject` class represents a moveable object in a game, which is controlled by the Player 
 * 
 * 
 */
public abstract class PlayerObject implements Moveable {

    /** The unique identifier for the PlayerObject */
    private final int id;
    
    /** The x-coordinate of the PlayerObject */
    private float x;

    /** The y-coordinate of the PlayerObject */
    private float y; 

    /**
     * Constructs a new `PlayerObject` with the specified starting coordinates.
     * 
     * @param x the initial x-coordinate of the PlayerObject
     * @param y the initial y-coordinate of the PlayerObject
     */
    public PlayerObject(int id, float x, float y) {
        this.id = id;
        this.x = x; 
        this.y = y;  
    }

    /**
     * Returns the unique identifier of the PlayerObject.
     * 
     * @return the `id` of the PlayerObject
     */
    public int getId() {
        return id;
    }


    /**
     * Returns the x-coordinate of the PlayerObject.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the PlayerObject.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }
    
}
