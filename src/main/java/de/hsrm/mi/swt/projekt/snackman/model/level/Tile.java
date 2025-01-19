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
    private List<Food> foodsOnTile;

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
        this.foodsOnTile = new ArrayList<Food>(); 
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
            this.foodsOnTile.add((Food) occupation);
            occupationType = OccupationType.ITEM;
        }
    }
    
    public void removeFromOccupation(GameObject occupation) {
        if (occupations.contains(occupation) && occupation != null) {
            occupations.remove(occupation); 
        }
        if (occupation instanceof Food) {
            foodsOnTile.remove(occupation); 

            if (foodsOnTile.size() == 0) {
                this.occupationType = OccupationType.FREE; 
            }
        }
    }

    public TileRecord toRecord () {
        List<FoodRecord> foodRecords = new ArrayList<FoodRecord>();
        if (foodsOnTile.size() > 0) {
            for (Food food: foodsOnTile) {
                foodRecords.add(food.toRecord());
            }
        }
        FoodRecord[] foodRecordsArray = foodRecords.toArray(new FoodRecord[foodRecords.size()]);
        return new TileRecord(x, z, occupationType, foodRecordsArray);
    }

    public List<Food> getFoodsOnTile() {
        return foodsOnTile;
    }
}
