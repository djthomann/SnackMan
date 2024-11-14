package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Ghost` class represents a player character in the game with
 * an initial position on a plane.
 * 
 * This class extends `PlayerObject` and inherits its properties for
 * coordinates management.
 * 
 * 
 */
public class Ghost extends PlayerObject {

    /**
     * Constructs a new `Ghost` with the specified starting position.
     * 
     * @param x the initial x-coordinate of the `Ghost`
     * @param y the initial y-coordinate of the `Ghost`
     */
    public Ghost(float x, float y) {
        super(x, y);
    }

    /**
     * moves Ghost to new coords
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    @Override
    public void move(float x, float y) {
    }

/* 
    @Override
    public void handle(Event event) {
    }
*/

}