package de.hsrm.mi.swt.projekt.snackman.model.level;

import de.hsrm.mi.swt.projekt.snackman.configuration.MapGenerationConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

public class Map {

    // Funktionsweise von Sidewinder (und Simons Hirn) gibt vor:
    // Tile an stelle (x, y) erreichbar über allTiles[y][x]
    // für weniger verwirrenden Zugriff getTileAt(x, y) nutzen
    private Tile[][] allTiles;
    private int w;
    private int h;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Constructor, liefert ein zufällig generiertes Map-Objekt mit Breite w und Höhe h
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
     * Constructor, liefert ein Map-Objekt aus Basis der angegebenen Datei
     * @param filepath Pfad zur map-csv Datei
     */
    public Map(String filepath) {
        //TODO: implement
    }

    /**
     * instanziiert eine map, die nur außen Wände hat
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
     * setzt den sidewinder algorithmus um
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

        // Spiegel obere Hälfte nach unten
        mirror(h);

        // bei ungerade höhe wird in der Mitte eine freie Reihe gebaut
        for (int i = 1; i < w - 1; i++) {
            allTiles[h/2][i].setOccupationType(OccupationType.ITEM);
        }
        // momentan könnte es vorkommen, dass manche Spieler voneinander und den Geistern abgeschnitten sind,
        // daher immer 2 horizontale gänge
        int path = w / 4;
        for (int i = 1; i < h - 1; i++) {
            allTiles[i][path].setOccupationType(OccupationType.ITEM);
            allTiles[i][w - path].setOccupationType(OccupationType.ITEM);
        }

        // schaffe Platz in der Mitte zum Geister-Spawn
        int spawnWidth = w / 5;
        int spawnHeight = h / 5;
        int middleW = w / 2;
        int middleH = h / 2;
        for (int row = 0; row < spawnWidth; row++) {
            for (int col = 0; col < spawnHeight; col++) {
                allTiles[middleH - (spawnHeight / 2) + col][middleW - (spawnWidth / 2) + row].setOccupationType(OccupationType.FREE);
            }
        }

        // schaffe in den Ecken Platz zum Spieler-Spawn
        allTiles[1][1].setOccupationType(OccupationType.FREE);
        allTiles[1][w - 2].setOccupationType(OccupationType.FREE);
        allTiles[h - 2][1].setOccupationType(OccupationType.FREE);
        allTiles[h - 2][w - 2].setOccupationType(OccupationType.FREE);
    }

    /**
     * spiegelt die obere auf die untere Hälfte
     * @param h höhe der map
     */
    private void mirror(int h) {
        for (int i = 0; i < h / 2; i++) {
            allTiles[(h - 1) - i] = allTiles[i].clone();
        }
    }

    public void saveAsCSV() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        File file = new File("src/main/resources/savedMaps/map_" + now.format(dateTimeFormatter) + ".csv");

        try (FileWriter writer = new FileWriter(file.getPath())) {

            if (!file.createNewFile()) {
                logger.warning("File " + file.getPath() + " already exists!");
            }

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
