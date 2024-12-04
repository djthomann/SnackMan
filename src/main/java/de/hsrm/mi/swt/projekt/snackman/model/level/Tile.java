package de.hsrm.mi.swt.projekt.snackman.model.level;

public class Tile {

    private final int x;
    private final int z;
    private OccupationType occupationType;

    private Object occupation;


    /**
     * Constructor, creates Tile with given parameters:
     * @param x x-coordinate of Tile (final)
     * @param z z-coordinate of Tile (final)
     * @param occupationType OccupationType of Tile
     */
    public Tile(int x, int z, OccupationType occupationType) {
        this.x = x;
        this.z = z;
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

    public int getZ() {
        return z;
    }

    public Object getOccupation() {
        return occupation;
    }

    public void setOccupation(Object occupation) {
        this.occupation = occupation;
    }
}
