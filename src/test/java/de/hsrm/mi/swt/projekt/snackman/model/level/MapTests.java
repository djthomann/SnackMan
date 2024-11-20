package de.hsrm.mi.swt.projekt.snackman.model.level;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class MapTests {

    Map map;
    int w = 15;
    int h = 10;

    @BeforeEach
    void setUp() {
        map = new Map(w, h);
    }

    /**
     * Testet, ob ein mittleres Tile frei ist
     */
    @Test
    void testGhostSpawn() {
        Assertions.assertEquals(map.getAllTiles()[h / 2][w / 2].getOccupationType(), OccupationType.FREE);
    }

    /**
     * Testet, ob alle 4 Ecken-Tiles frei sind
     */
    @Test
    void testPlayerSpawn() {
        Assertions.assertEquals(map.getAllTiles()[1][1].getOccupationType(), OccupationType.FREE);
        Assertions.assertEquals(map.getAllTiles()[h - 2][1].getOccupationType(), OccupationType.FREE);
        Assertions.assertEquals(map.getAllTiles()[1][w - 2].getOccupationType(), OccupationType.FREE);
        Assertions.assertEquals(map.getAllTiles()[h - 2][w - 2].getOccupationType(), OccupationType.FREE);
    }

    /**
     * Testet die Map-Größe
     */
    @Test
    void testDimensions() {
        Assertions.assertEquals(map.getAllTiles().length, h);
        Assertions.assertEquals(map.getAllTiles()[0].length, w);
    }

    /**
     * Testet, ob die Map von einer Wand umrandet ist
     */
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

    @Test
    void testSaveAsCSVCreatesNewFile() {
        File dir = new File("src/main/resources/savedMaps");
        if (dir.exists() && dir.isDirectory()) {
            int numFilesBefore = dir.list().length;
            map.saveAsCSV();
            int numFilesAfter = Objects.requireNonNull(dir.listFiles()).length;
            Assertions.assertEquals(numFilesBefore + 1, numFilesAfter);

            if (!Objects.requireNonNull(Arrays.stream(dir.listFiles())
                    .max((file1, file2) -> Long.compare(file1.lastModified(), file2.lastModified()))
                    .orElse(null)).delete()) {
                fail("file could not be deleted");
            }

        } else {
            fail("directory not found");
        }
    }

    @Test
    void testSaveAsCSVSavedFileIsCorrect() {
        map.saveAsCSV();

        File dir = new File("src/main/resources/savedMaps");
        if (dir.exists() && dir.isDirectory()) {

            File newFile = Arrays.stream(dir.listFiles())
                    .max((file1, file2) -> Long.compare(file1.lastModified(), file2.lastModified()))
                    .orElse(null);

            Assertions.assertNotNull(newFile);
            try (BufferedReader reader = new BufferedReader(new FileReader(newFile.getPath()))) {
                String line;
                int lines = 0;
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    Assertions.assertEquals(w, tokens.length);

                    for (int col = 0; col < w; col++) {
                        switch (map.getTileAt(col, lines).getOccupationType()) {
                            case FREE -> Assertions.assertEquals("0", tokens[col]);
                            case WALL -> Assertions.assertEquals("-1", tokens[col]);
                            case ITEM -> Assertions.assertEquals("1", tokens[col]);
                            default -> fail("unknown OccupationType in map: " + map.getTileAt(col, lines).getOccupationType());
                        }
                    }
                    lines++;
                }
                Assertions.assertEquals(h, lines);
                if (!newFile.delete()) {
                    fail("file could not be deleted");
                }
            } catch (IOException e) {
                fail("Exception: " + e.getMessage());
            }

        } else {
            fail("directory not found");
        }
    }

}
