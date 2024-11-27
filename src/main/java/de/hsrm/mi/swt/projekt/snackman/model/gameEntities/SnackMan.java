package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.communication.events.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;

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

    final float JUMP_HEIGHT = 5.0F;
    GameManager gameManger;
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
     */
    public SnackMan(int id, float x, float y, float z, GameManager gameManager) {
        super(id, x, y, z);
        this.gainedCalories = 0;
        this.gameManger = gameManager;
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
     * @param newX the x-coordinate to move the `SnackMan` to
     * @param newY the y-coordinate to move the `SnackMan` to
     * @param newZ the y-coordinate to move the `SnackMan` to
     */
    @Override
    public void move(float newX, float newY, float newZ) {
        this.x += newX; 
        this.y += newY; 
        this.z += newZ; 
    }

    /**
     * Method to implement jumping
     */
    public void jump() {

        for(int i = 0; i < 5; i++) {
            this.move(0, JUMP_HEIGHT/5, 0);

            MoveEvent moveEvent = new MoveEvent(new Vector3f(x, y, z));
            gameManger.notifyChange(moveEvent);

            logger.info("One fifth up");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }

        for(int i = 0; i < 5; i++) {
            this.move(0, -JUMP_HEIGHT/5, 0);

            MoveEvent moveEvent = new MoveEvent(new Vector3f(x, y, z));
            gameManger.notifyChange(moveEvent);

            logger.info("One fifth down");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        

    }

    /**
     * method to Consume Food
     *
     * @param food the calorie resource to be consumed by the `SnackMan`. 
     */
    @Override
    public void eat(Food food) {
    }

    /**
     * Returns the current gainedCalories count of the `SnackMan`.
     * 
     * @return the current gainedCalories count
     */
    public int getGainedCalories() {
        return gainedCalories;
    }

    @Override
    public void handle(Event event) {

        if(event.getObjectID() != this.id) {
            return;
        }

        switch(event.getType()) {

            case MOVE:

                Vector3f vector = ((MoveEvent)event).getMovementVector();
                logger.info("Movement-Vektor: x = " + vector.x + ", y = " + vector.y + ", z = " + vector.z);

                //checks if the movementVector is from a jump action or not
                if(vector.y != 0.0) {
                    
                    //TODO while jumping no other key presses are possible
                    this.jump();

                    logger.info("SNACKMAN JUMPT");
                }

                else {
                    this.move(vector.x * 0.2f,vector.y * 0.2f, vector.z * 0.2f);
                }

                break;

        }

        logger.info("Event arrived at SnackMan");
    }

}
