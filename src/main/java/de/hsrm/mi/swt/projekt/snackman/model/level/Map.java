package de.hsrm.mi.swt.projekt.snackman.model.level;

import de.hsrm.mi.swt.projekt.snackman.configuration.MapGenerationConfig;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

public class Map {

    // due to sidewinder (and Simon's brain):
    // Tile at (x, y) can be reached via allTiles[y][x],
    // or (recommended) getTileAt(x, y)
    private Tile[][] allTiles;
    private int w;
    private int h;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Constructor, creates randomly generated Map object with dimensions width w and height h
     * @param w breite
     * @param h höhe
     */
    public Map(int w, int h) {
        this.w = w;
        this.h = h;
        this.makeBlankMap();
        this.sidewinder();
    }

    /**
     * Constructor, creates Map object on base of given csv file
     * @param filepath path to file (starting with src/...)
     */
    public Map(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {

            String line;
            int lines = 0;
            int numTokens = 0;
            List<Tile[]> allRows = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                List<Tile> currentRow = new ArrayList<>();
                String[] tokens = line.split(",");

                if (numTokens != 0) {
                    if (numTokens != tokens.length) throw new IOException("changing number of tokens per line");
                } else {
                    numTokens = tokens.length;
                }

                for (int i = 0; i < tokens.length; i++) {
                    Tile newTile;
                    switch (tokens[i]) {
                        case "-1" -> newTile = new Tile(i, lines, OccupationType.WALL);
                        case "0" -> newTile = new Tile(i, lines, OccupationType.FREE);
                        case "1" -> newTile = new Tile(i, lines, OccupationType.ITEM);
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

        } catch (IOException e) {
            logger.warning("Something went wrong while loading file:");
            logger.warning(e.getMessage());
        }
    }

    /**
     * creates new blank map with walls on the outside
     */
    private void makeBlankMap() {
        this.allTiles = new Tile[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                allTiles[y][x] = new Tile(x, y, OccupationType.WALL);
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

            for (int cell = 1; cell < w-2; cell += 2) {
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
                }
                else {
                    allTiles[row][cell + 1].setOccupationType(OccupationType.ITEM);
                }
            }
        }

        // mirror upper half downwards
        mirror(h);

        // for odd h: create empty row in the middle
        for (int i = 1; i < w - 1; i++) {
            allTiles[h/2][i].setOccupationType(OccupationType.ITEM);
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
                allTiles[middleH - (spawnHeight / 2) + col][middleW - (spawnWidth / 2) + row].setOccupationType(OccupationType.FREE);
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
     * @param h height of map
     */
    private void mirror(int h) {
        for (int i = 0; i < h / 2; i++) {
            allTiles[(h - 1) - i] = allTiles[i].clone();
        }
    }

    /**
     * saves map-object to csv-file into src/main/resources/savedMaps
     */
    public void saveAsCSV() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        File file = new File("src/main/resources/savedMaps/map_" + now.format(dateTimeFormatter) + ".csv");

        try (FileWriter writer = new FileWriter(file.getPath())) {

            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    String token = "";
                    switch (allTiles[j][i].getOccupationType()) {
                        case FREE -> token = "0";
                        case WALL -> token = "-1";
                        case ITEM -> token = "1";
                    }
                    writer.write(token);
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

    public Tile getTileAt(int x, int y) {
        return allTiles[y][x];
    }

    public Tile[][] getAllTiles() {
        return allTiles;
    }
}
