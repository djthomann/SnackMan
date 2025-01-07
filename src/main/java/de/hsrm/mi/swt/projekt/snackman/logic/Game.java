package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.*;

import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameStartEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.FoodType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;
import de.hsrm.mi.swt.projekt.snackman.model.level.OccupationType;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

/**
 * The Game class contains all the information and logic necessary within an individual game.
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
    private Timer timer = new Timer();
    private SnackManMap map;
    private GameEventBus eventBus;
    private GameManager gameManager;
    private CollisionManager collisionManager;
    private GameState gameState;
    private int numSnackmen = 0;
    private int numGhosts = 0;

    

    public Game(long id, GameConfig gameConfig, SnackManMap map, GameManager gameManager) {
        this.id = id;
        this.gameConfig = gameConfig;
        this.map = map;
        this.gameManager = gameManager;
        this.collisionManager = new CollisionManager(this, map, allMovables); //temporary, (this) to be deleted later
        this.timer = new Timer();
        startTimer();
        gameState = new GameState(this);
        logger.info("created Game with id: " + id);
    }

    public Game(Lobby lobby, GameManager gameManager) {
        this.id = lobby.getId();
        this.gameConfig = lobby.getGameConfig();
        this.map = lobby.getMap();
        this.gameManager = gameManager;

        createMovables(lobby.getClientsAsList());
        init(lobby.getClientsAsList());

        this.collisionManager = new CollisionManager(this, map, allMovables);
        startTimer();
        gameState = new GameState(this);
        logger.info("created Game with id: " + id);
    }

    /**
     * creates all
     * @param clients
     */
    private void createMovables(List<Client> clients) {
        for (Client c: clients) {
            switch (c.getRole()) {
                case SNACKMAN -> spawnSnackMan(c.getClientId(), c.getUsername());
                case GHOST -> spawnGhost(c.getClientId(), c.getUsername());
                default -> logger.warn("cannot cope with GameObjectType: " + c.getRole());
            }
        }
    }

    /**
     * spawns ghost with given id, for now every Ghost on the same tile
     * @param id
     */
    private void spawnGhost(long id, String username) {
        allMovables.add(new Ghost(username, id, this.id, (float) map.getW() / 2.0f, 0f, (float) map.getH() / 2.0f, this.gameConfig));
    }

    private void spawnSnackMan(long id, String username) {
        float x;
        float z;
        switch (numSnackmen) {
            case 0 -> {
                x = 1;
                z = 1;
            }
            case 1 -> {
                x = map.getW() - 2;
                z = map.getH() - 2;
            }
            case 2 -> {
                x = 1;
                z = map.getH() - 2;
            }
            case 3 -> {
                x = map.getW() - 2;
                z = 1;
            }
            default -> {
                logger.warn("already 4 Snackmen in Game");
                return;
            }
        }

        x += 0.5f;
        z += 0.5f;

        allMovables.add(new SnackMan(username, id, this.id, x, 0f, z, this.gameManager, this.gameConfig, this.collisionManager));
        numSnackmen++;
    }

    /**
     * Initializes the game by adding the first player entity to the game object
     * list.
     * This method is called right after the game object is created.
     * TODO: This method will be expanded to create all game objects and add them to
     * the game object list.
     */
    public void init(List<Client> clients) {

        // for testing clients is null
        if (clients != null) createMovables(clients);
        createFood();

        // for testing setup test SnackMan
        if (clients == null) allMovables.add(new SnackMan("Snacko", IDGenerator.getInstance().getUniqueID(), id, 20.5f, 1.1f, 20.5f, gameManager,
                gameConfig, collisionManager));
        createChicken();
        ArrayList<Subscribable> subscribers = createSubscriberList();
        this.eventBus = new GameEventBus(subscribers);

        this.gameManager.notifyChange(createGameStartEvent());
    }

    private GameStartEvent createGameStartEvent() {
        GameStartEvent res = new GameStartEvent();
        for (MovableAndSubscribable m: allMovables) {
            switch (m.getClass().getSimpleName()) {
                case "SnackMan" -> res.addSnackMan((SnackMan) m);
                case "Ghost" -> res.addGhost((Ghost) m);
                case "Chicken" -> res.addChicken((Chicken) m);
            }
        }

        res.setMap(map);

        return res;
    }

    private void createFood() {
        Random r = new Random();
        Tile allTiles[][] = map.getAllTiles();
        for (int row = 0; row < map.getH(); row++) {
            for (int col = 0; col < map.getW(); col++) {
                if (allTiles[row][col].getOccupationType() == OccupationType.ITEM) {
                    FoodType foodType = FoodType.OKAY;
                    if (r.nextBoolean()) {
                        if (r.nextBoolean()) {
                            foodType = FoodType.HEALTHY;
                        } else {
                            foodType = FoodType.UNHEALTHY;
                        }
                    }
                    allTiles[row][col].setOccupation(new Food(id, col, row, foodType, gameConfig));
                }
            }
        }
    }

    private void createChicken() {
        Tile tile = map.getTileAt((map.getW() / 2) - 3, (map.getH() / 2) - 3);
        if (tile.getOccupationType() == OccupationType.FREE && tile.getOccupation() == null) {
            Chicken chickenOne = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tile.getX()+0.5f, 0.0f,
                    (float) tile.getZ()+0.5f, "one", gameManager, gameConfig, collisionManager);
            tile.setOccupation(chickenOne.toRecord());
            allMovables.add(chickenOne);
        }   
        Tile secTile = map.getTileAt((map.getW() / 2) + 3, (map.getH() / 2) + 3); 
         if (secTile.getOccupationType() == OccupationType.FREE && secTile.getOccupation() == null) {
            Chicken chickenTwo = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) secTile.getX()+0.5f, 0.0f,
                    (float) secTile.getZ()+0.5f, "one", gameManager, gameConfig, collisionManager);
            secTile.setOccupation(chickenTwo.toRecord());
            allMovables.add(chickenTwo);
        }
    }

    /**
     * Creates a copy of allMovables casting the game objects to Subscribable for
     * the event bus
     *
     * @return allSubscribers
     */
    private ArrayList<Subscribable> createSubscriberList() {

        logger.info("Movables in Game: " + this.allMovables.toString() + "\n");

        ArrayList<Subscribable> allSubscribers = new ArrayList<>();

        for (MovableAndSubscribable currentGameObject : this.allMovables) {
            allSubscribers.add((Subscribable) currentGameObject);
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
     * Passes the event to the event bus which notifies all subscribers about the
     * event
     * 
     * @param event
     */
    public void receiveEvent(Event event) {
        logger.info("event received by game\n");
        logger.info("Subscribers: " + eventBus.getSubscribers().toString());
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

    public SnackManMap getMap() {
        return this.map; 
    }

}
