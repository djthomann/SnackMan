package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Moveable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

/**
 * Event that informs Frontend about changes in the existence or position of
 * moveable objects and foods
 */
public class GameStateEvent extends Event {

    private List<Ghost> updatesGhosts;
    private List<SnackMan> updatesSnackMen;
    private List<Chicken> updatesChicken;
    private List<Food> eatenFoods;

    // gameStateEvent sends any Object or Food where soemthin has changed.
    public GameStateEvent(List<Ghost> updatesGhosts, List<SnackMan> updatesSnackMen, List<Chicken> updatesChicken,
            List<Food> updatesFoods) {
        this.updatesGhosts = updatesGhosts;
        this.updatesSnackMen = updatesSnackMen;
        this.updatesChicken = updatesChicken;
        this.eatenFoods = updatesFoods;
    }

    public List<Food> getEatenFoods() {
        return eatenFoods;
    }

    public void setUpdatesFoods(List<Food> eatenFoods) {
        this.eatenFoods = eatenFoods;
    }

    public List<Ghost> getUpdatesGhosts() {
        return updatesGhosts;
    }

    public void setUpdatesGhosts(List<Ghost> updatesGhosts) {
        this.updatesGhosts = updatesGhosts;
    }

    public List<SnackMan> getUpdatesSnackMen() {
        return updatesSnackMen;
    }

    public void setUpdatesSnackMen(List<SnackMan> updatesSnackMen) {
        this.updatesSnackMen = updatesSnackMen;
    }

    public List<Chicken> getUpdatesChicken() {
        return updatesChicken;
    }

    public void setUpdatesChicken(List<Chicken> updatesChicken) {
        this.updatesChicken = updatesChicken;
    }

}
