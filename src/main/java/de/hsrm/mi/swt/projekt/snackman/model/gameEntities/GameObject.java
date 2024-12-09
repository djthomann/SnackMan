package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

public class GameObject {

    protected final long gameId;
    protected final long id;
    protected float x;
    protected float y;
    protected float z;

    public GameObject(long gameId, float x, float y, float z) {
        this.gameId = gameId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = IDGenerator.getInstance().getUniqueID();
    }

    public long getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

}
