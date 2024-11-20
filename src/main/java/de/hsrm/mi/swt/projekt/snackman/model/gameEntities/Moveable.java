package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;
/**
 * The Moveable interface represents an entity capable of movement.
 * Classes implementing this interface can be moved to specified x and y coordinates.
 * 
 * 
 */
public interface Moveable {
    /**
     * Moves the entity to a specified position.
     *
     * @param x the x-coordinate to move the entity to
     * @param y the y-coordinate to move the entity to
     */
    void move(float x, float y); 

    /**
     * Handles a specified event (e.g. jump event, move event).
     *
     * @param event the event to handle
     */
    
     // Removed method handle and created own interface Subscribable for the handle method
}
