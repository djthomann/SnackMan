package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

/**
 * The Game class contains all of the information and logic necessary within an individual game.
 * The game starts as soon as the Game class is instantiated.
 * Every game has a map and its own game objects. 
 * It can receive and pass on events to subscribers to its event bus.
 * 
 */
public class Game {

    Logger logger = LoggerFactory.getLogger(Game.class);

    public long id;
    private GameConfig gameConfig;
    private ArrayList<MovableAndSubscribable> allMovables = new ArrayList<>();
    //private ArrayList<Food> allFoods; TODO might not be required here and only in Map
    private Timer timer;
    private SnackManMap map;
    private GameEventBus eventBus;
    private GameManager gameManager;
    private CollisionManager collisionManager;
    private GameState gameState;
    
    

    public Game(long id, GameConfig gameConfig, SnackManMap map, GameManager gameManager) {
        this.id = id;
        this.gameConfig = gameConfig;
        this.map = map;
        this.gameManager = gameManager;
        this.collisionManager = new CollisionManager(map,allMovables);
        this.timer = new Timer();   
        startTimer();
        gameState = new GameState(this);
    }

    /**
     * Initializes the game by adding the first player entity to the game object list.
     * This method is called right after the game object is created.
     * TODO: This method will be expanded to create all game objects and add them to the game object list.
     */
    public void init() {
        allMovables.add(new SnackMan(id,20.0f, 1.1f, 20.0f, gameManager, gameConfig,collisionManager));
        ArrayList<Subscribable> subscribers = createSubscriberList();
        this.eventBus = new GameEventBus(subscribers);
    }

    /**
     * Creates a copy of allMovables casting the game objects to Subscribable for the event bus
     * @return allSubscribers
     */
    private ArrayList<Subscribable> createSubscriberList() {

        logger.info("Movables in Game: " + this.allMovables.toString() + "\n");

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
        long delay = gameConfig.getGameTime() * 1000;

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
        logger.info("event received by game\n");
        logger.info("Subscribers: " + eventBus.getSubscribers().toString());
        eventBus.sendEventToSubscribers(event);
        

        //Create new move event with the new position of the SnackMan that is sent back to the frontend for testing purposes
        /*float newX = ((SnackMan)this.allMovables.get(0)).getX();
        float newY = ((SnackMan)this.allMovables.get(0)).getY();
        float newZ = ((SnackMan)this.allMovables.get(0)).getZ();
        MoveEvent moveEvent = new MoveEvent(new Vector3f(newX, newY, newZ));

        this.gameManager.notifyChange(moveEvent);*/
    }

    /**
     * Returns the current list of all subscribers from its event bus 
     * 
     * @return subscribers from eventBus
     */
    public ArrayList<Subscribable> getAllSubscribers() {
        return eventBus.getSubscribers();
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

}
