package de.hsrm.mi.swt.projekt.snackman.model.level;

import java.util.ArrayList;
import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.GameObject;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.FoodRecord;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.TileRecord;

public class Tile {

    private final int x;
    private final int z;
    private OccupationType occupationType;
    private Food foodOnTile;

    //private GameObject occupation;
    private final List<GameObject> occupations;

    /**
     * Constructor, creates Tile with given parameters:
     * @param x x-coordinate of Tile (final)
     * @param z z-coordinate of Tile (final)
     * @param occupationType OccupationType of Tile
     */
    public Tile(int x, int z, OccupationType occupationType) {
        this.x = x;
        this.z = z;
        this.occupationType = occupationType;
        this.occupations = new ArrayList<>();
    }

    public OccupationType getOccupationType() {
        return occupationType;
    }

    public void setOccupationType(OccupationType occupationType) {
        this.occupationType = occupationType;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public List <GameObject> getOccupations() {
        return occupations;
    }
    public void addToOccupation(GameObject occupation) {
        if (occupation == null) {
            return;
        } 
        this.occupations.add(occupation);
        if (occupation instanceof Food) {
            this.foodOnTile = (Food) occupation;
            occupationType = OccupationType.ITEM;
        }
    }
    
    public void removeFromOccupation(GameObject occupation) {
        if (occupations.contains(occupation) && occupation != null) {
            occupations.remove(occupation); 
        }
    }

    public TileRecord toRecord () {
        FoodRecord foodRecord = null;
        if (foodOnTile != null) {
            foodRecord = foodOnTile.toRecord();
        }
        return new TileRecord(x, z, occupationType, foodRecord);
    }

    public Food getFoodOnTile() {
        return foodOnTile;
    }

}
