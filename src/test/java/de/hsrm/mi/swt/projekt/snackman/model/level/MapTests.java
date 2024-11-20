package de.hsrm.mi.swt.projekt.snackman.model.level;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
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
        Assertions.assertEquals(OccupationType.FREE, map.getAllTiles()[h / 2][w / 2].getOccupationType());
    }

    /**
     * Testet, ob alle 4 Ecken-Tiles frei sind
     */
    @Test
    void testPlayerSpawn() {
        Assertions.assertEquals(OccupationType.FREE, map.getAllTiles()[1][1].getOccupationType());
        Assertions.assertEquals(OccupationType.FREE, map.getAllTiles()[h - 2][1].getOccupationType());
        Assertions.assertEquals(OccupationType.FREE, map.getAllTiles()[1][w - 2].getOccupationType());
        Assertions.assertEquals(OccupationType.FREE, map.getAllTiles()[h - 2][w - 2].getOccupationType());
    }

    /**
     * Testet die Map-Größe
     */
    @Test
    void testDimensions() {
        Assertions.assertEquals(h, map.getAllTiles().length);
        Assertions.assertEquals(w, map.getAllTiles()[0].length);
    }

    /**
     * Testet, ob die Map von einer Wand umrandet ist
     */
    @Test
    void testOuterWall() {
        Tile[][] tiles = map.getAllTiles();
        for (int row = 0; row < h; row++) {
            if (row > 0 && row < h - 1) {
                Assertions.assertEquals(OccupationType.WALL, tiles[row][0].getOccupationType());
                Assertions.assertEquals(OccupationType.WALL, tiles[row][w - 1].getOccupationType());
            } else {
                for (int col = 0; col < w; col++) {
                    Assertions.assertEquals(OccupationType.WALL, tiles[row][col].getOccupationType());
                }
            }
        }
    }

    @Test
    void testSaveAsCSVCreatesNewFile() {
        File dir = new File("src/main/resources/savedMaps");
        if (dir.exists() && dir.isDirectory()) {
            int numFilesBefore = Objects.requireNonNull(dir.list()).length;
            map.saveAsCSV();
            int numFilesAfter = Objects.requireNonNull(dir.listFiles()).length;
            Assertions.assertEquals(numFilesBefore + 1, numFilesAfter);

            if (!Objects.requireNonNull(Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                    .max(Comparator.comparingLong(File::lastModified))
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

            File newFile = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                    .max(Comparator.comparingLong(File::lastModified))
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

    @Test
    void testFileConstructor() {

        File testFile = new File("src/main/resources/savedMaps/testFile.csv");
        /*
        try (FileWriter writer = new FileWriter(testFile.getPath())) {

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if (i == 0 || j == 0 || i == h - 1 || j == w - 1) {
                        writer.write("-1");
                    } else {
                        if ((j + i) % 2 == 0) {
                            writer.write("0");
                        } else {
                            writer.write("-1");
                        }
                    }
                    writer.write(",");
                }
                writer.write("\n");
            }

        } catch (IOException e) {
            fail("Something went wrong: " + e.getMessage());
        }

         */

    }

}
