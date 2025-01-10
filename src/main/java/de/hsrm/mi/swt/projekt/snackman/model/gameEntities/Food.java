package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.FoodRecord;

/**
 * The `Food` class represents a NP Object in the game.
 * 
 * 
 */
public class Food extends GameObject {

    /** The calorie count contained in the Food */
    private int calories;

    /** The type of the Food */
    private FoodType foodType;

    /**
     * Constructs a new Food with the specified position and
     * initial calorie count.
     *
     * @param x          the initial x-coordinate of the Food
     * @param z          the initial y-coordinate of the Food
     * @param foodType   the initial type of the Food
     * @param gameConfig the configuration of the game
     */
    public Food(long gameId, float x, float z, FoodType foodType, GameConfig gameConfig) {
        super(gameId, x, 0.5f, z, gameConfig.getFoodRadius(), gameConfig.getFoodHeight());
        this.foodType = foodType;
        this.calories = foodType.getCalories();

    }

    /**
     * Returns the number of contained Calories in the Food.
     * 
     * @return the current contained calories
     */
    public int getCalories() {
        return calories;
    }

    /**
     * Returns record of Food.
     */
    public FoodRecord toRecord() {
        return new FoodRecord(gameId, objectId, x, y, z, calories, foodType);
    }

    // String representation used for chickenssurroundings
    public String toString() {
        return "FOOD";
    }

}
