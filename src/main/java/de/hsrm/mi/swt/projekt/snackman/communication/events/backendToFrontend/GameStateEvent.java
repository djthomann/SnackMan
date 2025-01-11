package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.*;

/**
 * Event that informs Frontend about changes in the existence or position of
 * moveable objects and foods
 */
public class GameStateEvent extends Event {

    private List<GhostRecord> updatesGhosts;
    private List<SnackManRecord> updatesSnackMen;
    private List<ChickenRecord> updatesChickens;
    private List<FoodRecord> eatenFoods;
    private long remainingSeconds;

    // gameStateEvent sends any Object or Food where soemthin has changed.
    public GameStateEvent(List<GhostRecord> updatesGhosts, List<SnackManRecord> updatesSnackMen, List<ChickenRecord> updatesChickens,
            List<FoodRecord> updatesFoods, long remainingSeconds) {
        this.setType(EventType.GAME_STATE);
        this.updatesGhosts = updatesGhosts;
        this.updatesSnackMen = updatesSnackMen;
        this.updatesChickens = updatesChickens;
        this.eatenFoods = updatesFoods;
        this.remainingSeconds = remainingSeconds;
    }

    public List<FoodRecord> getEatenFoods() {
        return eatenFoods;
    }

    public void setUpdatesFoods(List<FoodRecord> eatenFoods) {
        this.eatenFoods = eatenFoods;
    }

    public List<GhostRecord> getUpdatesGhosts() {
        return updatesGhosts;
    }

    public void setUpdatesGhosts(List<GhostRecord> updatesGhosts) {
        this.updatesGhosts = updatesGhosts;
    }

    public List<SnackManRecord> getUpdatesSnackMen() {
        return updatesSnackMen;
    }

    public void setUpdatesSnackMen(List<SnackManRecord> updatesSnackMen) {
        this.updatesSnackMen = updatesSnackMen;
    }

    public List<ChickenRecord> getUpdatesChickens() {
        return updatesChickens;
    }


    public void setUpdatesChickens(List<ChickenRecord> updatesChickens) {
        this.updatesChickens = updatesChickens;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

}
