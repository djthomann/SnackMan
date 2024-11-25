package de.hsrm.mi.swt.projekt.snackman.communication.events;
import org.joml.Vector3f;

/**
 * Event that informs backend that a Player wants to move and in what direction
 */
public class MoveEvent extends Event {
    
    private Vector3f movementVector;

    // Added setType to constructor
    public MoveEvent(Vector3f movementVector) {
        type = EventType.MOVE;
        this.movementVector = movementVector;
        this.setType(EventType.MOVE);
    }

    public Vector3f getMovementVector() {
        return movementVector;
    }

    public void setMovementVector(Vector3f movementVector) {
        this.movementVector = movementVector;
    }

}
