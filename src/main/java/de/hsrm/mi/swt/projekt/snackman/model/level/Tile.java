package de.hsrm.mi.swt.projekt.snackman.model.level;

public class Tile {

    private final int x;
    private final int y;
    private OccupationType occupationType;

    private Object occupation;


    /**
     * Constructor, liefert Tile-Objekt mit gegebenen Daten
     * @param x x-Koordinate der Tile (final)
     * @param y y-Koordinate der Tile (final)
     * @param occupationType OccupationType der Tile
     */
    public Tile(int x, int y, OccupationType occupationType) {
        this.x = x;
        this.y = y;
        this.occupationType = occupationType;
    }

    public OccupationType getOccupationType() {
        return occupationType;
    }

    public void setOccupationType(OccupationType occupationType) {
        this.occupationType = occupationType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Object getOccupation() {
        return occupation;
    }

}
