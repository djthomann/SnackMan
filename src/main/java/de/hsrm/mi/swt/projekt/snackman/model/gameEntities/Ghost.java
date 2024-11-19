package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Ghost` class represents a player character in the game with
 * an initial position on a plane.
 * 
 * This class extends `PlayerObject` and inherits its properties for
 * the id, and coordinates management.
 * 
 * 
 */
public class Ghost extends PlayerObject {

    /**
     * Constructs a new `Ghost` with the specified starting position.
     * 
     * @param id        the unique identifier of the `Ghost`
     * @param x         the initial x-coordinate of the `Ghost`
     * @param y         the initial y-coordinate of the `Ghost`
     */
    public Ghost(int id, float x, float y) {
        super(id, x, y);
    }

    /**
     * moves Ghost to new coords
     * 
     * @param newX the x-coordinate to move the `Ghost` to
     * @param newY the y-coordinate to move the `Ghost` to
     */
    @Override
    public void move(float newX, float newY) {
    }

    /* 
    @Override
    public void handle(Event event) {}
    */
}
