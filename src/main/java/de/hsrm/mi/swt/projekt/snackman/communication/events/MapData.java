package de.hsrm.mi.swt.projekt.snackman.communication.events;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

public class MapData {
    private List<Tile> tiles;
    private List<Tile> walls;

    public MapData(List<Tile> tiles, List<Tile> walls) {
        this.tiles = tiles;
        this.walls = walls;
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
}
