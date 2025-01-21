package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameEndEvent;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.WebSocketHandler;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;

/**
 * The GameManager manages all the current games.
 * It passes on incoming events to the game with the respective id.
 * 
 */
public class GameManager {

    Logger logger = LoggerFactory.getLogger(GameManager.class);
    
    private final Map<Long, Game> allGames = new HashMap<>();
    private final Map<Long, Lobby> allLobbies = new HashMap<>();
    private final WebSocketHandler webSocketHandler;
    private final GameConfig gameConfig = new GameConfig();
    private GameEndEvent lastGameEndEvent;
    private SnackManMap lastMapOriginal;

    public GameManager(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public Game getGameById(Long id) {
        return allGames.getOrDefault(id, null);
    }

    /**
     * Passes on the received event to the game with the matching game objectId
     * 
     * @param event event to be handled
     */
    public void handleEvent(Event event) {
        logger.info("handleEvent\n");

        if (allGames.containsKey(event.getGameID())) {
            allGames.get(event.getGameID()).receiveEvent(event);
        } else {
            logger.warn("no game with id " + event.getGameID() + " in GameManager");
        }
    }

    /**
     * Notifies the frontend of the changes in the backend
     * 
     * @param event event to be converted to json and sent so FE
     */
    public void notifyChange(Client client, Event event) {
        webSocketHandler.notifyFrontend(client, event);
    }

    public String[][] getPlayersInLobby(long lobbyCode) {
        Lobby lobby = this.allLobbies.get(lobbyCode);
        return lobby.getClientsAsList().stream()
                .map(client -> new String[]{client.getUsername(), String.valueOf(client.getClientId()), String.valueOf(client.getRole())})
                .toArray(String[][]::new);
    }

    public void addClientToLobby(Client c, long lobbyCode) {
        this.allLobbies.get(lobbyCode).addClient(c);
    }

    public void removeClientFromLobby(Client c, long lobbyCode) {
        this.allLobbies.get(lobbyCode).removeClient(c);
    }

    public void createGame(long id) {
        Lobby lobby = allLobbies.get(id);
        logger.info("Lobby started game: " + lobby.toString());
        Game game = lobby.startGame(this);
        allGames.put(game.getId(), game);
    }

    public void removeGame(long id) {
        allGames.remove(id);
    }

    public void setGameConfig(GameConfig gameConfig, long lobbyID) {
        allLobbies.get(lobbyID).setGameConfig(gameConfig);
    }

    public GameConfig getGameConfig(long lobbyID) {
        if (allLobbies.get(lobbyID) == null) {
            return this.gameConfig;
        }
        return allLobbies.get(lobbyID).getGameConfig();
    }

    public Lobby createLobby(){
        Lobby lobby = new Lobby();
        allLobbies.put(lobby.getId(), lobby);
        //createGame(gameConfig, lobby.getId());
        return lobby;
    }

    public void removeLobby(long id) {
        allLobbies.remove(id);
    }

    public Lobby getLobbyFromClient(Client c) {
        for (Lobby lobby: allLobbies.values()) {
            for (Client client: lobby.getClientsAsList()) {
                if (client.getClientId() == c.getClientId()) {
                    return lobby;
                }
            }
        }
        return null;
    }

    public List<Lobby> getAllLobbies() {
        return new ArrayList<>(allLobbies.values());
    }

    public Map<Long, Lobby> getLobbyMap() {
        return allLobbies;
    }

    public Lobby getLobbyById(long id) {
        return allLobbies.get(id);
    }

    public GameEndEvent getLastGameEndEvent() {
        return lastGameEndEvent;
    }

    public void setLastGameEndEvent(GameEndEvent lastGameEndEvent) {
        this.lastGameEndEvent = lastGameEndEvent;
    }

    public SnackManMap getLastMapOriginal() {
        return lastMapOriginal;
    }

    public void setLastMapOriginal(SnackManMap lastMapOriginal) {
        this.lastMapOriginal = lastMapOriginal;
    }
}
