package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.logic.GameState;

public class LayEggEventListener implements ApplicationListener <LayEggEvent> {

    Logger logger = LoggerFactory.getLogger(LayEggEventListener.class);

    @Autowired
    GameManager gameManager;

    @Override
    public void onApplicationEvent(LayEggEvent event) {
        GameState gameState = gameManager.getGameById(event.getGameId()).getGameState();
        gameState.addLaidEgg(event.getFood());
    }
}
