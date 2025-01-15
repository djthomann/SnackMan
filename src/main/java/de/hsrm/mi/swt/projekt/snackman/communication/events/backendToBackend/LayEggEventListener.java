package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import de.hsrm.mi.swt.projekt.snackman.logic.GameState;

@Component
public class LayEggEventListener implements ApplicationListener <LayEggEvent> {

    Logger logger = LoggerFactory.getLogger(LayEggEventListener.class);

    @Override
    public void onApplicationEvent(LayEggEvent event) {
        GameState gameState = event.getGameManager().getGameById(event.getGameId()).getGameState();
        gameState.addLaidEgg(event.getFood());
        logger.info("LayEggListener aufgerufen");
    }
}
