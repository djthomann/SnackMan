package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;

/**
 * The interface Subscribable represents an entity capable of subscribing to an event bus,
 * from then on being notified about events that occured within a game.
 * Classes implementing Subscribable can handle those events.
 * 
 */
public interface Subscribable {

    /**
     * The subscriber receives the event from the event bus and handles the event accordingly
     * 
     * @param event
     */
    void handle(Event event);
    
}
