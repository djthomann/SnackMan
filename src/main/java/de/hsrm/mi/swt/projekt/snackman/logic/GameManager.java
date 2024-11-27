package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.WebSocketHandler;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

/**
 * The GameManager manages all of the current games.
 * It passes on incoming events to the game with the respective id.
 * 
 */
public class GameManager {

    Logger logger = LoggerFactory.getLogger(GameManager.class);
    
    public HashMap<Integer, Game> allGames;
    private int nextGameId;
    private WebSocketHandler webSocketHandler;

    public GameManager(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        this.allGames = new HashMap<Integer, Game>();
        this.nextGameId = 0;
    }

    // Constructor for testing purposes with fake game and fake Movables list, to be deleted later
    public GameManager(WebSocketHandler webSocketHandler, String test) {

        logger.info("Game Manager Constructor \n");
        this.webSocketHandler = webSocketHandler;
        this.allGames = new HashMap<Integer, Game>();
        this.nextGameId = 0;

        ArrayList<MovableAndSubscribable> allMoveables = new ArrayList<>();
        allMoveables.add(new SnackMan(0, 0f, 1.1f, 0f, this));

        createGame(new GameConfig(), allMoveables);
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
     *  Notifies the frontend of the changes in the backend
     * 
     * @param event
     */
    public void notifyChange(Event event) {
        webSocketHandler.notifyFrontend(event);
    }

    /**
     *  Creates a new game with a unique id, the specified gameConfig and Moveables,
     *  and a randomly generated map with the in gameConfig specified width and height for it
     * 
     * @param gameConfig
     * @param allMoveables
     */
    public void createGame(GameConfig gameConfig, ArrayList<MovableAndSubscribable> allMoveables) {

        logger.info("Create Game \n");

        SnackManMap map = new SnackManMap(gameConfig.mapWidth, gameConfig.mapHeight);
        Game newGame = new Game(nextGameId, new GameConfig(), allMoveables, map, this);
        allGames.put(newGame.id, newGame);

        nextGameId++;
    }

    /**
     *  Creates a new game with a unique id, the specified gameConfig and Moveables,
     *  and creates a map from the given csv file
     * 
     * @param gameConfig
     * @param allMoveables
     * @param mapFile
     */
    public void createGame(GameConfig gameConfig, ArrayList<MovableAndSubscribable> allMoveables, String mapFile) {

        SnackManMap map = new SnackManMap(mapFile);

        Game newGame = new Game(nextGameId, new GameConfig(), new ArrayList<>(), map, this);
        allGames.put(newGame.id, newGame);

        nextGameId++;
    }

} 
