package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.ArrayList;
import java.util.Objects;

import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.logic.CollisionManager;
import de.hsrm.mi.swt.projekt.snackman.logic.CollisionType;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
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

    private final GameManager gameManager;
    private final GameConfig gameConfig;
    private final CollisionManager collisionManager;
    private final Logger logger = LoggerFactory.getLogger(Ghost.class);

    /**
     * Constructs a new `Ghost` with the specified starting position.
     *
     * @param x          the initial x-coordinate of the `Ghost`
     * @param y          the initial y-coordinate of the `Ghost`
     * @param z          the initial z-coordinate of the `Ghost`
     * @param gameConfig the configuration of the game
     * 
     */

    public Ghost(String username, long id, long gameId, float x, float y, float z, GameConfig gameConfig, GameManager gameManager, CollisionManager collisionManager) {
        super(username, id, gameId, x, y, z, gameConfig.getGhostRadius(), gameConfig.getGhostHeight());
        this.gameManager = gameManager;
        this.gameConfig = gameConfig;
        this.collisionManager = collisionManager;
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
        this.gameManager.getGameById(gameId).updateTileOccupation(this, x, z, x + newX, z + newZ);
        x += newX;
        y += newY;
        z += newZ;
        EventService.getInstance().applicationEventPublisher.publishEvent(new InternalMoveEvent(this, gameManager));
    }

    @Override
    public void handle(Event event) {

        if (event.getObjectID() != this.objectId) {
            return;
        }

        if (Objects.requireNonNull(event.getType()) == EventType.MOVE) {
            Vector3f vector = ((MoveEvent) event).getMovementVector();
            logger.info("Movement-Vektor: x = " + vector.x + ", y = " + vector.y + ", z = " + vector.z);
            ArrayList<CollisionType> collisions;
            float wishedX = this.getX() + (vector.x * gameConfig.getSnackManStep());
            logger.info("Wished X: " + wishedX);
            float wishedZ = this.getZ() + (vector.z * gameConfig.getSnackManStep());
            logger.info("Wished Z: " + wishedZ);
            collisions = collisionManager.checkCollision(wishedX, wishedZ, this);
            if (wishedX != this.getX() || wishedZ != this.getZ()) {
                if (this.getY() < gameConfig.getWallHeight()) {
                    if (collisions.contains(CollisionType.WALL)) {
                        vector.x = 0;
                        vector.z = 0;
                    }
                }

            }

            this.move(vector.x * gameConfig.getGhostStep(), 0, vector.z * gameConfig.getGhostStep());
        }

        logger.info("Event arrived at Ghost :" + event);

    }

    public GhostRecord toRecord() {
        return new GhostRecord(gameId, objectId, getUsername(), x, y, z);
    }

    // String representation used for chickenssurroundings
    public String toString() {
        return "GHOST";
    }

}
