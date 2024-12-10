package de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend;
import org.joml.Vector3f;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;

/**
 * Event that informs backend that a Player wants to move and in what direction
 */
public class MoveEvent extends Event {
    
    private Vector3f movementVector;

    // Added setType to constructor
    public MoveEvent(Vector3f movementVector) {
        this.setType(EventType.MOVE);
        this.movementVector = movementVector;
    }

    public Vector3f getMovementVector() {
        return movementVector;
    }

    public void setMovementVector(Vector3f movementVector) {
        this.movementVector = movementVector;
    }

}
