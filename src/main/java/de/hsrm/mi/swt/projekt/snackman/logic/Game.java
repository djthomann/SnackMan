package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameEndEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameStartEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.FoodType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.GameObject;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.IDGenerator;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.PlayerRecord;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.SnackManRecord;
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
    private final Timer timer = new Timer();
    private final SnackManMap map;
    private GameEventBus eventBus;
    private final GameManager gameManager;
    private final CollisionManager collisionManager;
    private final GameState gameState;
    private int numSnackmen = 0;
    private long startTime;

    // Constructor for Game with Lobby
    public Game(Lobby lobby, GameManager gameManager) {
        logger.info("in Game Constructor");
        this.id = lobby.getId();
        this.gameConfig = lobby.getGameConfig();
        this.map = lobby.getMap();
        this.gameManager = gameManager;
        this.collisionManager = new CollisionManager(this, map, allMovables);
        initialize(lobby.getClientsAsList()); 
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
        Ghost ghost = new Ghost(username, id, this.id, (float) map.getW() / 2.0f, 0, (float) map.getH() / 2.0f, this.gameConfig, this.gameManager, this.collisionManager);
        allMovables.add(ghost);
        map.getTileAt((int) ghost.getX(), (int) ghost.getZ()).addToOccupation(ghost);
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

        SnackMan snackMan = new SnackMan(username, id, this.id, x, 0, z, this.gameManager, this.gameConfig, this.collisionManager);
        allMovables.add(snackMan);
        map.getTileAt((int) x, (int) z).addToOccupation(snackMan);
        numSnackmen++;
    }

    /**
     * Initializes the game by adding the first player entity to the game object
     * list.
     * This method is called right after the game object is created.
     * TODO: This method will be expanded to create all game objects and add them to
     * the game object list.
     */
    public void initialize(List<Client> clients) {

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
                    allTiles[row][col].addToOccupation(new Food(id, (float) col +0.5f, (float) row + 0.5f, foodType, gameConfig));
                }
            }
        }
    }

    private void createChicken() { 
        int chickenCount = gameConfig.getChickenCount();
        if (chickenCount < 0 || chickenCount > 4) {
            logger.info("Invalid chickenCount in GameConfig: " + chickenCount + ". Allowed range is [0-4]. Setting to 4.");
            chickenCount = 4;
        }
        if (chickenCount >= 1) {

            Tile tileOne = map.getTileAt((map.getW() / 2) + 3, (map.getH() / 2) + 3);
            if (tileOne.getOccupationType() == OccupationType.FREE && tileOne.getOccupations().isEmpty()) {
                Chicken chickenOne = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tileOne.getX()+0.5f,
                0.0f, (float) tileOne.getZ()+0.5f, "ChickenPersonalityOne", gameManager, gameConfig, collisionManager);
                tileOne.addToOccupation(chickenOne);
                allMovables.add(chickenOne);
            }
        }
        if (chickenCount >= 2) {

            Tile tileTwo = map.getTileAt((map.getW() / 2) - 4, (map.getH() / 2) - 4);
            if (tileTwo.getOccupationType() == OccupationType.FREE && tileTwo.getOccupations().isEmpty()) {
                Chicken chickenTwo = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tileTwo.getX()+0.5f,
                0.0f, (float) tileTwo.getZ()+0.5f, "ChickenPersonalityTwo", gameManager, gameConfig, collisionManager);
                tileTwo.addToOccupation(chickenTwo);
                allMovables.add(chickenTwo);
            }
        }
        if (chickenCount >= 3) {

            Tile tileThree = map.getTileAt((map.getW() / 2) + 3, (map.getH() / 2) - 4);
            if (tileThree.getOccupationType() == OccupationType.FREE && tileThree.getOccupations().isEmpty()) {
                Chicken chickenThree = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tileThree.getX()+0.5f,
                0.0f, (float) tileThree.getZ()+0.5f, "ChickenPersonalityOne", gameManager, gameConfig, collisionManager);
                tileThree.addToOccupation(chickenThree);
                allMovables.add(chickenThree);
            }
        }
        if (chickenCount >= 4) {

            Tile tileFour = map.getTileAt((map.getW() / 2) - 4, (map.getH() / 2) + 3);   
            if (tileFour.getOccupationType() == OccupationType.FREE && tileFour.getOccupations().isEmpty()) {
                Chicken chickenFour = new Chicken(IDGenerator.getInstance().getUniqueID(), id, (float) tileFour.getX()+0.5f,
                0.0f, (float) tileFour.getZ()+0.5f, "ChickenPersonalityTwo", gameManager, gameConfig, collisionManager);
                tileFour.addToOccupation(chickenFour);
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

        logger.info("Movables in Game: " + this.allMovables + "\n");

        return new ArrayList<>(this.allMovables);
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
        logger.info("TIMER ENDED");
        sendGameEndEvent();
    }

    /**
     * Determines the winner based on the highest score.
     *
     * @param scores A map of player IDs to their scores.
     * @return The ID of the player with the highest score.
     */
    private long determineWinner(Map<Long, Integer> scores) {

        logger.info("Given Scores: " + scores.toString());

        long winnerId = -1;
        int highestScore = Integer.MIN_VALUE;
        for (Map.Entry<Long, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                winnerId = entry.getKey();
            }
        }
        return winnerId;
    }

    private void sendGameEndEvent() {
        // Create Hashmap for Scores
        Map<Long, Integer> scores = new HashMap<>();

        // Determine lobby (works)
        Lobby lobby = this.gameManager.getLobbyById(this.id);

        if(lobby != null) {
            logger.info("Lobby found with id: " + lobby.getId());
        }
        
        // Determine clients (works)
        List<Client> clientsAsList = lobby.getClientsAsList();
        
        for (Client c : clientsAsList) {
            logger.info("Client in Lobby: " + c.toString());
        }

        /* 
        // TODO For some reason, the changedSnackMen are empty
        for (SnackManRecord snackManRecord : this.gameState.getChangedSnackMen()){
            long id = snackManRecord.objectId();
            int gainedCalories = snackManRecord.gainedCalories();
            scores.put(id, gainedCalories);
        }

        if (scores.isEmpty()) {
            logger.info("No scores found");
        }
        */

        // Mock scores
        scores.put((long)2, 500);

        long winnerId = determineWinner(scores);

        // does it calculate the winner correctly?
        logger.info("Winner Client ID: " + winnerId);

        String winnerName = lobby.getClient(winnerId).getUsername();
        String winnerTeam = lobby.getClient(winnerId).getRole().toString();
        int winnerCaloryCount = scores.get(winnerId);

        List<PlayerRecord> playerRecords = new ArrayList<>();

        for (Client c : clientsAsList) {
            long id = c.getClientId();
            String username = c.getUsername();
            int score = scores.get(id);
            playerRecords.add(new PlayerRecord(username, c.getRole().toString(), score));
        }

        // Create GameEndEvent with winner and scores
        GameEndEvent gameEndEvent = new GameEndEvent(winnerTeam, winnerName, winnerCaloryCount, playerRecords);

        // Send the event to the frontend
        gameManager.notifyChange(gameEndEvent);
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
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameManager getGameManager() {
        return gameManager;
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
                        case WALL -> rowList.add("WALL");
                        case ITEM, FREE -> {
                            if (!tile.getOccupations().isEmpty()) {
                                String highestPriority = "UNKNOWN OCCUPATION"; // default priority

                                for (GameObject go : tile.getOccupations()) {
                                    String className = go.getClass().getSimpleName();

                                    // priority from top to bottom (Ghost > SnackMan > Chicken > Food)
                                    switch (className) {
                                        case "Ghost" -> highestPriority = "GHOST";
                                        case "SnackMan" -> {
                                            if (!highestPriority.equals("GHOST")) {
                                                highestPriority = "SNACKMAN";
                                            }
                                        }
                                        case "Chicken" -> {
                                            if (!highestPriority.equals("GHOST") && !highestPriority.equals("SNACKMAN")) {
                                                highestPriority = "CHICKEN";
                                            }
                                        }
                                        case "Food" -> {
                                            if (!highestPriority.equals("GHOST") && !highestPriority.equals("SNACKMAN") && !highestPriority.equals("CHICKEN")) {
                                                highestPriority = "FOOD";
                                            }
                                        }
                                        default -> {
                                            if (highestPriority.equals("UNKNOWN OCCUPATION")) {
                                                highestPriority = "UNKNOWN OCCUPATION";
                                            }
                                        }
                                    }
                                }
                                rowList.add(highestPriority);
                            } else {
                                rowList.add("FREE");
                            }
                        }
                    }
                }
            }
            pythonCompatibleSurroundings.add(rowList);
        }
        return pythonCompatibleSurroundings;
    }

    public void updateTileOccupation(GameObject gameObject, float oldX, float oldZ, float newX, float newZ) {
        if ((int) oldX != (int) newX || (int) oldZ != (int) newZ ) { 
            Tile oldTile = map.getTileAt((int) oldX, (int) oldZ); 
            Tile newTile = map.getTileAt((int) newX, (int) newZ); 
            newTile.addToOccupation(gameObject);
            oldTile.removeFromOccupation(gameObject); 

            if(newTile.getOccupationType() == OccupationType.WALL || oldTile.getOccupationType() == OccupationType.WALL) {
                return;
            }
            // Item-occupationType should only be changed in collisionManager to make sure collision with food works
            if (newTile.getOccupationType() == OccupationType.ITEM) {
                return; 
            }
            // when snackman's entered the old tile but did not collide with food on it
            if (oldTile.getOccupationType() == OccupationType.ITEM) {
                return;
            }
            else {
                oldTile.setOccupationType(OccupationType.FREE);
            }
        }
    }

    public SnackManMap getMap() {
        return map;
    }
    
}
