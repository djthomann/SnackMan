package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

public class GameObject {

    protected final long gameId;
    protected final long id;
    protected float x;
    protected float y;
    protected float z;
    protected float radius; 


    public GameObject(long gameId, float x, float y, float z, float radius) {
        this.gameId = gameId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius; 
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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius; 
    }

}
