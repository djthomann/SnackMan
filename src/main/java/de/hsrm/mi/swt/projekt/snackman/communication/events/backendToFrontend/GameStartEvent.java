package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

/**
 * Event that informs holds all Game/Level data which the frontend needs to
 * display
 */
public class GameStartEvent extends Event {

    private List<SnackMan> snackMen;
    private List<Ghost> ghosts;
    private List<Chicken> chicken;
    private List<Food> foods;
    private SnackManMap map;

    public GameStartEvent(List<SnackMan> snackMen, List<Ghost> ghosts, List<Chicken> chicken, List<Food> foods,
            SnackManMap map) {
        this.snackMen = snackMen;
        this.ghosts = ghosts;
        this.chicken = chicken;
        this.foods = foods;
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

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

}
