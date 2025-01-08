package de.hsrm.mi.swt.projekt.snackman.model.level;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.SnackManMapRecord;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.TileRecord;

public class SnackManMap {

    // due to sidewinder (and Simon's brain):
    // Tile at (x, z) (y is always at 0, we view the map as 2D) can be reached via
    // allTiles[z][x],
    // or (recommended) getTileAt(x, z)
    private int w; // x-coordinate
    private int h; // z-coordinate
    private Tile[][] allTiles;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Constructor, creates randomly generated Map object with dimensions width w
     * and height h
     * to ensure proper map, w and h are made odd, if not already
     * 
     * @param w breite
     * @param h höhe
     */
    public SnackManMap(int w, int h) {
        this.w = (w % 2 == 1) ? w : w + 1;
        this.h = (h % 2 == 1) ? h : h + 1;
        this.makeBlankMap();
        this.sidewinder();
    }

    /**
     * Constructor, creates Map object on base of given csv file
     * 
     * @param input  path to file (only filename needed, no path), or contents of
     *               csv-file
     * @param isPath true, if given String is path to map-file, otherwise would be
     *               regarded as file-content
     */
    public SnackManMap(String input, boolean isPath) {
        try (BufferedReader reader = (isPath)
                ? new BufferedReader(new FileReader(MapGenerationConfig.SAVED_MAPS_PATH + input))
                : new BufferedReader(new StringReader(input))) {
            parseFileContent(reader);
        } catch (IOException e) {
            logger.warning("Something went wrong while loading file:");
            logger.warning(e.getMessage());
        }
    }

    private void parseFileContent(BufferedReader reader) throws IOException {
        String line;

        int lines = 0;
        int numTokens = 0;
        List<Tile[]> allRows = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            List<Tile> currentRow = new ArrayList<>();
            String[] tokens = line.split(",");

            if (numTokens != 0) {
                if (numTokens != tokens.length)
                    throw new IOException("changing number of tokens per line");
            } else {
                numTokens = tokens.length;
            }

            for (int i = 0; i < tokens.length; i++) {
                Tile newTile;
                switch (tokens[i].charAt(0)) {
                    case '\u2588' -> newTile = new Tile(i, lines, OccupationType.WALL);
                    case '\u2591' -> newTile = new Tile(i, lines, OccupationType.FREE);
                    case '\u25CF' -> newTile = new Tile(i, lines, OccupationType.ITEM);
                    default -> throw new IOException("Unexpected token while loading file: " + tokens[i]);
                }

                currentRow.add(newTile);
            }

            allRows.add(currentRow.toArray(Tile[]::new));
            lines++;

        }

