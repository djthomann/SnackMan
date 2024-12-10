package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;


/**
 * The `Ghost` class represents a player character in the game with
 * an initial position on a plane.
 * 
 * This class extends `PlayerObject` and inherits its properties for
 * the id, and coordinates management.
 * 
 * 
 */
public class Ghost extends GameObject implements MovableAndSubscribable {

    /**
     * Constructs a new `Ghost` with the specified starting position.
     *
     * @param x         the initial x-coordinate of the `Ghost`
     * @param y         the initial y-coordinate of the `Ghost`
     * @param z         the initial z-coordinate of the `Ghost`
     * @param radius    the radius of the `Ghost`
     */
    public Ghost(long gameId, float x, float y, float z, float radius) {
        super(gameId, x, y, z);
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
        EventService.getInstance().applicationEventPublisher.publishEvent(new InternalMoveEvent(this,gameId));
    }

    @Override
    public void handle(Event event) {
    }

}
