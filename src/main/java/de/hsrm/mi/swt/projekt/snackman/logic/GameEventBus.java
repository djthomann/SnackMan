package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;

public class GameEventBus {

    private ArrayList<Subscribable> subscribers;

    public GameEventBus(ArrayList<Subscribable> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * Registers a new subscriber who gets notified about events
     * 
     * @param newSubscriber
     */
    public void registerSubcriber(Subscribable newSubscriber) {
        this.subscribers.add(newSubscriber);
    }

    /**
     * Notifies all subscribers about the new event
     * The subscribers handle the event individually
     * 
     * @param type
     * @param objectId
     */
    public void sendEventToSubscribers(Event event) {

        for (Subscribable currentSubscribable : subscribers) {
            currentSubscribable.handle(event);
        }
    }
    
}