        allTiles = allRows.toArray(Tile[][]::new);
        h = allTiles.length;
        w = allTiles[0].length;

    }

    /**
     * creates new map with walls on the outside and grid of walls inside
     */
    private void makeBlankMap() {
        this.allTiles = new Tile[h][w];

        for (int z = 0; z < h; z++) {
            for (int x = 0; x < w; x++) {
                OccupationType occupationType = OccupationType.WALL;

                if (z != h - 1 && x != 0 && x != w - 1 && (z == 1 || (z % 2 == 1 && x % 2 == 1))) {
                    occupationType = OccupationType.ITEM;
                }
                allTiles[z][x] = new Tile(x, z, occupationType);
                allTiles[z][x].setOccupation(null);
            }
        }
    }

    /**
     * implements sidewinder algorithm to generate new map
     * please be sure to call makeBlankMap() before using sidewinder()
     */
    private void sidewinder() {
        Random r = new Random();
        int w = allTiles[0].length;
        int h = allTiles.length;
        boolean closed;

        for (int row = 3; row < h / 2; row += 2) {
            List<Integer> run = new ArrayList<>();
            Set<Integer> cellUp;

            for (int cell = 1; cell < w - 2; cell += 2) {
                run.add(cell);
                closed = cell == w - 2 || r.nextDouble() < MapGenerationConfig.SIDEWINDER_ODDS;
                if (closed) {
                    cellUp = new HashSet<>();
                    while (cellUp.size() < (run.size() / MapGenerationConfig.SIDEWINDER_E) + 1) {
                        cellUp.add(1 + 2 * r.nextInt(w / 2));
                    }

                    for (Integer integer : cellUp) {
                        if (row != 1) {
                            allTiles[row - 1][integer].setOccupationType(OccupationType.ITEM);
                        }
                    }
                    run.clear();
                } else {
                    allTiles[row][cell + 1].setOccupationType(OccupationType.ITEM);
                }
            }
        }

        // mirror upper half downwards
        mirror(h);

        // for odd h: create empty row in the middle
        for (int i = 1; i < w - 1; i++) {
            allTiles[h / 2][i].setOccupationType(OccupationType.ITEM);
        }

        // to prevent players from being trapped, create two horizontal paths
        int path = w / 4;
        for (int i = 1; i < h - 1; i++) {
            allTiles[i][path].setOccupationType(OccupationType.ITEM);
            allTiles[i][w - path].setOccupationType(OccupationType.ITEM);
        }

        // create place for ghost-spawn
        int spawnWidth = w / 5;
        int spawnHeight = h / 5;
        int middleW = w / 2;
        int middleH = h / 2;
        for (int row = 0; row < spawnWidth; row++) {
            for (int col = 0; col < spawnHeight; col++) {
                allTiles[middleH - (spawnHeight / 2) + col][middleW - (spawnWidth / 2) + row]
                        .setOccupationType(OccupationType.FREE);
            }
        }

        // create place for player spawn at each corner
        allTiles[1][1].setOccupationType(OccupationType.FREE);
        allTiles[1][w - 2].setOccupationType(OccupationType.FREE);
        allTiles[h - 2][1].setOccupationType(OccupationType.FREE);
        allTiles[h - 2][w - 2].setOccupationType(OccupationType.FREE);
    }

    /**
     * mirrors upper on lower half
     * 
     * @param h height of map
     */
    private void mirror(int h) {
        for (int i = 0; i < h / 2; i++) {
            for (int x = 0; x < w; x++) {
                allTiles[(h - 1) - i][x].setOccupationType(allTiles[i][x].getOccupationType());
            }
        }
    }

    /**
     * saves map-object to csv-file into path established in MapGenerationConfig
     */
    public void saveAsCSV() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
        File file = new File(MapGenerationConfig.SAVED_MAPS_PATH + "map_" + now.format(dateTimeFormatter) + ".csv");

        try (FileWriter writer = new FileWriter(file.getPath(), StandardCharsets.UTF_8)) {

            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    writer.write(allTiles[j][i].getOccupationType().c);
                    if (i < w - 1) {
                        writer.write(",");
                    } else {
                        writer.write("\n");
                    }
                }
            }

            logger.info("saved file " + file.getPath());

        } catch (IOException e) {
            logger.warning("Something went wrong while saving file");
            logger.warning(e.getMessage());
        }
    }

    public boolean positionIsWithinMapBounds(float x, float z) {
        logger.info("Checking position: x|z " + x + " " + z);
        logger.info("Map bounds: w|h " + w + " " + h);

        // Überprüfen, ob die Position innerhalb der Kartenbreite und -höhe liegt
        if (x < 0 || x >= w || z < 0 || z >= h) {
            return false; // Position ist außerhalb der Karte
        }
        return true;
    }

    public Tile getTileAt(int x, int z) throws IndexOutOfBoundsException {
        try {
            return allTiles[z][x];
        } catch(IndexOutOfBoundsException e) {
            logger.info("Position asked for not within bounds");
            throw e;
        }
    }

    public Tile[][] getAllTiles() {
        return allTiles;
    }

    /**
     * returns the current environment around the given tile
     *
     * @param tile the tile in the center
     * @return 3x3 map with the surrounding tiles, where 'null' stands for outside
     *         positions (Prevention of ArrayindexoutofBoundsException)
     */
    public Tile[][] getSurroundingTiles(Tile tile) {

        Tile[][] surroudings = new Tile[3][3];

        for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                int tileX = tile.getX() + offsetX;
                int tileY = tile.getZ() + offsetZ;

                if (tileX >= 0 && tileX < w && tileY >= 0 && tileY < h) {
                    surroudings[offsetX + 1][offsetZ + 1] = allTiles[tileY][tileX];
                } else {
                    surroudings[offsetX + 1][offsetZ + 1] = null; // outside the map

                }
            }
        }
        return surroudings;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SnackManMap other)) {
            return false;
        }

        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                if (this.allTiles[row][col].getOccupationType() != other.allTiles[row][col].getOccupationType())
                    return false;
            }
        }

        return true;
    }

    public Food getFoodOfTile(Tile tile) {
        Food food = (Food) tile.getOccupation();
        return food;
    }

    public SnackManMapRecord toRecord() {
        TileRecord [][] tileRecords = new TileRecord[h][w];
        for (int z = 0; z < h; z++) {
            for (int x = 0; x < w; x++) {
                tileRecords[z][x] = allTiles[z][x].toRecord();
            }
        }
        return new SnackManMapRecord(w, h, tileRecords);
    }
}
