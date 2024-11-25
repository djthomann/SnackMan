package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

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
    
    public ArrayList<Game> allGames;
    private int nextGameId;
    private WebSocketHandler webSocketHandler;

    public GameManager(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        this.allGames = new ArrayList<Game>();
        this.nextGameId = 0;
    }

    /**
     * Passes on the received event to the game with the matching game id
     * 
     * @param event
     */
    public void handleEvent(Event event) {

        for (Game currentGame : allGames) {

            if(currentGame.id == event.getGameID()) {
                currentGame.receiveEvent(event);
            }
        }
    }

    /**
     *  Creates a new game with a unique id, the specified gameConfig and Moveables,
     *  and a randomly generated map with the in gameConfig specified width and height for it
     * 
     * @param gameConfig
     * @param allMoveables
     */
    public void createGame(GameConfig gameConfig, ArrayList<MovableAndSubscribable> allMoveables) {

        SnackManMap map = new SnackManMap(gameConfig.mapWidth, gameConfig.mapHeight);
        Game newGame = new Game(nextGameId, new GameConfig(), new ArrayList<>(), map);
        allGames.add(newGame);

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

        Game newGame = new Game(nextGameId, new GameConfig(), new ArrayList<>(), map);
        allGames.add(newGame);

        nextGameId++;
    }

} 
