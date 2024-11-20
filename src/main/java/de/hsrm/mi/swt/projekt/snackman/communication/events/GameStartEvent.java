package de.hsrm.mi.swt.projekt.snackman.communication.events;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Moveable;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

/**
 * Event that informs holds all Game/Level data which the frontend needs to display
 */
public class GameStartEvent extends Event {
    
    private List<Moveable> moveables;
    private List<Food> foods;
    private int floor; // TODO: No Data Format defined yet
    private List<Tile> tiles;

    public GameStartEvent(List<Moveable> moveables, List<Food> foods, int floor, List<Tile> tiles) {
        this.moveables = moveables;
        this.foods = foods;
        this.floor = floor;
        this.tiles = tiles;
    }
    public List<Moveable> getMoveables() {
        return moveables;
    }
    public void setMoveables(List<Moveable> moveables) {
        this.moveables = moveables;
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
