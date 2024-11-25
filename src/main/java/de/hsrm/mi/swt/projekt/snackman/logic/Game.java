package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;
import de.hsrm.mi.swt.projekt.snackman.model.level.Map;

/**
 * The Game class contains all of the information and logic necessary within an individual game.
 * The game starts as soon as the Game class is instantiated.
 * Every game has a map and its own game objects. 
 * It can receive and pass on events to subscribers to its event bus.
 * 
 */
public class Game {

    public int id;
    private GameConfig gameConfig;
    private ArrayList<MovableAndSubscribable> allMovables;
    //private ArrayList<Food> allFoods; TODO might not be required here and only in Map
    private Timer timer;
    private Map map;
    private GameEventBus eventBus;
    

    public Game(int id, GameConfig gameConfig, ArrayList<MovableAndSubscribable> allMoveables, Map map) {
        this.id = id;
        this.gameConfig = gameConfig;
        this.allMovables = allMoveables;
        this.timer = new Timer();
        this.map = map;
        this.eventBus = new GameEventBus(createSubscriberList());
        startTimer();
    }

    /**
     * Creates a copy of allMovables casting the game objects to Subscribable for the event bus
     * 
     * @return allSubscribers
     */
    private ArrayList<Subscribable> createSubscriberList() {

        ArrayList<Subscribable> allSubscribers = new ArrayList<>();

        for(MovableAndSubscribable currentGameObject : this.allMovables) {
            allSubscribers.add((Subscribable)currentGameObject);
        }

        return allSubscribers;
    }

    /**
     * Starts the timer as soon as the game starts
     * If the timer is finished, the game stops
     * 
     */
    private void startTimer() {

        TimerTask task;
         task = new TimerTask() {
            
            @Override
            public void run() {
                stopGame();
            }
        };

        // Multiply by 1000 to get the needed milliseconds for timer.schedule
        long delay = gameConfig.gameTime * 1000;

        timer.schedule(task, delay);
    }

    private void stopGame() {

    }

    /**
     * Passes the event to the event bus which notifies all subscribers about the event
     * 
     * @param event
     */
    public void receiveEvent(Event event) {
        eventBus.sendEventToSubscribers(event);
    }

    /**
     * Returns the current list of all subscribers from its event bus 
     * 
     * @return subscribers from eventBus
     */
    public ArrayList<Subscribable> getAllSubscribers() {
        return eventBus.getSubscribers();
    }

}
