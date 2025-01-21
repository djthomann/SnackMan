package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import de.hsrm.mi.swt.projekt.snackman.logic.GameState;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

@Component
public class ScareEventListener implements ApplicationListener <ScareEvent> {

    Logger logger = LoggerFactory.getLogger(ScareEventListener.class);

    @Override
    public void onApplicationEvent(ScareEvent event) {
        GameState gameState = event.getGameManager().getGameById(event.getGameId()).getGameState();
        switch(event.getSource()) {
            case Chicken chicken -> gameState.addChangedChicken(chicken);
            case SnackMan snackMan -> gameState.addChangedSnackMan(snackMan);
            default -> logger.info("Event not valid.");
        }
    }

}
