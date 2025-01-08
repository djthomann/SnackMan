package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import java.util.ArrayList;
import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.*;

/**
 * Event that informs and holds all Game/Level data which the frontend needs to
 * display and manage. Map holds all Food Data itself.
 */
public class GameStartEvent extends Event {

    private List<SnackManRecord> snackMen;
    private List<GhostRecord> ghosts;
    private List<ChickenRecord> chicken;
    private SnackManMapRecord map;

    public GameStartEvent(List<SnackManRecord> snackMen, List<GhostRecord> ghosts, List<ChickenRecord> chicken, SnackManMapRecord map) {
        this.setType(EventType.GAME_START);
        this.snackMen = snackMen;
        this.ghosts = ghosts;
        this.chicken = chicken;
        this.map = map;
    }

    public GameStartEvent() {
        this.setType(EventType.GAME_START);
        this.snackMen = new ArrayList<>();
        this.chicken = new ArrayList<>();
        this.ghosts = new ArrayList<>();
    }

    public void addSnackMan(SnackMan s) {
        this.snackMen.add(s.toRecord());
    }

    public void addGhost(Ghost g) {
        this.ghosts.add(g.toRecord());
    }

    public void addChicken(Chicken c) {
        this.chicken.add(c.toRecord());
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

    public SnackManMapRecord getMap() {
        return map;
    }

    public void setMap(SnackManMapRecord map) {
        this.map = map;
    }
}
