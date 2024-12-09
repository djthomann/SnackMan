package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.logic.GameState;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class InternalMoveEventListener implements ApplicationListener <InternalMoveEvent> {

    Logger logger = LoggerFactory.getLogger(EatEventListener.class);

    @Autowired
    GameManager gameManager;

    @Override
    public void onApplicationEvent(InternalMoveEvent event) {
        GameState gameState = gameManager.getGameById(event.getGameId()).getGameState();
        switch(event.getSource()) {
            case Chicken chicken -> gameState.addChangedChicken(chicken);
            case SnackMan snackMan -> gameState.addChangedSnackMan(snackMan);
            case Ghost ghost -> gameState.addChangedGhost(ghost);
            default -> logger.info("Event not valid.");
        }
    }
    
}
