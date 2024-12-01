package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.level.OccupationType;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

public class CollisionManager {

    private SnackManMap snackManMap;
    private ArrayList<MovableAndSubscribable> allMovables;
    Logger logger = LoggerFactory.getLogger(GameManager.class);


    public CollisionManager(SnackManMap snackManMap, ArrayList<MovableAndSubscribable> allMovables) {
        this.snackManMap = snackManMap;
        this.allMovables = allMovables;
    }
    
    /**
     * Checks for collisions in the event.
     * If a collision is detected, the method returns a String representing the collision type.
     * If no collision is detected, the method returns "none".
     * 
     * @param event The MoveEvent that triggered the collision check.
     * @return The type of object that the movable object collided with, or "none" if no collision is detected.
     */
    public String checkCollision(Event event) {
        switch (event.getType()) {
            case MOVE:
                int objectID = event.getObjectID();
                for (MovableAndSubscribable movable : allMovables) {
                    if (movable.getId() == objectID) {
                        if (movable instanceof SnackMan) {
                            Tile tile= snackManMap.getTileAt((int) movable.getX(), (int) movable.getY());
                            Vector3f movementVector = ((MoveEvent) event).getMovementVector();
                            float wishedX = movable.getX() + (movementVector.x * 0.2f);
                            float wishedY = movable.getY() + (movementVector.y * 0.2f);
                            Tile wishedTile = snackManMap.getTileAt((int) wishedX, (int) wishedY);
                            if(wishedTile.getX() != tile.getX() || wishedTile.getY() != tile.getY()) {
                                if(wishedTile.getOccupationType() == OccupationType.WALL) 
                                //logger.info("snackman and wall Collision!");
                                    return "snackman:wall";
                                }
                        } else if (movable instanceof Ghost) {
                        // Handle Ghost collision
                        } else {
                        // Handle other types of objects
                        }
                    break;
                    }
                }
            default:
                break;
        }
        return "none";
        }
/**
 * Handles collision events within the game. This method processes the given
 * event, which contains information about the collision, and takes appropriate
 * actions based on the type and details of the event.
 *
 * @param event The collision event to be processed.
 */
    public Event resolveCollision(Event event, String problem) {
        switch (problem) {
            case "snackman:wall":
                event = new MoveEvent(new Vector3f(0, 0, 0));
                break;  
        }       
        return event;
    }

}
