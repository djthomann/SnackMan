package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Moveable;

public class Game {

    public int id;
    private GameConfig gameConfig;
    private ArrayList<Moveable> allMovables;
    //private ArrayList<Food> allFoods; vllt reicht es, wenn man es nur in der Map hat
    private Timer timer;
    //private Map map;
    private GameEventBus eventBus;
    

    public Game(int id, GameConfig gameConfig, ArrayList<Moveable> allMoveables/*, Map map*/) {
        this.id = id;
        this.gameConfig = gameConfig;
        this.allMovables = allMoveables;
        this.timer = new Timer();
        //this.map = map;
        this.eventBus = new GameEventBus();
        startTimer();
    }

    private void startTimer() {
        TimerTask task;
         task = new TimerTask() {
            
            @Override
            public void run() {
                stopGame();
            }
        };

        // multiply by 1000 to get the needed milliseconds for timer.schedule
        long delay = gameConfig.gameTime * 1000;

        timer.schedule(task, delay);
    }

    private void stopGame() {

    }

    private void notifyAllMoveables(/*Event event*/) {
        
    }

    public void receiveEvent(String event, int objectId) {

    }
    
    

}
