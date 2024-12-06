package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;
/**
 * The Moveable interface represents an entity capable of movement.
 * Classes implementing this interface can be moved to specified x and y coordinates.
 * 
 * 
 */
public interface Moveable {
   
    int getId();
    void move(float x, float y, float z); 
    float getX();
    float getY();
    float getZ();
}
