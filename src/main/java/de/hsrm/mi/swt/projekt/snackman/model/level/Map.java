package de.hsrm.mi.swt.projekt.snackman.model.level;

import de.hsrm.mi.swt.projekt.snackman.configuration.Config;

import java.util.*;

public class Map {

    // All Tiles in Map -> get Tile at column x in row y via allTiles[y][x]
    private Tile[][] allTiles;

    public Map(int w, int h) {
        this.makeBlankMap(w, h);
        this.sidewinder();
    }

    private void makeBlankMap(int w, int h) {
        this.allTiles = new Tile[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                allTiles[y][x] = new Tile(x, y, OccupationType.WALL);
            }
        }
    }

    /**
     * setzt den sidewinder algo
     */
    private void sidewinder() {
        Random r = new Random();
        int w = allTiles[0].length;
        int h = allTiles.length;
        boolean closed = false;

        for (int row = 3; row < h / 2; row += 2) {
            List<Integer> run = new ArrayList<>();
            Set<Integer> cellUp;

            for (int cell = 1; cell < w-2; cell += 2) {
                run.add(cell);
                closed = cell == w - 2 || r.nextDouble() < Config.SIDEWINDER_ODDS;
                if (closed) {
                    cellUp = new HashSet<>();
                    while (cellUp.size() < (run.size() / Config.SIDEWINDER_E) + 1) {
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
        for (int i = 1; i < w-1; i++) {
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
        int spawnWidth = w/5;
        int spawnHeight = h/5;
        int middleW = w/2;
        int middleH = h/2;
        for (int i = 0; i < spawnWidth; i++) {
            for (int j = 0; j < spawnHeight; j++) {
                allTiles[middleH - (spawnHeight/2) + j][middleW - (spawnWidth/2) + i].setOccupationType(OccupationType.FREE);
            }
        }

        // schaffe in den Ecken Platz zum Spieler-Spawn
        allTiles[1][1].setOccupationType(OccupationType.FREE);
        allTiles[1][w - 2].setOccupationType(OccupationType.FREE);
        allTiles[h - 2][1].setOccupationType(OccupationType.FREE);
        allTiles[h-2][w-2].setOccupationType(OccupationType.FREE);
    }

    private void mirror(int h) {
        for (int i = 0; i < h/2; i++) {
            allTiles[(h - 1) - i] = allTiles[i].clone();
        }
    }

    public Tile[][] getAllTiles() {
        return allTiles;
    }
}
