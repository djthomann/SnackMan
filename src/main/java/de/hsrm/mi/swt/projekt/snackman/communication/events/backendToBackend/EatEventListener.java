package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.logic.GameState;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class EatEventListener implements ApplicationListener <EatEvent> {

    Logger logger = LoggerFactory.getLogger(EatEventListener.class);

    @Autowired
    GameManager gameManager;

    @Override
    public void onApplicationEvent(EatEvent event) {
        GameState gameState = gameManager.getGameById(event.getGameId()).getGameState();
        switch(event.getSource()) {
            case Chicken chicken -> gameState.addChangedChicken(chicken);
            case SnackMan snackMan -> gameState.addChangedSnackMan(snackMan);
            default -> logger.info("Event not valid.");
        }
        gameState.addEatenFood(event.getFood());
    }
}
