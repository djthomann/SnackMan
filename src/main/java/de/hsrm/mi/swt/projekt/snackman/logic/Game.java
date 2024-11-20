package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Moveable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;

public class Game {

    public int id;
    private GameConfig gameConfig;
    private ArrayList<MovableAndSubscribable> allMovables;
    //private ArrayList<Food> allFoods; vllt reicht es, wenn man es nur in der Map hat
    private Timer timer;
    //private Map map;
    private GameEventBus eventBus;
    

    public Game(int id, GameConfig gameConfig, ArrayList<MovableAndSubscribable> allMoveables/*, Map map*/) {
        this.id = id;
        this.gameConfig = gameConfig;
        this.allMovables = allMoveables;
        this.timer = new Timer();
        //this.map = map;
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

}
