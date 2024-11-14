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
public class Chicken extends NonPlayerObject implements Moveable, CanEat {

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

    /**
     * moves chicken to new coords
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    @Override
    public void move(int x, int y) {
    }
    
    /**
     * Consumes the food, affecting the weight (calories) of the entity
     *
     * @param food the calorie resource to be consumed by the entity
     */
    @Override
    public void eat(Food food) {
    } 
    
    /*      
    @Override
    public void handle(Event event) {
    } */
    
}
