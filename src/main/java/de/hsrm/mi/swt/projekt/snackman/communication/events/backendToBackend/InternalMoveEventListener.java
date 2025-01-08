package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import de.hsrm.mi.swt.projekt.snackman.logic.GameState;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.GameObject;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

@Component
public class InternalMoveEventListener implements ApplicationListener <InternalMoveEvent> {

    Logger logger = LoggerFactory.getLogger(EatEventListener.class);

    //TODO: check if Singletons work

    @Override
    public void onApplicationEvent(InternalMoveEvent event) {
        GameState gameState = event.getGameManager().getGameById(((GameObject)event.getSource()).getGameId()).getGameState();
        switch(event.getSource()) {
            case Chicken chicken -> gameState.addChangedChicken(chicken);
            case SnackMan snackMan -> gameState.addChangedSnackMan(snackMan);
            case Ghost ghost -> gameState.addChangedGhost(ghost);
            default -> logger.info("Event not valid.");
        }
    }
    
}
