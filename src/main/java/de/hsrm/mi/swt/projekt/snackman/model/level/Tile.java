package de.hsrm.mi.swt.projekt.snackman.model.level;

public class Tile {

    private final int x;
    private final int y;
    private OccupationType occupationType;

    private Object occupation;


    /**
     * Constructor, creates Tile with given parameters:
     * @param x x-coordinate of Tile (final)
     * @param y y-coordinate of Tile (final)
     * @param occupationType OccupationType of Tile
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

    public void setOccupation(Object occupation) {
        this.occupation = occupation;
    }
}
