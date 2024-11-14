package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `PlayerObject` class represents a moveable object in a game, which is controlled by the Player 
 * 
 * 
 */
public abstract class PlayerObject extends GameObject implements Moveable {

    /**
     * Constructs a new `PlayerObject` with the specified starting coordinates.
     * 
     * @param x the initial x-coordinate of the player object
     * @param y the initial y-coordinate of the player object
     */
    public PlayerObject(float x, float y) {
        super(x, y);
    }
    
}
