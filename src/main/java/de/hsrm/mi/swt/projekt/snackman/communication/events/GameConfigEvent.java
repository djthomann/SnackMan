package de.hsrm.mi.swt.projekt.snackman.communication.events;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;

public class GameConfigEvent extends Event{

    private GameConfig gameConfig;

    public GameConfigEvent() {
        super();
        setType(EventType.GAME_CONFIG);
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

}
