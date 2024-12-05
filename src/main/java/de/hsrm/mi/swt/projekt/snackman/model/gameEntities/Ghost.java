package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;

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

        /** Publisher to publish the internal backend event. */
     @Autowired
     private ApplicationEventPublisher applicationEventPublisher;

        /** id of game the ghost belongs to */
        private final long gameId;

    /**
     * Constructs a new `Ghost` with the specified starting position.
     * 
     * @param id        the unique identifier of the `Ghost`
     * @param x         the initial x-coordinate of the `Ghost`
     * @param y         the initial y-coordinate of the `Ghost`
     * @param z         the initial z-coordinate of the `Ghost`
     */
    public Ghost(int id, float x, float y, float z, long gameId) {
        super(id, x, y, z);
        this.gameId = gameId;
    }

    /**
     * moves Ghost to new coords
     * 
     * @param newX the x-coordinate to move the `Ghost` to
     * @param newY the y-coordinate to move the `Ghost` to
     * @param newZ the z-coordinate (remains the same)
     */
    @Override
    public void move(float newX, float newY, float newZ) {
        x = newX; 
        y = newY; 
        z = newZ; 
        applicationEventPublisher.publishEvent(new InternalMoveEvent(this,gameId));
    }

    @Override
    public void handle(Event event) {
    }

}
