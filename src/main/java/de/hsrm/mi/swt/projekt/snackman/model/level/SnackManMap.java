package de.hsrm.mi.swt.projekt.snackman.model.level;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.FoodType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

public class SnackManMap {

    // due to sidewinder (and Simon's brain):
    // Tile at (x, y) can be reached via allTiles[y][x],
    // or (recommended) getTileAt(x, y)
    private int w;
    private int h;
    private Tile[][] allTiles;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Constructor, creates randomly generated Map object with dimensions width w and height h
     * to ensure proper map, w and h are made odd, if not already
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
     * @param filename path to file (only filename needed, no path)
     */
    public SnackManMap(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

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

            createFood();

        } catch (IOException e) {
            logger.warning("Something went wrong while loading file:");
            logger.warning(e.getMessage());
        }
    }

    private void createFood() {
        Random r = new Random();
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                if (allTiles[row][col].getOccupationType() == OccupationType.ITEM) {
                    FoodType foodType = FoodType.OKAY;
                    if (r.nextBoolean()) {
                        if (r.nextBoolean()) {
                            foodType = FoodType.HEALTHY;
                        } else {
                            foodType = FoodType.UNHEALTHY;
                        }
                    }
                    allTiles[row][col].setOccupation(new Food(col, row, foodType));
                }
            }
        }
    }

    /**
     * creates new map with walls on the outside and grid of walls inside
     */
    private void makeBlankMap() {
        this.allTiles = new Tile[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                /*
                if (i == 0 || j == 0 || i == h - 1 || j == w - 1) {
                    res[i][j] = -1;
                } else {
                    res[i][j] = (i == 1 || (i % 2 == 1 && j % 2 == 1)) ? 0 : -1;
                }
                 */
                OccupationType occupationType = OccupationType.WALL;

                if (y != h - 1 && x != 0 && x != w - 1 && (y == 1 || (y % 2 == 1 && x % 2 == 1))) {
                    occupationType = OccupationType.ITEM;
                }
                allTiles[y][x] = new Tile(x, y, occupationType);
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

        createFood();
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

    public Tile getTileAt(int x, int y) {
        return allTiles[y][x];
    }

    public Tile[][] getAllTiles() {
        return allTiles;
    }

    public int getW() {
        return w;
    }
    
    public int getH() {
        return h;
    }

}
