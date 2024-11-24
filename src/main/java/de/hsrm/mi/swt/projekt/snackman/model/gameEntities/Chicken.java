package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.python.core.PyObject;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;
import de.hsrm.mi.swt.projekt.snackman.model.level.Map;
import de.hsrm.mi.swt.projekt.snackman.model.level.OccupationType;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

/**
 * The `Chicken` class represents a NPC in the game.
 * 
 * 
 */
public class Chicken implements Moveable, CanEat {

    /** The unique identifier for the Chicken */
    private final int id;

    /** The x-coordinate of the Chicken */
    private float x;

    /** The y-coordinate of the Chicken */
    private float y;    
    
    /** The z-coordinate of the Chicken */
    private float z;

    /** The gainedCalorie count of the Chicken */
    private int gainedCalories;

    /** path of behavior script */
    private String behaviorScript; 

    /** Jython-Interpreter for the script logic */
    private final PythonInterpreter scriptInterpreter; 


    /**
     * Constructs a new Chicken with the id, and specified starting Coords.
     * 
     * @param id            the unique identifier of the Chicken
     * @param x             the initial x-coordinate of the Chicken
     * @param y             the initial y-coordinate of the Chicken       
     * @param z             the initial z-coordinate of the Chicken    
     * @param scriptPath    the path of the associated behavior script    
     */
    public Chicken(int id, float x, float y, float z, String scriptPath, Map map) { // map should be removed later
        this.id = id; 
        // this.x = x; 
        // this.y = y; 
        // this.z = z; 
        this.behaviorScript = scriptPath;
        this.gainedCalories = 0;
        this.scriptInterpreter = new PythonInterpreter();  
        this.scriptInterpreter.execfile(scriptPath);

        // spawn chicken on a random free tile in the map (later in Game-logic)
        Tile[][] tiles = map.getAllTiles(); 
        List <Tile> freeTiles = new ArrayList<>(); 

        for (int y_ = 0; y_ < tiles.length; y++) {
            for (int x_ = 0; x < tiles[y_].length; x++) {
                if (tiles[y_][x_].getOccupationType() == OccupationType.FREE) {
                    freeTiles.add(tiles[y_][x_]); 
                }
            }

            if(!freeTiles.isEmpty()) {
                Random random = new Random(); 
                Tile startTile = freeTiles.get(random.nextInt(freeTiles.size())); 
                this.x = startTile.getX(); 
                this.y = startTile.getY(); 
                this.z = 0; 
            }
            else {
                throw new IllegalStateException("No free tiles available for Chicken"); 
            }
        }
    }

    /**
     * Moves chicken to new coords
     * 
     * @param newX the x-coordinate to move the Chicken to
     * @param newY the y-coordinate to move the Chicken to
     * @param newZ the z-coordinate (remains the same)
     */
    @Override
    public void move(float newX, float newY, float newZ) {
        x = newX; 
        y = newY; 
        z = newZ; 
    }

    /**
     * executes the behavior of the chicken, controlled by the script
     * 
     * @param map the map, on which the chicken navigates 
     */
    public void executeScript(Map map) {

        // TODO the associated tile should be here calculated from the position of chicken
        Tile positionTile = map.getTileAt((int) x, (int) y); 

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
                move((x + movementX), (y + movementY), (z + movementZ)); 
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

    /**
     * Returns the unique identifier of the Chicken.
     * 
     * @return the `id` of the Chicken
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the Chicken.
     * 
     * @return the current x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the Chicken.
     * 
     * @return the current y-coordinate
     */
    public float getY() {
        return y;
    }    
    
    /**
    * Returns the z-coordinate of the Chicken.
    * 
    * @return the current z-coordinate
    */
   public float getZ() {
       return z;
   }

    /*      
    @Override
    public void handle(Event event) {}
    */
}
