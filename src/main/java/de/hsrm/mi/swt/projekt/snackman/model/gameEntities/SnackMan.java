package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.EatEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.logic.CollisionManager;
import de.hsrm.mi.swt.projekt.snackman.logic.CollisionType;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.SnackManRecord;

/**
 * The `SnackMan` class represents a player character in the game who has an
 * initial position on a plane and has a calorie count.
 * This class extends `PlayerObject` and adds the ability to track and modify
 * the calorie count of the `SnackMan`.
 */
public class SnackMan extends PlayerObject implements CanEat, MovableAndSubscribable {

    private final Logger logger = LoggerFactory.getLogger(SnackMan.class);

    // Jumping constants
    private final float GRAVITY = -9.81f; // Gravity constant for physically realistic jumping
    private final float INITIAL_VELOCITY = 8.0f; // Initial velocity at the start of the jump
    private final float JUMP_BOOST = 7.0f; // Boost applied to the current jump if the SnackMan is already jumping and
                                           // the jump-method is called again
    private final int MAX_BOOSTS = 2; // Maximum number of velocity boosts that is possible to reach during one jump
    private final long BOOST_PUFFER_TIME = 100; // Time that has to be passed since last space bar press to enable boost

    // Jumping variables
    private float currentVelocity;
    private boolean jumping;
    private float initialY;
    private boolean falling;
    private float heightGain;
    private int boosts;
    private long jumpStartTime;
    private long jumpEndTime;
    float deltaTime = 0.1f;

    private boolean needsResolving = false;
    private Vector3f resolveVector = null;
    private final static float RESOLVE_SPEED = 0.1f;

    private ScheduledExecutorService jumpExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> jumpTaskFuture;
    private Runnable jumpTask;
    private ScheduledExecutorService resolveExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> resolveTaskFuture;
    private Runnable resolveTask;
    private ScheduledExecutorService fallExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> fallTaskFuture;
    private Runnable fallTask;

    private GameManager gameManager;
    private List<Client> clients;
    private GameConfig gameConfig;
    private CollisionManager collisionManager;

    /** The calorie count of the SnackMan */
    private int gainedCalories;
    private Consumer<Integer> calorieChangeListener;

    // Collision constants
    private final long STUNNED_TIME = 1000;
    private final long INVINCIBLE_TIME = 3000;

    // Collision variables
    private boolean stunned;
    private Timer stunnedTimer;
    private boolean invincible;
    private Timer invincibleTimer;
    private boolean alive;

    /**
     * Constructs a new `SnackMan` with the specified starting position and
     * initial calorie count.
     *
     * @param x the initial x-coordinate of the `SnackMan`
     * @param y the initial y-coordinate of the `SnackMan`
     * @param z the initial z-coordinate of the `SnackMan`
     */
    public SnackMan(String username, long id, List<Client> clients, long gameId, float x, float y, float z, GameManager gameManager, GameConfig gameConfig,
            CollisionManager collisionManager) {
        super(username, id, gameId, x, y, z, gameConfig.getSnackManRadius(), gameConfig.getSnackManHeight());
        this.gameConfig = gameConfig;
        this.collisionManager = collisionManager;

        // TODO Initial calories to make jumping possible, change back to 0 later
        this.gainedCalories = 1000;
        this.gameManager = gameManager;

        this.invincible = false;
        this.stunned = false;
        this.alive = true;
        this.stunnedTimer = new Timer();
        this.invincibleTimer = new Timer();

        this.clients = clients;

        init();

        logger.info("created SnackMan with id: " + id);
    }

