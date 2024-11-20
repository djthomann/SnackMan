package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `PlayerObject` class represents a moveable object in a game, which is controlled by the Player 
 * 
 * 
 */
public abstract class PlayerObject implements Moveable, Subscribable {

    /** The unique identifier for the PlayerObject */
    protected final int id;
    
    /** The x-coordinate of the PlayerObject */
    protected float x;

    /** The y-coordinate of the PlayerObject */
    protected float y;     
    
    /** The z-coordinate of the PlayerObject */
    protected float z; 

    
    /**
     * Constructs a new `PlayerObject` with the specified starting coordinates.
     * 
     * @param x the initial x-coordinate of the PlayerObject
     * @param y the initial y-coordinate of the PlayerObject
     * @param z the initial z-coordinate of the PlayerObject
     */
    public PlayerObject(int id, float x, float y, float z) {
        this.id = id;
        this.x = x; 
        this.y = y;  
        this.z = z; 
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
    
    /**
    * Returns the z-coordinate of the PlayerObject.
    * 
    * @return the current z-coordinate
    */
   public float getZ() {
       return z;
   }
    
}
