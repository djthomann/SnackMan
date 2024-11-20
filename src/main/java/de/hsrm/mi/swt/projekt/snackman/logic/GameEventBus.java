package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;

public class GameEventBus {

    ArrayList<Subscribable> subscribers;

    public GameEventBus() {
        this.subscribers = new ArrayList<Subscribable>();
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
    public void sendEventToSubscribers(String type, int objectId) {

        for (Subscribable currentSubscribable : subscribers) {
            currentSubscribable.handle(type, objectId);
        }
    }
    
}
