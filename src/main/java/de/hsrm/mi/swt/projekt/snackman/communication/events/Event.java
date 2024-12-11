package de.hsrm.mi.swt.projekt.snackman.communication.events;

/**
 * Abstract super class for Events i.e. messages that are exchanged between Frontend and Backend
 */
public abstract class Event {

    private EventType type;
    private long gameID;
    private long objectID;

    public EventType getType() {
        return type;
    }
    public void setType(EventType type) {
        this.type = type;
    }
    public long getGameID() {
        return gameID;
    }
    public void setGameID(long gameID) {
        this.gameID = gameID;
    }
    public long getObjectID() {
        return objectID;
    }
    public void setObjectID(long objectID) {
        this.objectID = objectID;
    }

}
