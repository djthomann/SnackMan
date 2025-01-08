package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.ArrayList;
import java.util.List;

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
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;
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
    /** The gainedCalorie count of the Chicken */
    private int gainedCalories;

    /** The current direction of the Chicken (N, NE, E, SE, S, SW, W, NW) */
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
     * @param scriptPath the path of the associated behavior script
     * @param gameConfig the configuration of the game
     */

    public Chicken(long id, long gameId, float x, float y, float z, String script, GameManager gameManager,
            GameConfig gameConfig, CollisionManager collisionManager) {
        super(id, gameId, x, y, z, gameConfig.getChickenMinRadius());
        this.direction = "N";
        this.wallCollision = false;
        this.gameConfig = gameConfig;
        this.collisionManager = collisionManager;
        this.gameManager = gameManager;
        this.gainedCalories = 0;
        this.scriptInterpreter = new PythonInterpreter();
        initScriptInterpreter(script);
    }

    private void initScriptInterpreter(String script) {
        String scriptFile;
        switch (script) {
            case "TEST", "test", "Test": // TO BE DELETED : TEST SCRIPT
                scriptFile = "src/main/java/de/hsrm/mi/swt/projekt/snackman/logic/scripts/chickenTestScript.py";
                break;
            case "ONE", "one", "One":
                scriptFile = "src/main/java/de/hsrm/mi/swt/projekt/snackman/logic/scripts/ChickenPersonalityOne.py";
                break;
            case "TWO", "two", "Two":
                scriptFile = "src/main/java/de/hsrm/mi/swt/projekt/snackman/logic/scripts/ChickenPersonalityTwo.py";
                break;
            default:
                scriptFile = script;
                break;
        }
        this.scriptInterpreter.execfile(scriptFile);
        SnackManMap map = gameManager.getGameById(gameId).getMap(); 
        new Thread(() -> {
            try {
                while ( x >= 1.0 && x < map.getW() && z >= 1.0 && z < map.getH()) {  
                    surroundings = generateSurroundings(map);
                    //logger.info(surroundings.toString());
                    Thread.sleep(50); // 1000 = 1 sec
                    executeScript(surroundings); 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

    }

    public List<List<String>> generateSurroundings(SnackManMap map) {
        Tile positionTile = map.getTileAt((int) (x), (int) (z));
        Tile[][] surroundings = map.getSurroundingTiles(positionTile);
        List<List<String>> pythonCompatibleSurroundings = new ArrayList<>();

        for (int row = 1; row >= -1; row--) {
            List<String> rowList = new ArrayList<>();
            for (int col = -1; col <= 1; col++) {
                Tile tile = surroundings[row + 1][col + 1];
                if (tile == null) {
                    rowList.add("OUT");
                } else {
                    switch (tile.getOccupationType()) {
                        case WALL:
                            rowList.add("WALL");
                            break;
                        case ITEM:
                            rowList.add(tile.getOccupation().toString());
                            break;
                        case FREE:
                            if (tile.getOccupation() != null) {
                                rowList.add(tile.getOccupation().toString());
                            } else {
                                rowList.add("FREE");
                            }
                            break;
                        default:
                            rowList.add("UNKNOWN");
                    }
                }
            }
            pythonCompatibleSurroundings.add(rowList);
        }
        return pythonCompatibleSurroundings;
    }

    /**
     * executes the behavior of the chicken, controlled by the script
     *
     * @param map the map, on which the chicken navigates
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
                    String collision = "none";
                    float wishedX = 0.0f;
                    float wishedZ = 0.0f;

                    if ( direction.equals("N") || direction.equals("E") ) {
                        wishedX = this.getX() + (movementX + this.radius);
                        wishedZ = this.getZ() + (movementZ + this.radius);
                    } else {
                        wishedX = this.getX() + (movementX - this.radius);
                        wishedZ = this.getZ() + (movementZ - this.radius);
                    }
                    collision = collisionManager.checkCollision(wishedX, wishedZ, this);
                        if (collision.equals("wall")) {
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

        if (event.getObjectID() != this.objectId) {
            return;
        }

        // logger.info("Event arrived at chicken: " + event.toString());

    }

    public ChickenRecord toRecord() {
        return new ChickenRecord(gameId, objectId, x, y, z, gainedCalories);
    }

    public String toString() {
        return "Chicken";
    }

}
