package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Food` class represents a NP Object in the game.
 * 
 * 
 */
public class Food extends GameObject {

    /** The calorie count contained in the Food */
    private int calories;
    private FoodType foodType;

    /**
     * Constructs a new Food with the specified position and
     * initial calorie count.
     *
     * @param x         the initial x-coordinate of the Food
     * @param z         the initial y-coordinate of the Food
     * @param foodType  the initial type of the Food
     */
    public Food(long gameId, float x, float z, FoodType foodType) {
        super(gameId, x, 0.5f, z);
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

}
