package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

public interface Subscribable {

    /**
     * The subscriber receives the event from the event bus and handles the event accordingly
     * @param type
     * @param objectId
     */
    void handle(String type, int objectId);
    
}
