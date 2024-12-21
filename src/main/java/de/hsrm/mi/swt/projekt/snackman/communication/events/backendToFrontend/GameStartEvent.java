package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.*;

/**
 * Event that informs and holds all Game/Level data which the frontend needs to
 * display and manage. Map holds all Food Data itself.
 */
public class GameStartEvent extends Event {

    private List<SnackManRecord> snackMen;
    private List<GhostRecord> ghosts;
    private List<ChickenRecord> chicken;
    private SnackManMap map;

    public GameStartEvent(List<SnackManRecord> snackMen, List<GhostRecord> ghosts, List<ChickenRecord> chicken,
            SnackManMap map) {
        this.setType(EventType.GAME_START);
        this.snackMen = snackMen;
        this.ghosts = ghosts;
        this.chicken = chicken;
        this.map = map;
    }

    public List<SnackManRecord> getSnackMen() {
        return snackMen;
    }

    public void setSnackMen(List<SnackManRecord> snackMen) {
        this.snackMen = snackMen;
    }

    public List<GhostRecord> getGhosts() {
        return ghosts;
    }

    public void setGhosts(List<GhostRecord> ghosts) {
        this.ghosts = ghosts;
    }

    public List<ChickenRecord> getChicken() {
        return chicken;
    }

    public void setChicken(List<ChickenRecord> chicken) {
        this.chicken = chicken;
    }

    public SnackManMap getMap() {
        return map;
    }

    public void setMap(SnackManMap map) {
        this.map = map;
    }

}
