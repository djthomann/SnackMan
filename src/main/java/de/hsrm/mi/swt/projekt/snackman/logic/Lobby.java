package de.hsrm.mi.swt.projekt.snackman.logic;

import org.glassfish.jaxb.core.v2.model.core.ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.IDFactory;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class Lobby {
    
    Logger logger = LoggerFactory.getLogger(Lobby.class);

    IDFactory idFactory = IDFactory.getInstance();
    private long id = idFactory.getUniqueID();

    private GameConfig gameConfig;
    private SnackManMap map;

    // Players
    


    public Lobby(GameConfig gameConfig, SnackManMap map) {
        this.gameConfig = gameConfig;
        this.map = map;
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
