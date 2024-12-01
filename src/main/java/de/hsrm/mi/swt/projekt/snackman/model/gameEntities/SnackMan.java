package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.MoveEvent;

/**
 * The `SnackMan` class represents a player character in the game who has an
 * initial position on a plane and has a calorie count.
 * 
 * This class extends `PlayerObject` and adds the ability to track and modify
 * the calorie count of the `SnackMan`.
 * 
 * 
 */
public class SnackMan extends PlayerObject implements CanEat {

    Logger logger = LoggerFactory.getLogger(SnackMan.class);

    /** The calorie count of the SnackMan */
    private int gainedCalories;

    /**
     * Constructs a new `SnackMan` with the specified starting position and
     * initial calorie count.
     * 
     * @param id       the unique identifier of the `SnackMan`
     * @param x        the initial x-coordinate of the `SnackMan`
     * @param y        the initial y-coordinate of the `SnackMan` 
     * @param z        the initial z-coordinate of the `SnackMan` 
     * @param radius   the radius of the `SnackMan`
     */
    public SnackMan(int id, float x, float y, float z, float radius) {
        super(id, x, y, z, radius);
        this.gainedCalories = 0;
    }

    /**
     * Resets the gainedCalories for the `SnackMan` to 0.
     * 
     */
    public void resetGainedCalories() {
        this.gainedCalories = 0;
    }
    
    /**
     * moves SnackMan to new coords
     * 
     * @param newX the x-coordinate to move the `SnackMan`
     * @param newY the y-coordinate to move the `SnackMan`
     * @param newZ the y-coordinate to move the `SnackMan`
     */
    @Override
    public void move(float newX, float newY, float newZ) {
        this.x += newX; 
        this.y += newY; 
        this.z += newZ; 
    }

    /**
     * method to Consume Food
     *
     * @param food the calorie resource to be consumed by the `SnackMan`. 
     */
    @Override
    public void eat(Food food) {
        this.gainedCalories += food.getCalories();
    }

    /**
     * Returns the current gainedCalories count of the `SnackMan`.
     * 
     * @return the current gainedCalories count
     */
    public int getGainedCalories() {
        return gainedCalories;
    }

    /**
     * Handles an incoming event and reacts accordingly.

     * @param event the event to be handled
     */
    @Override
    public void handle(Event event) {

        if(event.getObjectID() != this.id) {
            return;
        }

        switch(event.getType()) {

            case MOVE:

                Vector3f vector = ((MoveEvent)event).getMovementVector();
                this.move(vector.x * 0.2f,vector.y * 0.2f, vector.z * 0.2f);
                break;

            default:
                break;

        }

        logger.info("Event arrived at SnackMan");
    }

}
