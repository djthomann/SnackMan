package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameObject {

    protected final long gameId;
    protected final long objectId;
    protected float x;
    protected float y;
    protected float z;
    protected float radius;
    protected float height; 

    private final Logger logger = LoggerFactory.getLogger(GameObject.class);

    public GameObject(long gameId, float x, float y, float z, float radius, float height) {
        this(IDGenerator.getInstance().getUniqueID(), gameId, x, y, z, radius, height);
    }

    public GameObject(long objectId, long gameId, float x, float y, float z, float radius, float height) {
        this.objectId = objectId;
        this.gameId = gameId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius; 
        this.height = height;
    }

    public void move(float newX, float newY, float newZ) {
        x += newX;
        y += newY;
        z += newZ;
        logger.info(this.getClass().getSimpleName() + " moved to " + x + ", " + y + ", " + z);
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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius; 
    }

    public float getHeight() {
        return height;
    }

    public long getGameId() {
        return gameId;
    }
}
