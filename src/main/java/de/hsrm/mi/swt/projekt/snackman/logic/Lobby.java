package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.IDGenerator;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

public class Lobby {
    
    Logger logger = LoggerFactory.getLogger(Lobby.class);
    private final long id = IDGenerator.getInstance().getUniqueID();
    private GameConfig gameConfig;
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
        return new Game(this, gameManager);
    }

}
