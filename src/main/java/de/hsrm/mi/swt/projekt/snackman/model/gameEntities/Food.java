package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Food` class represents a non-player Object in the game with
 * an initial position on a plane.
 * 
 * This class extends `NonPlayerObject` and inherits its properties for
 * coordinates management.
 * 
 * 
 * @author Mohamad Hajjar
 */
public class Food extends NonPlayerObject {

    /** The calorie count of the food */
    private int calories;

    /**
     * Constructs a new Food with the specified position and
     * initial calorie count.
     * 
     * @param x        the initial x-coordinate of the Food
     * @param y        the initial y-coordinate of the Food       
     * @param calories  the initial number of calories of the Food
     */
    public Food(float x, float y, int calories) {
        super(x, y);
        this.calories = calories;
    }       

    /**
     * Returns the number of calories of the food.
     * 
     * @return the current calories
     */
    public int getCalories() {
        return calories;
    }
    
}
