package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.IDGenerator;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

public class Lobby {
    
    Logger logger = LoggerFactory.getLogger(Lobby.class);

    private IDGenerator idGenerator = IDGenerator.getInstance();
    private long id = idGenerator.getUniqueID();
    private GameConfig gameConfig;
    private SnackManMap map;
    private List<String> players = new ArrayList<>();
    
    public Lobby(GameConfig gameConfig, SnackManMap map) {
        this.gameConfig = gameConfig;
        this.map = map;
        this.players = new ArrayList<String>();
    }

    public Lobby() {
        this.gameConfig = null;
        this.map = null;
        this.players = new ArrayList<String>();
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

}
