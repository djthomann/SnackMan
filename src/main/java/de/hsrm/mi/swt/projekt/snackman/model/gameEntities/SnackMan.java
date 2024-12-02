package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
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

    // Jumping constants
    final float GRAVITY = -9.81f; // Gravity constant for physically realistic jumping
    final float INITIAL_VELOCITY = 8.0f; // Initial velocity at the start of the jump
    final float JUMP_BOOST = 5.0f; // Boost applied to the current jump if the SnackMan is already jumping and the jump-method is called again
    final int MAX_BOOSTS = 2; // Maximum number of velocity boosts that is possible to reach during one jump

    // Jumping variables
    private float currentVelocity;
    private boolean jumping;
    private float initialY;
    private float heightGain;
    private int boosts;

    private ScheduledExecutorService jumpExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> jumpTaskFuture;

    //final float JUMP_HEIGHT = 5.0F;

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

        // If we are not jumping, start new jump
        if(!jumping) {

            this.initialY = this.y;
            this.currentVelocity = INITIAL_VELOCITY;
            this.jumping = true;
            this.boosts = 0;
            float deltaTime = 0.1f;
 
            Runnable jumpTask = new Runnable() {

                @Override
                public void run() {
                
                        /**
                         * The gravity (a negative constant) is applied to the SnackMan's velocity
                         * The velocity starts at a positive value
                         * It is 0 when the SnackMan is at its highest position
                         * It is negative during the falling phase
                         * Formula: v = v + deltaT * G where v is the velocity, deltaT the time between position updates and G the gravity constant
                         */
                        currentVelocity += deltaTime * GRAVITY;
            
                        /**
                         * Updating the SnackMan's position by applying the velocity to the current position
                         * Formula: y = y + deltaT * v where y is the position, deltaT the time between position updates and v the SnackMan's current velocity
                         */
                        heightGain = deltaTime * currentVelocity;
            
                        move(0, heightGain, 0);
            
                        /**
                         * Check if the SnackMan has landed at its initial y-position
                         * If so, the jump is done
                         */
                        if (y < initialY ) {
                            y = initialY;
                            jumping = false;
                        }
            
                        MoveEvent moveEvent = new MoveEvent(new Vector3f(x, y, z));
                        gameManger.notifyChange(moveEvent);
            
                        // If the jump is done, the task is not repeated anymore
                        if (!jumping) {
                            boosts = 0;
                            jumpTaskFuture.cancel(false);
                        }
                }
                
            };

            // Repeat task every time the specified time period has passed until cancelled
            jumpTaskFuture = jumpExecutor.scheduleAtFixedRate(jumpTask, 0, (long) (deltaTime * 300), TimeUnit.MILLISECONDS);
        

        /**
         * If we are already jumping and have not reached the maximum possible number of boosts,
         * apply boost to current velocity, so that we jump higher
         */
        } else if (this.boosts < MAX_BOOSTS) {
            this.currentVelocity += JUMP_BOOST;
            this.boosts++;
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

                this.move(vector.x * 0.2f,0, vector.z * 0.2f);

                //checks if the movementVector is from a jump action or not
                if(vector.y != 0.0) {
                    
                    this.jump();

                    logger.info("SNACKMAN JUMPT");
                }

                break;

        }

        logger.info("Event arrived at SnackMan");
    }

}
