package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.hsrm.mi.swt.projekt.snackman.communication.websocket.WebSocketHandler;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.EatEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.logic.CollisionManager;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.*;
/**
 * The `SnackMan` class represents a player character in the game who has an
 * initial position on a plane and has a calorie count.
 * This class extends `PlayerObject` and adds the ability to track and modify
 * the calorie count of the `SnackMan`.
 * 
 * 
 */
public class SnackMan extends PlayerObject implements CanEat, MovableAndSubscribable {

    private final Logger logger = LoggerFactory.getLogger(SnackMan.class);

    // Jumping constants
    // TODO: put into real config file
    private final float GRAVITY = -9.81f; // Gravity constant for physically realistic jumping
    private final float INITIAL_VELOCITY = 8.0f; // Initial velocity at the start of the jump
    private final float JUMP_BOOST = 5.0f; // Boost applied to the current jump if the SnackMan is already jumping and
                                           // the jump-method is called again
    private final int MAX_BOOSTS = 2; // Maximum number of velocity boosts that is possible to reach during one jump
    private final long BOOST_PUFFER_TIME = 100; // Time that has to be passed since last space bar press to enable boost

    // Jumping variables
    private float currentVelocity;
    private boolean jumping;
    private float initialY;
    private float heightGain;
    private int boosts;
    private long jumpStartTime;
    private long jumpEndTime;

    private ScheduledExecutorService jumpExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> jumpTaskFuture;

    private GameManager gameManger;
    private GameConfig gameConfig;
    private CollisionManager collisionManager;

    /** The calorie count of the SnackMan */
    private int gainedCalories;

    /**
     * Constructs a new `SnackMan` with the specified starting position and
     * initial calorie count.
     *
     * @param x the initial x-coordinate of the `SnackMan`
     * @param y the initial y-coordinate of the `SnackMan`
     * @param z the initial z-coordinate of the `SnackMan`
     */
    public SnackMan(String username, long id, long gameId, float x, float y, float z, GameManager gameManager, GameConfig gameConfig,
            CollisionManager collisionManager) {
        super(username, id, gameId, x, y, z, gameConfig.getSnackManRadius());
        this.gameConfig = gameConfig;
        this.collisionManager = collisionManager;

        // TODO Initial calories to make jumping possible, change back to 0 later
        this.gainedCalories = 1000000;
        this.gameManger = gameManager;

        logger.info("created SnackMan with id: " + id);
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
        if (!WebSocketHandler.testingMode) EventService.getInstance().applicationEventPublisher.publishEvent(new InternalMoveEvent(this, gameManger));
    }

    /**
     * Method to implement jumping
     */
    public void jump() {

        // If we are not jumping and have enough calories, start new jump
        if (!jumping && gainedCalories >= this.gameConfig.getJumpCalories()) {

            this.jumpStartTime = System.currentTimeMillis();

            // Jumping results in loss of calories
            this.gainedCalories -= this.gameConfig.getJumpCalories();

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
                     * Formula: v = v + deltaT * G where v is the velocity, deltaT the time between
                     * position updates and G the gravity constant
                     */
                    currentVelocity += deltaTime * GRAVITY;

                    /**
                     * Updating the SnackMan's position by applying the velocity to the current
                     * position
                     * Formula: y = y + deltaT * v where y is the position, deltaT the time between
                     * position updates and v the SnackMan's current velocity
                     */
                    heightGain = deltaTime * currentVelocity;

                    move(0, heightGain, 0);

                    /**
                     * Check if the SnackMan has landed at its initial y-position
                     * If so, the jump is done
                     */
                    if (y < initialY) {
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
            jumpTaskFuture = jumpExecutor.scheduleAtFixedRate(jumpTask, 0, (long) (deltaTime * 300),
                    TimeUnit.MILLISECONDS);

            /**
             * If we are already jumping, have not reached the maximum possible number of
             * boosts and have enough calories,
             * apply boost to current velocity, so that we jump higher
             */
        } else if (this.boosts < MAX_BOOSTS && this.gainedCalories >= this.gameConfig.getJumpCalories()) {

            this.jumpEndTime = System.currentTimeMillis();

            // Time that has passed since pressing the space bar
            float timeDifference = this.jumpEndTime - this.jumpStartTime;

            /**
             * Boosts are only possible when enough time has passed since pressing the space
             * bar
             * so that they are not applied immediately
             */
            if (timeDifference > this.BOOST_PUFFER_TIME) {
                this.currentVelocity += JUMP_BOOST;
                this.boosts++;
                this.gainedCalories -= this.gameConfig.getJumpCalories();
            }
        }

    }

    /**
     * method to Consume Food
     * publishes an eat event to be progressed by the GameState
     *
     * @param food the calorie resource to be consumed by the `SnackMan`.
     */
    @Override
    public void eat(Food food) {
        this.gainedCalories += food.getCalories();
        EventService.getInstance().applicationEventPublisher.publishEvent(new EatEvent(this, food, gameId));
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
     *
     * @param event the event to be handled
     */
    @Override
    public void handle(Event event) {

        if (!WebSocketHandler.testingMode && event.getObjectID() != this.objectId) {
            return;
        }

        switch (event.getType()) {

            case MOVE:

                Vector3f vector = ((MoveEvent) event).getMovementVector();
                logger.info("Movement-Vektor: x = " + vector.x + ", y = " + vector.y + ", z = " + vector.z);
                String collision = "none";
                float wishedX = this.getX() + (vector.x * gameConfig.getSnackManStep());
                logger.info("Wished X: " + wishedX);
                float wishedZ = this.getZ() + (vector.z * gameConfig.getSnackManStep());
                logger.info("Wished Z: " + wishedZ);
                if (wishedX != this.getX() || wishedZ != this.getZ()) {
                    if (this.getY() < gameConfig.getWallHeight()) {
                        collision = collisionManager.checkCollision(wishedX, wishedZ, this);
                        if (collision.equals("wall")) {
                            vector.x = 0;
                            vector.z = 0;
                        } else if (collision.equals("item")) {
                            logger.info("MMMMMM delicious ");
                        }
                    }

                }

                this.move(vector.x * gameConfig.getSnackManStep(), 0, vector.z * gameConfig.getSnackManStep());


                MoveEvent moveEvent = new MoveEvent(new Vector3f(x, y, z));
                gameManger.notifyChange(moveEvent);



                // checks if the movementVector is from a jump action or not
                if (vector.y != 0.0) {

                    this.jump();

                    logger.info("SNACKMAN JUMPT");
                }

            default:
                break;

        }

        logger.info("Event arrived at SnackMan :" + event.toString());
    }

    public SnackManRecord toRecord() {
        return new SnackManRecord(gameId, objectId, getUsername(), x, y, z, gainedCalories);
    }

}
