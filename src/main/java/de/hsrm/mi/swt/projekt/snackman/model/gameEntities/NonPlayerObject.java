package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `NonPlayerObject` class represents a NPC object in a game.
 * 
 * 
 */
public abstract class NonPlayerObject extends GameObject  {

    /**
     * Constructs a new `NonPlayerObject` with the specified starting coordinates.
     * 
     * @param x the initial x-coordinate of the non-player object
     * @param y the initial y-coordinate of the non-player object
     */
    public NonPlayerObject(float x, float y) {
        super(x, y);
    }

}
