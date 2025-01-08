package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.*;

/**
 * The `Ghost` class represents a player character in the game with
 * an initial position on a plane.
 * This class extends `PlayerObject` and inherits its properties for
 * the objectId, and coordinates management.
 * 
 * 
 */
public class Ghost extends PlayerObject implements MovableAndSubscribable {

    /**
     * Constructs a new `Ghost` with the specified starting position.
     *
     * @param x          the initial x-coordinate of the `Ghost`
     * @param y          the initial y-coordinate of the `Ghost`
     * @param z          the initial z-coordinate of the `Ghost`
     * @param gameConfig the configuration of the game
     * 
     */

    public Ghost(String username, long id, long gameId, float x, float y, float z, GameConfig gameConfig) {
        super(username, id, gameId, x, y, z, gameConfig.getGhostRadius());
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
        EventService.getInstance().applicationEventPublisher.publishEvent(new InternalMoveEvent(this, gameId));
    }

    @Override
    public void handle(Event event) {
    }

    public GhostRecord toRecord() {
        return new GhostRecord(gameId, objectId, getUsername(), x, y, z);
    }

    public String toString() {
        return "GHOST";
    }

}
