package de.hsrm.mi.swt.projekt.snackman.model.level;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MapTests {

    Map map;
    int w = 10;
    int h = 5;

    @BeforeEach
    void setUp() {
        map = new Map(10, 5);
    }

    @Test
    void testGhostSpawn() {
        Assertions.assertEquals(map.getAllTiles()[h / 2][w / 2].getOccupationType(), OccupationType.FREE);
    }

    @Test
    void testPlayerSpawn() {
        Assertions.assertEquals(map.getAllTiles()[1][1].getOccupationType(), OccupationType.FREE);
        Assertions.assertEquals(map.getAllTiles()[h - 2][1].getOccupationType(), OccupationType.FREE);
        Assertions.assertEquals(map.getAllTiles()[1][w - 2].getOccupationType(), OccupationType.FREE);
        Assertions.assertEquals(map.getAllTiles()[h - 2][w - 2].getOccupationType(), OccupationType.FREE);
    }

    @Test
    void testDimensions() {
        Assertions.assertEquals(map.getAllTiles().length, h);
        Assertions.assertEquals(map.getAllTiles()[0].length, w);
    }

    @Test
    void testOuterWall() {
        Tile[][] tiles = map.getAllTiles();
        for (int row = 0; row < h; row++) {
            if (row > 0 && row < h - 1) {
                Assertions.assertEquals(tiles[row][0].getOccupationType(), OccupationType.WALL);
                Assertions.assertEquals(tiles[row][w - 1].getOccupationType(), OccupationType.WALL);
            } else {
                for (int col = 0; col < w; col++) {
                    Assertions.assertEquals(tiles[row][col].getOccupationType(), OccupationType.WALL);
                }
            }
        }
    }

}
