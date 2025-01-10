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
    private List<ChickenRecord> chickens;
    private SnackManMapRecord map;
    private long gameTime;

    public GameStartEvent(List<SnackManRecord> snackMen, List<GhostRecord> ghosts, List<ChickenRecord> chickens, SnackManMapRecord map, long gameTime) {
        this.setType(EventType.GAME_START);
        this.snackMen = snackMen;
        this.ghosts = ghosts;
        this.chickens = chickens;
        this.map = map;
        this.gameTime = gameTime;
    }

    public GameStartEvent() {
        this.setType(EventType.GAME_START);
        this.snackMen = new ArrayList<>();
        this.chickens = new ArrayList<>();
        this.ghosts = new ArrayList<>();
    }

    public void addSnackMan(SnackMan s) {
        this.snackMen.add(s.toRecord());
    }

    public void addGhost(Ghost g) {
        this.ghosts.add(g.toRecord());
    }

    public void addChicken(Chicken c) {
        this.chickens.add(c.toRecord());
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

    public List<ChickenRecord> getChickens() {
        return chickens;
    }

    public void setChickens(List<ChickenRecord> chickens) {
        this.chickens = chickens;
    }

    public SnackManMapRecord getMap() {
        return map;
    }

    public void setMap(SnackManMapRecord map) {
        this.map = map;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }
    
}
