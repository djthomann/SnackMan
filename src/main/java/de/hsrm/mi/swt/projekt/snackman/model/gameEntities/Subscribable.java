package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;

public interface Subscribable {

    /**
     * The subscriber receives the event from the event bus and handles the event accordingly
     * @param type
     * @param objectId
     */
    void handle(Event event);
    
}
