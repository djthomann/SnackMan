package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.python.core.PyObject;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.EatEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.level.OccupationType;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

/**
 * The `Chicken` class represents a NPC in the game.
 * 
 * 
 */

public class Chicken extends GameObject implements CanEat, MovableAndSubscribable {

    /** The gainedCalorie count of the Chicken */
    private int gainedCalories;

    /** path of behavior script */
    private String behaviorScript;

    /** Jython-Interpreter for the script logic */
    private final PythonInterpreter scriptInterpreter;

    /**
     * Constructs a new Chicken with the id, and specified starting Coords.
     *
     * @param x          the initial x-coordinate of the Chicken
     * @param y          the initial y-coordinate of the Chicken
     * @param z          the initial z-coordinate of the Chicken
     * @param scriptPath the path of the associated behavior script
     * @param map        the game map
     * @param gameConfig the configuration of the game
     */

    public Chicken(long gameId, float x, float y, float z, String scriptPath, SnackManMap map, GameConfig gameConfig) {
        super(gameId, x, y, z, gameConfig.getChickenMinRadius());
        this.behaviorScript = scriptPath;
        this.gainedCalories = 0;
        this.scriptInterpreter = new PythonInterpreter();
        this.scriptInterpreter.execfile(scriptPath);

        // spawn chicken on a random free tile in the map (later in Game-logic)
        Tile[][] tiles = map.getAllTiles();
        List<Tile> freeTiles = new ArrayList<>();

        for (int y_ = 0; y_ < tiles.length; y++) {
            for (int x_ = 0; x < tiles[y_].length; x++) {
                if (tiles[y_][x_].getOccupationType() == OccupationType.FREE) {
                    freeTiles.add(tiles[y_][x_]);
                }
            }

            if (!freeTiles.isEmpty()) {
                Random random = new Random();
                Tile startTile = freeTiles.get(random.nextInt(freeTiles.size()));
                this.x = startTile.getX();
                this.y = startTile.getZ();
                this.z = 0;
            } else {
                throw new IllegalStateException("No free tiles available for Chicken");
            }
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
        EventService.getInstance().applicationEventPublisher.publishEvent(new InternalMoveEvent(this, gameId));
    }

    /**
     * executes the behavior of the chicken, controlled by the script
     *
     * @param map the map, on which the chicken navigates
     */
    public void executeScript(SnackManMap map) {

        // TODO the associated tile should be here calculated from the position of
        // chicken
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
    }

}
