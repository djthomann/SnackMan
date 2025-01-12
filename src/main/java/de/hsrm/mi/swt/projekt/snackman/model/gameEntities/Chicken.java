package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.python.core.PyObject;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.EatEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.logic.CollisionManager;
import de.hsrm.mi.swt.projekt.snackman.logic.CollisionType;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.*;


/**
 * The `Chicken` class represents a NPC in the game.
 * 
 * 
 */
public class Chicken extends GameObject implements CanEat, MovableAndSubscribable {

    private Logger logger = LoggerFactory.getLogger(Chicken.class);

    private GameManager gameManager;
    private GameConfig gameConfig;
    private CollisionManager collisionManager;

    // Variables for passive calorie gain
    private int passiveCalorieGain;
    private int passiveCalorieGainDelay;
    private TimerTask passiveCaloriesTask = new TimerTask() {
            
        @Override
        public void run() {

            //logger.info("\n \nChicken with id " + objectId + " has passively gained calories");
            //logger.info(objectId + " previous calories: " + gainedCalories);

            gainedCalories += passiveCalorieGain;

            //logger.info(objectId + " current calories: " + gainedCalories);
        }
    };
    private transient Timer passiveCaloriesTimer;

    /** The gainedCalorie count of the Chicken */
    private int gainedCalories;
    private String direction;
    private List<List<String>> surroundings;
    private Boolean wallCollision;
    /** Jython-Interpreter for the script logic */
    private final PythonInterpreter scriptInterpreter;

    /**
     * Constructs a new Chicken with the objectId, and specified starting Coords.
     *
     * @param x          the initial x-coordinate of the Chicken
     * @param y          the initial y-coordinate of the Chicken
     * @param z          the initial z-coordinate of the Chicken
     * @param gameConfig the configuration of the game
     */
    public Chicken(long id, long gameId, float x, float y, float z, String script, GameManager gameManager,
            GameConfig gameConfig, CollisionManager collisionManager) {
        super(id, gameId, x, y, z, gameConfig.getChickenMinRadius(), gameConfig.getChickenHeight());
        this.direction = "N";
        this.wallCollision = false;
        this.gameConfig = gameConfig;
        this.collisionManager = collisionManager;
        this.gameManager = gameManager;
        this.gainedCalories = 0;
        //Timer for constant passive calorie gain
        this.passiveCalorieGain = 10;
        this.passiveCalorieGainDelay = 1000; //in ms
        this.passiveCaloriesTimer = new Timer();
        this.passiveCaloriesTimer.scheduleAtFixedRate(passiveCaloriesTask, 0, passiveCalorieGainDelay);
        // choose script file
        this.scriptInterpreter = new PythonInterpreter();
        initScriptInterpreter(script);
        logger.info("created Chicken with id: " + id);
    }

    private void initScriptInterpreter(String script) {
        String scriptFile;
        switch (script.toLowerCase()) {
            case "test":
                scriptFile = "src/main/java/de/hsrm/mi/swt/projekt/snackman/logic/scripts/chickenTestScript.py";
                break;
            case "one":
                scriptFile = "src/main/java/de/hsrm/mi/swt/projekt/snackman/logic/scripts/ChickenPersonalityOne.py";
                break;
            case "two":
                scriptFile = "src/main/java/de/hsrm/mi/swt/projekt/snackman/logic/scripts/ChickenPersonalityTwo.py";
                break;
            default:
                scriptFile = script;
                break;
        }
        this.scriptInterpreter.execfile(scriptFile); 
        new Thread(() -> {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                if (gameManager.getGameById(gameId) == null) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(50); // 1000 = 1 sec
                    surroundings = gameManager.getGameById(gameId).generateSurroundings(this.x, this.z);
                    //logger.info(surroundings.toString());
                    executeScript(surroundings);
                }
            }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * executes the behavior of the chicken, controlled by the script
     *
     * @param pythonCompatibleSurroundings 3x3 part of the map, on which the chicken navigates
     */    
    public void executeScript(List<List<String>> pythonCompatibleSurroundings) {
        scriptInterpreter.set("environment", pythonCompatibleSurroundings);
        scriptInterpreter.set("direction", direction);
        scriptInterpreter.set("wall_collision", wallCollision );
        scriptInterpreter.set("x", x);
        scriptInterpreter.set("z", z);
        try {

            scriptInterpreter.exec("result = run_behavior(environment, direction, wall_collision,x,z)");
            PyObject result = scriptInterpreter.get("result");

            if (result instanceof PyTuple) {
                PyTuple tuple = (PyTuple) result;

                // Retrieve the elements of the tuple and cast them to float
                float movementX = gameConfig.getChickenSpeed() * (float) (double) tuple.get(0);
                float movementY = gameConfig.getChickenSpeed() * (float) (double) tuple.get(1);
                float movementZ = gameConfig.getChickenSpeed() * (float) (double) tuple.get(2);
                this.direction = (String) tuple.get(3);
                this.wallCollision = (boolean) tuple.get(4);

                if(this.wallCollision == false) {
                    ArrayList<CollisionType> collisions;
                    float wishedX = 0.0f;
                    float wishedZ = 0.0f;

                    if ( direction.equals("N") || direction.equals("E") ) {
                        wishedX = this.getX() + (movementX + this.radius);
                        wishedZ = this.getZ() + (movementZ + this.radius);
                    } else {
                        wishedX = this.getX() + (movementX - this.radius);
                        wishedZ = this.getZ() + (movementZ - this.radius);
                    }
                    collisions = collisionManager.checkCollision(wishedX, wishedZ, this);
                        if (collisions.contains(CollisionType.WALL)) {
                            movementX = 0.0f;
                            movementZ = 0.0f; 
                            this.wallCollision = true;  
                        }
                }
                move((movementX), (movementY), (movementZ));
                //logger.info("Chicken: x = " + this.x + ", y = " + this.y + ", z = " + this.z + ", direction = " + this.direction);
                gameManager.getGameById(gameId).getGameState().addChangedChicken(this); 
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves chicken to new coords
     * 
     * @param newX the x-coordinate to move the Chicken
     * @param newY the y-coordinate to move the Chicken
     * @param newZ the z-coordinate to move the Chicken
     */
    @Override
    public void move(float newX, float newY, float newZ) {
        this.x += newX;
        this.y += newY;
        this.z += newZ;
        EventService.getInstance().applicationEventPublisher.publishEvent(new InternalMoveEvent(this, gameManager));
    }

    /**
     * Consumes the food, make the Chicken gain Calories.
     *
     * @param food the calorie resource to be consumed by the entity
     */
    @Override
    public void eat(Food food) {
        this.gainedCalories += food.getCalories();
        EventService.getInstance().applicationEventPublisher.publishEvent(new EatEvent(this, food, gameId));
    }

    /**
     * Resets the gainedCalories for the Chicken to 0.
     * 
     */
    public void resetGainedCalories() {
        this.gainedCalories = 0;
    }

    /**
     * Returns the number of gainedCalories.
     * 
     * @return the current gainedCalories
     */
    public int getGainedCalories() {
        return gainedCalories;
    }

    @Override
public void handle(Event event) {
    if (event == null || event.getObjectID() != this.objectId) {
        return;
    }
    //logger.info("Event received for object ID {}: {}", this.objectId, event);
}

    public ChickenRecord toRecord() {
        return new ChickenRecord(gameId, objectId, x, y, z, gainedCalories);
    }

    // String representation used for chickenssurroundings
    public String toString() {
        return "Chicken";
    }

}
