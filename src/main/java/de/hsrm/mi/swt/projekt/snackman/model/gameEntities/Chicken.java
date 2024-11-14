package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

/**
 * The `Chicken` class represents a non-player character in the game with
 * an initial position on a plane.
 * 
 * This class extends `NonPlayerObject` and inherits its properties for
 * coordinates management.
 * 
 * 
 * @author Mohamad Hajjar
 */
public class Chicken extends NonPlayerObject {

    /**
     * Constructs a new Chicken with the specified starting position and
     * initial calorie count.
     * 
     * @param x        the initial x-coordinate of the Chicken
     * @param y        the initial y-coordinate of the Chicken       
     * @param calories  the initial number of calories of the Chicken
     */
    public Chicken(int x, int y, int calories) {
        super(x, y,  calories);
    }       
    
}
