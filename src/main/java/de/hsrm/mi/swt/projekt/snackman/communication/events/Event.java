package de.hsrm.mi.swt.projekt.snackman.communication.events;

/**
 * Abstract super class for Events i.e. messages that are exchanged between Frontend and Backend
 */
public abstract class Event {

    private EventType type;
    private int gameID;
    private int objectID;

    public EventType getType() {
        return type;
    }
    public void setType(EventType type) {
        this.type = type;
    }
    public int getGameID() {
        return gameID;
    }
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
    public int getObjectID() {
        return objectID;
    }
    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

}
