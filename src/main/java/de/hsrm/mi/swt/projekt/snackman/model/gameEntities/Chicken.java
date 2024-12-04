package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.EatEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend.InternalMoveEvent;

/**
 * The `Chicken` class represents a NPC in the game.
 * 
 * 
 */
public class Chicken implements CanEat, MovableAndSubscribable {

        /** Publisher to publish the internal backend event. */
     @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

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
     * @param id       the unique identifier of the Chicken
     * @param x        the initial x-coordinate of the Chicken
     * @param y        the initial y-coordinate of the Chicken       
     * @param z        the initial z-coordinate of the Chicken       
     */
    public Chicken(int id,float x, float y, float z) {
        this.id = id; 
        this.x = x; 
        this.y = y; 
        this.z = z; 
        this.gainedCalories = 0;
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
        applicationEventPublisher.publishEvent(new InternalMoveEvent(this));
    }

    
    /**
     * Consumes the food, make the Chicken gain Calories.
     *
     * @param food the calorie resource to be consumed by the entity
     */
    @Override
    public void eat(Food food) {
        applicationEventPublisher.publishEvent(new EatEvent(this, food));
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

    @Override
    public void handle(Event event) {
    }

}
