package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    /**
     * Constructs a new Chicken with the id, and specified starting Coords.
     * 
     * @param id            the unique identifier of the Chicken
     * @param x             the initial x-coordinate of the Chicken
     * @param y             the initial y-coordinate of the Chicken       
     * @param z             the initial z-coordinate of the Chicken    
     */
    public Chicken(int id, float x, float y, float z, Map map) { // map should be removed later
        this.id = id; 
        // this.x = x; 
        // this.y = y; 
        // this.z = z; 
        this.gainedCalories = 0;

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
     * Consumes the food, make the Chicken gain Calories.
     *
     * @param food the calorie resource to be consumed by the entity
     */
    @Override
    public void eat(Food food) {
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