    public void init() {
        this.jumpTask = () -> {

            logger.info("Jumping");

            /*
              The gravity (a negative constant) is applied to the SnackMan's velocity
              The velocity starts at a positive value
              It is 0 when the SnackMan is at its highest position
              It is negative during the falling phase
              Formula: v = v + deltaT * G where v is the velocity, deltaT the time between
              position updates and G the gravity constant
             */
            currentVelocity += deltaTime * GRAVITY;

            /*
             * Updating the SnackMan's position by applying the velocity to the current
             * position
             * Formula: y = y + deltaT * v where y is the position, deltaT the time between
             * position updates and v the SnackMan's current velocity
             */
            heightGain = deltaTime * currentVelocity;

            move(0, heightGain, 0);

            /*
             * Check if the SnackMan has landed at its initial y-position
             * If so, the jump is done
             */
            if(collisionManager.positionInWall(x, z)) {
                if (y < GameConfig.WALL_HEIGHT) {
                    y = GameConfig.WALL_HEIGHT;
                    jumping = false;
                    needsResolving = true;
                    resolveOnTopOfWall();
                }
            } else {
                // Check if SnackMan has landed on food
                collisionManager.checkCollision(x, z, SnackMan.this);
                if (y < 0.8f) {
                    y = 0.8f;
                    jumping = false;
                }
            }

            // If the jump is done, the task is not repeated anymore
            if (!jumping) {
                boosts = 0;
                jumpTaskFuture.cancel(false);
            }
        };

        this.resolveTask = () -> {

            if(resolveVector == null) {
                resolveVector = collisionManager.getResolveVector(x, z);
            }

            // logger.info("Trying to resolve");
            logger.info(resolveVector.toString());

            move(resolveVector.x * RESOLVE_SPEED, 0, resolveVector.z * RESOLVE_SPEED);

            /*
             * Check if the SnackMan is not over a wall anymore
             */
            if(!collisionManager.positionInWall(x, z)) {
                needsResolving = false;
            }

            // If the resolve is done, the task is not repeated anymore
            if (!needsResolving) {
                resolveVector = null;
                resolveTaskFuture.cancel(false);
                fall();
            }
        };

        this.fallTask = () -> {

            currentVelocity += deltaTime * GRAVITY;

            heightGain = deltaTime * currentVelocity;

            // logger.info("Falling" + currentVelocity);
            move(0, heightGain, 0);

            collisionManager.checkCollision(x, z, SnackMan.this);
            if (y < 0.8f) {
                y = 0.8f;
                falling = false;
            }

            // If the fall is done, the task is not repeated anymore
            if (!falling) {
                fallTaskFuture.cancel(false);
            }
        };
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
        this.gameManager.getGameById(gameId).updateTileOccupation(this, x, z, x + newX, z + newZ);
        super.move(newX, newY, newZ);
        EventService.getInstance().applicationEventPublisher.publishEvent(new InternalMoveEvent(this, gameManager));
    }

