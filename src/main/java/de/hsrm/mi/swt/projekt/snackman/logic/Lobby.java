package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.LobbyRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.IDGenerator;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import org.springframework.web.socket.WebSocketSession;

public class Lobby {
    
    Logger logger = LoggerFactory.getLogger(Lobby.class);
    private final long id = IDGenerator.getInstance().getUniqueID();
    private GameConfig gameConfig = new GameConfig();
    private SnackManMap map;
    private Map<Long, Client> clientMap = new HashMap<>();

    public List<Client> getClientsAsList() {
        return clientMap.values().stream().toList();
    }

    public void addClient(Client c) {
        clientMap.put(c.getClientId(), c);
    }

    public long getId() {
        return id;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

    public SnackManMap getMap() {
        return map;
    }

    public void setMap(SnackManMap map) {
        this.map = map;
    }

    public Game startGame(GameManager gameManager) {
        if (map == null) map = new SnackManMap(this.gameConfig.getMapWidth(), this.gameConfig.getMapHeight());
        Game game = new Game(this, gameManager); 
        
        return game;
    }

    public List<WebSocketSession> getAllSessions() {
        return this.getClientsAsList().stream().map(Client::getSession).toList();
    }

    public LobbyRecord toRecord() {
        return new LobbyRecord(this.getClientsAsList().size(), this.id);
    }

}
