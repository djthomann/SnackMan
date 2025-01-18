package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;

/**
 * The GameEventBus notifies all of its subscribers about an event that occured.
 * 
 */
public class GameEventBus {

    private final ArrayList<Subscribable> subscribers;

    public GameEventBus(ArrayList<Subscribable> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * Registers a new subscriber who gets notified about events
     * 
     * @param newSubscriber subscriber to be added
     */
    public void registerSubscriber(Subscribable newSubscriber) {
        this.subscribers.add(newSubscriber);
    }

    /**
     * Notifies all subscribers about the new event
     * The subscribers handle the event individually
     * 
     * @param event event to be sent
     */
    public void sendEventToSubscribers(Event event) {

        for (Subscribable currentSubscribable : subscribers) {
            currentSubscribable.handle(event);
        }
    }

    /**
     * Returns the current list of all subscribers from this event bus
     * 
     * @return subscribers
     */
    public ArrayList<Subscribable> getSubscribers() {
        return subscribers;
    }
    
}
