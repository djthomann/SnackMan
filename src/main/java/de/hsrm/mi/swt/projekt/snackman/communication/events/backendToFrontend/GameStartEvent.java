package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

/**
 * Event that informs and holds all Game/Level data which the frontend needs to
 * display and manage. Map holds all Food Data itself.
 */
public class GameStartEvent extends Event {

    private List<SnackMan> snackMen;
    private List<Ghost> ghosts;
    private List<Chicken> chicken;
    private SnackManMap map;

    public GameStartEvent(List<SnackMan> snackMen, List<Ghost> ghosts, List<Chicken> chicken, List<Food> foods,
            SnackManMap map) {
        this.snackMen = snackMen;
        this.ghosts = ghosts;
        this.chicken = chicken;
        this.map = map;
    }

    public List<SnackMan> getSnackMen() {
        return snackMen;
    }

    public void setSnackMen(List<SnackMan> snackMen) {
        this.snackMen = snackMen;
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public void setGhosts(List<Ghost> ghosts) {
        this.ghosts = ghosts;
    }

    public List<Chicken> getChicken() {
        return chicken;
    }

    public void setChicken(List<Chicken> chicken) {
        this.chicken = chicken;
    }

    public SnackManMap getMap() {
        return map;
    }

    public void setMap(SnackManMap map) {
        this.map = map;
    }

}
