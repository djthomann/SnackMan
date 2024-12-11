package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

public class GameObject {

    protected final long gameId;
    protected final long objectId;
    protected float x;
    protected float y;
    protected float z;

    public GameObject(long gameId, float x, float y, float z) {
        this(IDGenerator.getInstance().getUniqueID(), gameId, x, y, z);
    }

    public GameObject(long objectId, long gameId, float x, float y, float z) {
        this.objectId = objectId;
        this.gameId = gameId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getObjectId() {
        return objectId;
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
