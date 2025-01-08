package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.HashMap;
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
        super(id, gameId, x, y, z, gameConfig.getChickenMinRadius());
        this.gameConfig = gameConfig;
        this.collisionManager = collisionManager;
        this.gameManager = gameManager;
        this.gainedCalories = 0;
        // choose script file
        this.scriptInterpreter = new PythonInterpreter();
        String scriptFile = script.equals("test")
                ? "src/main/java/de/hsrm/mi/swt/projekt/snackman/logic/scripts/chickenTestScript.py"
                : script;
        this.scriptInterpreter.execfile(scriptFile);
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
     * executes the behavior of the chicken, controlled by the script
     *
     * @param map the map, on which the chicken navigates
     */
    public void executeScript(SnackManMap map) {

        // TODO the associated tile should be here calculated from Chicken position
        Tile positionTile = map.getTileAt((int) x, (int) z);

        // Retrieve the surrounding tiles as a 3x3 grid
        Tile[][] surroundings = map.getSurroundingTiles(positionTile);

        // Convert the Tile[][] to a Python-compatible List<List<String>>
        HashMap<String, String> pythonCompatibleSurroundings = new HashMap<>();

        for (int row = -1; row <= 1; row++) {
            for (int col = 1; col >= -1; col--) {
                Tile tile = surroundings[row + 1][1 - col];
                String key = "tile_" + row + "_" + col; // unique key for every tile
                pythonCompatibleSurroundings.put(key, (tile != null) ? tile.getOccupationType().name() : "NULL");
            }
        }

        // Set the surroundings in the PythonInterpreter
        scriptInterpreter.set("environment", pythonCompatibleSurroundings);

        // Execute the Python script and retrieve the result
        try {

            scriptInterpreter.exec("result = run_behavior(environment)");
            PyObject result = scriptInterpreter.get("result");

            if (result instanceof PyTuple) {
                PyTuple tuple = (PyTuple) result;

                // Retrieve the elements of the tuple and cast them to float
                float movementX = (float) tuple.get(0);
                float movementY = (float) tuple.get(1);
                float movementZ = (float) tuple.get(2);
                move((movementX), (movementY), (movementZ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
