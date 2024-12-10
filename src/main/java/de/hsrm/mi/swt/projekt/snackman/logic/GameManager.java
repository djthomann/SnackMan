package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.WebSocketHandler;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

/**
 * The GameManager manages all of the current games.
 * It passes on incoming events to the game with the respective id.
 * 
 */
public class GameManager {

    Logger logger = LoggerFactory.getLogger(GameManager.class);
    
    public HashMap<Long, Game> allGames;
    private long nextGameId;
    private WebSocketHandler webSocketHandler;
    private GameConfig gameConfig = new GameConfig();
    private ArrayList<MovableAndSubscribable> allMoveables = new ArrayList<>();


    public GameManager(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        this.allGames = new HashMap<Long, Game>();
        this.nextGameId = 0;
    }

    // TODO: To Be Deleted , Constructor for testing purposes with fake game
    public GameManager(WebSocketHandler webSocketHandler, String test) {

        logger.info("Game Manager Constructor \n");
        this.webSocketHandler = webSocketHandler;
        this.allGames = new HashMap<Long, Game>();
        this.nextGameId = 0;

        GameConfig gameConfig = new GameConfig();

        createGame(gameConfig);
    }

    public Game getGameById(Long id) {
        if (allGames.containsKey(id)) {
            return allGames.get(id);
        } else {
            return null;
        }
    }

    /**
     * Passes on the received event to the game with the matching game id
     * 
     * @param event
     */
    public void handleEvent(Event event) {

        logger.info("handleEvent\n");

        if (allGames.containsKey(event.getGameID())) {

            allGames.get(event.getGameID()).receiveEvent(event);

        }
    }

    /**
     * Notifies the frontend of the changes in the backend
     * 
     * @param event
     */
    public void notifyChange(Event event) {
        webSocketHandler.notifyFrontend(event);
    }

    /**
     * Creates a new game with a unique id, the specified gameConfig and Moveables,
     * and a randomly generated map with the in gameConfig specified width and
     * height for it
     * 
     * @param gameConfig
     * @param allMoveables
     */
    public void createGame(GameConfig gameConfig) {

        logger.info("Create Game \n");

        // SnackManMap map = new SnackManMap(gameConfig.mapWidth, gameConfig.mapHeight);
        SnackManMap map = new SnackManMap("map_2024-11-26_19_17_39.csv", true);
        // SnackManMap map = new SnackManMap(MapGenerationConfig.SAVED_MAPS_PATH + "testFile.csv", true);
        Game newGame = new Game(nextGameId, new GameConfig(), map, this);
        newGame.init(); // Add Snackman
        allGames.put(newGame.id, newGame);

        nextGameId++;
    }

    /**
     * Creates a new game with a unique id, the specified gameConfig and Moveables,
     * and creates a map from the given csv file
     * 
     * @param gameConfig
     * @param allMoveables
     * @param mapFile
     */
    public void createGame(GameConfig gameConfig, ArrayList<MovableAndSubscribable> allMoveables, String mapFile) {

        SnackManMap map = new SnackManMap(mapFile, true);

        Game newGame = new Game(nextGameId, new GameConfig(), map, this);
        allGames.put(newGame.id, newGame);

        nextGameId++;
    }

    public void setGameConfig(GameConfig gameConfig, int gameID) {
        // TODO: Funktioniert nur mit id: 0, solange LobbyID nicht mit GameID verknüpft ist
        allGames.get(gameID).setGameConfig(gameConfig);
    }

    public GameConfig getGameConfig(int gameID) {
        if (allGames.get(gameID) == null) {
            return this.gameConfig;
        }
        return allGames.get(gameID).getGameConfig(); 
    }

}
