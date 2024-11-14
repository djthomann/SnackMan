package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;
/**
 * The Moveable interface represents an entity capable of movement.
 * Classes implementing this interface can be moved to specified x and y coordinates.
 * @author Abdallah Jaber
 */
public interface Moveable {
    /**
     * Moves the entity to a specified position within the 2D coordinate system.
     *
     * @param x the x-coordinate to move the entity to
     * @param y the y-coordinate to move the entity to
     */
    void move(int x, int y); 

    // /**
    //  * Handles a specified event (e.g. jump event, move event).
    //  *
    //  * @param event the event to handle
    //  */
    // void handle(Event event);
}
