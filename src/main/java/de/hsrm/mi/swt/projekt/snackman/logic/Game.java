package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.*;

import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameStartEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.WebSocketHandler;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.*;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
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
    private final GameConfig gameConfig;
    private final ArrayList<MovableAndSubscribable> allMovables = new ArrayList<>();
    private Timer timer = new Timer();
    private final SnackManMap map;
    private GameEventBus eventBus;
    private final GameManager gameManager;
    private final CollisionManager collisionManager;
    private final GameState gameState;
    private int numSnackmen = 0;
    private GameStartEvent gameStartEvent;
    private long startTime;

    // Constructor for Game with no Lobby
    public Game(long id, GameConfig gameConfig, SnackManMap map, GameManager gameManager) {
        this.id = id;
        this.gameConfig = gameConfig;
        this.map = map;
        this.gameManager = gameManager;
        this.collisionManager = new CollisionManager(this, map, allMovables); //temporary, (this) to be deleted later
        this.Initialize(null);
        this.timer = new Timer();
        startTimer();
        gameState = new GameState(this);
        logger.info("created Game with id: " + id);
    }

    // Constructor for Game with Lobby
    public Game(Lobby lobby, GameManager gameManager) {
        logger.info("in Game Constructor");
        this.id = lobby.getId();
        this.gameConfig = lobby.getGameConfig();
        this.map = lobby.getMap();
        this.gameManager = gameManager;
        this.collisionManager = new CollisionManager(this, map, allMovables);
        Initialize(lobby.getClientsAsList()); 
        startTimer();
        gameState = new GameState(this);
        logger.info("created Game with id: " + id);
    }

    /**
     * creates all Movables from connected Clients in Lobby
     * @param clients said clients
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
     * @param id said id
     */
    private void spawnGhost(long id, String username) {
        allMovables.add(new Ghost(username, id, this.id, (float) map.getW() / 2.0f, 0.5f, (float) map.getH() / 2.0f, this.gameConfig, this.gameManager, this.collisionManager));
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

        allMovables.add(new SnackMan(username, id, this.id, x, 0.5f, z, this.gameManager, this.gameConfig, this.collisionManager));
        numSnackmen++;
    }

    /**
     * Initializes the game by adding the first player entity to the game object
     * list.
     * This method is called right after the game object is created.
     * TODO: This method will be expanded to create all game objects and add them to
     * the game object list.
     */
    public void Initialize(List<Client> clients) {

        if (clients != null) {
            createMovables(clients);
        } else {
            // for testing setup test SnackMan
            allMovables.add(new SnackMan("Snacko", IDGenerator.getInstance().getUniqueID(), id, 20.5f, 1.1f, 20.5f, 
            gameManager, gameConfig, collisionManager));
            // Ghost to test collision
            allMovables.add(new Ghost("spookie", IDGenerator.getInstance().getUniqueID(), id, 16.5f, 1.1f, 20.5f, 
            gameConfig, gameManager, collisionManager));
        }

        // Initialize food and chicken
        createFood();
        createChicken();

        // Setup Event Bus and Subscribers
        ArrayList<Subscribable> subscribers = createSubscriberList();
        this.eventBus = new GameEventBus(subscribers);

        // Notify Game Start if clients are provided
        if (clients != null) {
            this.gameManager.notifyChange(createGameStartEvent());
        }
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
        res.setMap(map.toRecord());
        res.setGameTime(gameConfig.getGameTime());

        this.gameStartEvent = res;

        return res;
    }

    private void createFood() {
        Random r = new Random();
        Tile[][] allTiles = map.getAllTiles();
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

    // TODO:adjust the spawning chicken depending on the gameconfig
    private void createChicken() { 
        int chickenCount = gameConfig.getChickenCount();
        if (chickenCount < 0 || chickenCount > 4) {
            logger.info("Invalid chickenCount in GameConfig: " + chickenCount + ". Allowed range is [0-4]. Setting to 4.");
            chickenCount = 4;
        }
        if (chickenCount >= 1) {
            Tile tileOne = map.getTileAt((map.getW() / 2) + 3, (map.getH() / 2) + 3);
            if (tileOne.getOccupationType() == OccupationType.FREE && tileOne.getOccupation() == null) {
                Chicken chickenOne = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tileOne.getX()+0.5f,
                0.0f, (float) tileOne.getZ()+0.5f, "one", gameManager, gameConfig, collisionManager);
                tileOne.setOccupation(chickenOne.toRecord());
                allMovables.add(chickenOne);
            }
        }
        if (chickenCount >= 2) {
            Tile tileTwo = map.getTileAt((map.getW() / 2) - 4, (map.getH() / 2) - 4);
            if (tileTwo.getOccupationType() == OccupationType.FREE && tileTwo.getOccupation() == null) {
                Chicken chickenTwo = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tileTwo.getX()+0.5f,
                0.0f, (float) tileTwo.getZ()+0.5f, "one", gameManager, gameConfig, collisionManager);
                tileTwo.setOccupation(chickenTwo.toRecord());
                allMovables.add(chickenTwo);
            }
        }
        if (chickenCount >= 3) {
            Tile tileThree = map.getTileAt((map.getW() / 2) + 3, (map.getH() / 2) - 4);
            if (tileThree.getOccupationType() == OccupationType.FREE && tileThree.getOccupation() == null) {
                Chicken chickenThree = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tileThree.getX()+0.5f,
                0.0f, (float) tileThree.getZ()+0.5f, "one", gameManager, gameConfig, collisionManager);
                tileThree.setOccupation(chickenThree.toRecord());
                allMovables.add(chickenThree);
            }
        }
        if (chickenCount >= 4) {
            Tile tileFour = map.getTileAt((map.getW() / 2) - 4, (map.getH() / 2) + 3);   
            if (tileFour.getOccupationType() == OccupationType.FREE && tileFour.getOccupation() == null) {
                Chicken chickenFour = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tileFour.getX()+0.5f,
                0.0f, (float) tileFour.getZ()+0.5f, "one", gameManager, gameConfig, collisionManager);
                tileFour.setOccupation(chickenFour.toRecord());
                allMovables.add(chickenFour);
            }
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
        long delay = gameConfig.getGameTime() * 1000L;

        timer.schedule(task, delay);

        startTime = System.currentTimeMillis();
    }

    public long getElapsedMillis() {
        return System.currentTimeMillis() - startTime;
    }

    public long getRemainingSeconds() {
        return gameConfig.getGameTime() - getElapsedMillis() / 1000;
    }

    private void stopGame() {

    }

    /**
     * Passes the event to the event bus which notifies all subscribers about the
     * event
     * 
     * @param event the event to be published
     */
    public void receiveEvent(Event event) {
        logger.info("event received by game\n");
        logger.info("Subscribers: " + eventBus.getSubscribers().toString());

        eventBus.sendEventToSubscribers(event);

        // Testing mode means not starting a game via Lobby
        if (WebSocketHandler.testingMode) {
            float newX = ((SnackMan) this.allMovables.get(0)).getX();
            float newY = ((SnackMan) this.allMovables.get(0)).getY();
            float newZ = ((SnackMan) this.allMovables.get(0)).getZ();
            MoveEvent moveEvent = new MoveEvent(new Vector3f(newX, newY, newZ));

            this.gameManager.notifyChange(moveEvent);
        }

    }

    public GameState getGameState() {
        return gameState;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameStartEvent getGameStartEvent() {
        return gameStartEvent;
    }

    //adjust the cases,in case of changing the Occupation in the Tile of the map to records!
    public List<List<String>> generateSurroundings(float x, float z) {
        Tile positionTile = this.map.getTileAt((int) x, (int) z);
        Tile[][] surroundings = this.map.getSurroundingTiles(positionTile);
        List<List<String>> pythonCompatibleSurroundings = new ArrayList<>();

        for (int row = 1; row >= -1; row--) {
            List<String> rowList = new ArrayList<>();
            for (int col = -1; col <= 1; col++) {
                Tile tile = surroundings[row + 1][col + 1];
                if (tile == null) {
                    rowList.add("OUT");
                } else {
                    switch (tile.getOccupationType()) {
                        case WALL:
                            rowList.add("WALL");
                            break;
                        case ITEM:
                            if ( tile.getOccupation() != null && tile.getOccupation().getClass().getSimpleName().equals("Food")) {
                                rowList.add("FOOD");
                            } else {
                                rowList.add("Unknown ITEM");
                            }
                            break;
                        case FREE:
                            if (tile.getOccupation() != null) {
                                switch (tile.getOccupation().getClass().getSimpleName()) {
                                    case "ChickenRecord":
                                        rowList.add("CHICKEN");
                                        break;
                                    case "Ghost":
                                        rowList.add("GHOST");
                                        break;
                                    case "SnackMan":
                                        rowList.add("SNACKMAN");
                                        break;
                                    default:
                                        rowList.add("FREE");
                                        break;
                                }
                            } else {
                                rowList.add("FREE");
                            }
                            break;
                        default:
                            rowList.add("Unknown");
                            break;
                    }
                }
            }
            pythonCompatibleSurroundings.add(rowList);
        }
        return pythonCompatibleSurroundings;
    }

}
