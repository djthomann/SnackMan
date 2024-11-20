package de.hsrm.mi.swt.projekt.snackman.communication.events;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

public class MapData {
    private List<Tile> tiles;
    private List<Tile> walls;
    private List<Food> foods;

    public MapData(List<Tile> tiles, List<Tile> walls, List<Food> foods) {
        this.tiles = tiles;
        this.walls = walls;
        this.foods = foods;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public List<Tile> getWalls() {
        return walls;
    }

    public void setWalls(List<Tile> walls) {
        this.walls = walls;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
    
}
