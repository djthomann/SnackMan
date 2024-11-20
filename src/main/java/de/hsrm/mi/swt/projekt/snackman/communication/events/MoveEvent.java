package de.hsrm.mi.swt.projekt.snackman.communication.events;

/**
 * Event that informs backend that a Player wants to move and in what direction
 */
public class MoveEvent extends Event {
    
    // TODO: Should be Vector3f
    private int movementVector;

    public MoveEvent(int movementVector) {
        this.movementVector = movementVector;
    }

    public int getMovementVector() {
        return movementVector;
    }

    public void setMovementVector(int movementVector) {
        this.movementVector = movementVector;
    }

}