   /**
     * Method to implement jumping
     */
    public void jump() {

        // If we are not jumping and have enough calories, start new jump
        if (!falling && !jumping && !needsResolving && gainedCalories >= this.gameConfig.getJumpCalories()) {

            this.jumpStartTime = System.currentTimeMillis();

            // Jumping results in loss of calories
            this.gainedCalories -= this.gameConfig.getJumpCalories();

            this.initialY = this.y;
            this.currentVelocity = INITIAL_VELOCITY;
            this.jumping = true;
            this.boosts = 0;

            this.needsResolving = false;

            // Repeat task every time the specified time period has passed until cancelled
            jumpTaskFuture = jumpExecutor.scheduleAtFixedRate(jumpTask, 0, (long) (deltaTime * 300),
                    TimeUnit.MILLISECONDS);

            

            /*
             * If we are already jumping, have not reached the maximum possible number of
             * boosts and have enough calories,
             * apply boost to current velocity, so that we jump higher
             */
        } else if (this.boosts < MAX_BOOSTS && this.gainedCalories >= this.gameConfig.getJumpCalories()) {

            this.jumpEndTime = System.currentTimeMillis();

            // Time that has passed since pressing the space bar
            float timeDifference = this.jumpEndTime - this.jumpStartTime;

            /*
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

    public void resolveOnTopOfWall() {

        resolveTaskFuture = resolveExecutor.scheduleAtFixedRate(resolveTask, 0, (long) (deltaTime * 300),
            TimeUnit.MILLISECONDS);
        
    }

    public void fall() {

        
        if (!jumping && !falling && !needsResolving) {
            this.currentVelocity = 0;
            falling = true;
            fallTaskFuture = fallExecutor.scheduleAtFixedRate(fallTask, 0, (long) (deltaTime * 300),
            TimeUnit.MILLISECONDS);
        }

    }

    /**
     * Sets the calorieChangeListener for the SnackMan
     * 
     * @param listener the listener to be set
     */
    public void setCalorieChangeListener(Consumer<Integer> listener) {
        this.calorieChangeListener = listener;
    }

    /**
     * Handles a change in the calorie count of the `SnackMan`.
     * 
     * @param newCalories the new calorie count of the `SnackMan`
     */
    private void handleCalorieChange(int newCalories) {
        this.gainedCalories = newCalories;
        if (calorieChangeListener != null) {
            calorieChangeListener.accept(this.gainedCalories);
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
        handleCalorieChange(this.gainedCalories + food.getCalories());
        EventService.getInstance().applicationEventPublisher.publishEvent(new EatEvent(this, food, gameId, gameManager));
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

        if (event.getObjectID() != this.objectId) {
            return;
        }

        logger.info("Event " + event.getType() + " in Snackman " + objectId);
        if (Objects.requireNonNull(event.getType()) == EventType.MOVE) {// The SnackMan is unable to move when stunned
            if (this.stunned) {
                /*
                MoveEvent moveEvent = new MoveEvent(new Vector3f(x, y, z));
                for(Client c : clients) {
                    gameManager.notifyChange(c, moveEvent);
                }
                    */
                return;
            }

            Vector3f vector = ((MoveEvent) event).getMovementVector();
            logger.info("Movement-Vektor: x = " + vector.x + ", y = " + vector.y + ", z = " + vector.z);

            float wishedX = this.getX() + (vector.x * gameConfig.getSnackManStep());
            logger.info("Wished X: " + wishedX);
            float wishedZ = this.getZ() + (vector.z * gameConfig.getSnackManStep());
            logger.info("Wished Z: " + wishedZ);

            if (!collisionManager.positionIsWithinMapBounds(wishedX, wishedZ)) {
                vector.x = 0.0f;
                vector.z = 0.0f;
                return;
            }

            if (needsResolving || falling) {
                logger.info("Can't move during resolving");
                vector.x = 0;
                vector.z = 0;
                return;
            }

            if (collisionManager.positionInWall(x, z) && !jumping) {
                if (!collisionManager.positionInWall(wishedX, wishedZ)) {
                    this.jump();
                }
            }

            // Logic for collision with wall side and food
            List<CollisionType> collisions;
            collisions = collisionManager.checkCollision(wishedX, wishedZ, this);
            if (wishedX != this.getX() || wishedZ != this.getZ()) {
                if (this.getY() < gameConfig.getWallHeight()) {
                    if (collisions.contains(CollisionType.WALL)) {
                        vector.x = 0.0f;
                        vector.z = 0.0f;
                    } 
                }

            }

            /*
             * In case of a ghost collision the SnackMan loses calories,
             * is stunned and unable to move for a certain time period
             * and invincible for a certain time period
             */
            if (collisions.contains(CollisionType.GHOST) && !this.invincible) {
                reactToGhostCollision();
            }

            /*
             * In case of a SnackMan collision the SnackMan is unable to move through the other SnackMan
             */
            if (collisions.contains(CollisionType.SNACKMAN)) {

                // If the SnackMan is mid-jump, it has landed on another SnackMan
                if (this.jumping) {
                    this.jumping = false;
                    jumpTaskFuture.cancel(false);
                    this.y = gameConfig.getSnackManHeight() + gameConfig.getSnackManHeight() / 2;
                }

                    logger.info("Kollision mit Snack Man, aktuelle Kalorien: " + this.gainedCalories);
                    vector.x = 0.0f;
                    vector.z = 0.0f;
                }

                if (collisions.contains(CollisionType.CHICKEN)) {
                    if(this.jumping) {
                        this.jumping = false;
                        jumpTaskFuture.cancel(false);
                        this.y = gameConfig.getSnackManHeight() + gameConfig.getSnackManHeight() / 2;
                    }
                    vector.x = 0.0f; 
                    vector.z = 0.0f; 
                }
                
                this.move(vector.x * gameConfig.getSnackManStep(), 0, vector.z * gameConfig.getSnackManStep());

            // checks if the movementVector is from a jump action or not
            if (vector.y != 0.0) {

                this.jump();

                logger.info("SNACKMAN JUMPT");
            }
        }

        logger.info("Event arrived at SnackMan :" + event);
    }

    public SnackManRecord toRecord() {
        return new SnackManRecord(gameId, objectId, getUsername(), x, y, z, gainedCalories);
    }


    /**
     * Returns string representation used for chicken-surroundings 
     * 
     * @return string representation
     */        
    public String toString() {
        return "SNACKMAN";
    }

    public boolean isInvincible() {
        return invincible;
    }

    public boolean isStunned() {
        return stunned;
    }

    /**
     * Makes the SnackMan lose the passed calorie amount
     * and sets the SnackMan to dead if its new calorie count is negative or zero
     * @param calorieLoss amount to be lost
     */
    private void checkIfDead(int calorieLoss) {
        this.gainedCalories -= calorieLoss;
        if(this.gainedCalories <= 0) {
            this.alive = false;
        }
    }

    /**
     * While the timer runs, the SnackMan is stunned and thus unable to move
     * 
     */
    private void startStunnedTimer() {

        this.stunned = true;
        logger.info("SnackMan with id " + this.objectId + " is stunned");

        TimerTask task;
         task = new TimerTask() {
            
            @Override
            public void run() {
                stunned = false;
                logger.info("SnackMan with id " + objectId + " is NOT stunned anymore");
            }
        };

        this.stunnedTimer.schedule(task, STUNNED_TIME);
    }

    /**
     * While the timer runs, the SnackMan is invincible 
     * and thus cannot be stunned by a ghost and lose calories
     * 
     */
    private void startInvincibleTimer() {

        this.invincible = true;
        logger.info("SnackMan with id " + this.objectId + " is invincible");

        TimerTask task;
         task = new TimerTask() {
            
            @Override
            public void run() {
                invincible = false;
                logger.info("SnackMan with id " + objectId + " is NOT invincible anymore");
            }
        };

        this.invincibleTimer.schedule(task, INVINCIBLE_TIME);
    }

    public void reactToGhostCollision() {
        if (!isInvincible() && !isStunned()) {
            logger.info("SnackMan calories before ghost collision: " + this.gainedCalories);
            checkIfDead(gameConfig.getGhostCollisionCalories());
            logger.info("SnackMan calories after ghost collision: " + this.gainedCalories);
            startStunnedTimer();
            startInvincibleTimer();
        }
    }

    public boolean isChicken() {
        return false;
    }

}
