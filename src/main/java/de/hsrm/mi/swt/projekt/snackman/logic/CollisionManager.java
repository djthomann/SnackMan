package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.DisappearEvent;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.GameObject;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.level.OccupationType;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

public class CollisionManager {

    // Translation added to x- and z-values for correct depiction in frontend
    private final float TRANSLATION = 0.5f;

    private SnackManMap snackManMap;
    private ArrayList<MovableAndSubscribable> allMovables;
    Logger logger = LoggerFactory.getLogger(GameManager.class);
    private Game game;
    
    // game here is temporary (until PR-- collision s-G is merged)
    public CollisionManager(Game game, SnackManMap snackManMap, ArrayList<MovableAndSubscribable> allMovables) {
        this.snackManMap = snackManMap;
        this.game = game; 
        this.allMovables = allMovables;
    }

    /**
     * Checks for collisions in the map.
     * the method returns a String with the type of entity/object occyping the tile.
     * If it's free, the method returns "none".
     * 
     * @param wishedX The x-coordinate of the tile to check for collisions.
     * @param wishedZ The z-coordinate of the tile to check for collisions.
     * @return String of The type of entity/object collided with, or "none" if no
     *         collision is detected.
     */
    public ArrayList<CollisionType> checkCollision(float wishedX, float wishedZ, GameObject collisionPartner) {
        
        // Changed return type from string to array list as several collisions can happen at once, e.g. ghost and item
        ArrayList<CollisionType> collisions = new ArrayList<>();
        collisions.clear();
        Tile wishedTile = snackManMap.getTileAt((int) wishedX, (int) wishedZ);

        switch (wishedTile.getOccupationType()) {
            case WALL:
                logger.info(
                        "snackman and wall Collision ! Tile :" + wishedTile.getX() + " , " + wishedTile.getZ() + " .");
                collisions.add(CollisionType.WALL);
            case ITEM:
                logger.info(
                        "snackman and item Collision ! Tile :" + wishedTile.getX() + " , " + wishedTile.getZ() + " .");
                Food nearbyFood = snackManMap.getFoodOfTile(wishedTile);
                if (collisionPartner instanceof SnackMan) {

                    try {
                        // currently food gets exact the same coord as tile
                        float foodPosX = (float) (nearbyFood.getX() + 0.3);
                        float foodPosZ = (float) (nearbyFood.getZ() + 0.3);
                        float distance = calculateDistance(wishedX, foodPosX, wishedZ, foodPosZ);
                        
                        if (distance < (collisionPartner.getRadius() + nearbyFood.getRadius())) {
                            ((SnackMan) collisionPartner).eat(nearbyFood);
                            GameManager gameManager = game.getGameManager(); 
                            DisappearEvent event = new DisappearEvent(game.id, nearbyFood); 
                            gameManager.notifyChange(event);
                            wishedTile.setOccupationType(OccupationType.FREE);
                            collisions.add(CollisionType.ITEM);
                        }
                    } catch(NullPointerException e) {

                    }

                }
                break;
        }
 
        // Iterate through all Movables and check for collisions
        for (MovableAndSubscribable aktMovable : this.allMovables) {

            // Check for collisions with ghosts (ghost ghost collision doesn't exist)
            if (!(collisionPartner instanceof Ghost) && aktMovable instanceof Ghost && aktMovable != collisionPartner) {

                float distance = calculateDistance(wishedX, ((Ghost)aktMovable).getX() + TRANSLATION, wishedZ, ((Ghost)aktMovable).getZ() + TRANSLATION);

                // Check if the cylindrical hitboxes collide 
                if (distance <= ((Ghost)aktMovable).getRadius() + collisionPartner.getRadius()) {

                    // Check if SnackMan is jumping over the Ghost
                    float heightDistance = calculateHeightDifference(collisionPartner.getY(), ((Ghost)aktMovable).getY());

                    // Heights /2 because the y-coordinate is in the middle of the object
                    if (heightDistance <= collisionPartner.getHeight()/2 + ((Ghost)aktMovable).getHeight()/2){
                        logger.info("Collision with Ghost!");
                        collisions.add(CollisionType.GHOST);
                    }
                }
                
            }

            // Check for collisions with SnackMan
            if (aktMovable instanceof SnackMan && aktMovable != collisionPartner) {

                float distance = calculateDistance(wishedX, ((SnackMan)aktMovable).getX() + TRANSLATION, wishedZ, ((SnackMan)aktMovable).getZ() + TRANSLATION);
                logger.info("\nDistance between SnackMen: " + distance + "\n");

                // Get tile of aktMovable
                Tile aktMovableTile = snackManMap.getTileAt((int)((SnackMan)aktMovable).getX(), (int)((SnackMan)aktMovable).getZ());

                if (distance <= ((SnackMan)aktMovable).getRadius() + collisionPartner.getRadius()) {

                    // If the collisionPartner is also a Snackman, check if they collide when taking into account their y-coordinates
                    // (since a SnackMan can jump over other SnackMan or both can be jumping)
                    if(collisionPartner instanceof SnackMan) {

                        float heightDistance = calculateHeightDifference(collisionPartner.getY(), ((SnackMan)aktMovable).getY());

                        // Heights /2 because the y-coordinate is in the middle of the object
                        if (heightDistance <= collisionPartner.getHeight()/2 + ((SnackMan)aktMovable).getHeight()/2){
                            logger.info("Collision with SnackMan!: " + ((SnackMan)aktMovable).getObjectId());
                            logger.info("This SnackMan: " + collisionPartner.getObjectId());
                            collisions.add(CollisionType.SNACKMAN);
                        }

                    // When collisionPartner is an instanceof Ghost check if SnackMan is invincible
                    // If not call reactToGhostCollision from the SnackMan (to cover the cases when a SnackMan is standing still
                    // and a ghost runs into them)
                    } else if(collisionPartner instanceof Ghost && !((SnackMan)aktMovable).isInvincible()){
                        logger.info("Collision with SnackMan!: " + ((SnackMan)aktMovable).getObjectId());
                        logger.info("This Ghost: " + collisionPartner.getObjectId());
                        collisions.add(CollisionType.SNACKMAN);
                        ((SnackMan)aktMovable).reactToGhostCollision();;
                    }

                }  

            }

        }

        return collisions;
    }

    public boolean positionIsWithinMapBounds(float x, float z) {
        return snackManMap.positionIsWithinMapBounds(x, z);
    }

    public boolean positionInWall(float x, float z) {
        Tile tileAtPosition = snackManMap.getTileAt((int)x, (int)z);
        switch(tileAtPosition.getOccupationType()) {
            case WALL:
                return true;
            default:
                return false;   
        }
    }

    public Vector3f getResolveVector(float x, float z) {

        Vector3f vec = new Vector3f(0, 0, 0);
    
        boolean vectorFound = false;

        // Koordinaten des aktuellen Tiles
        int coordX = (int) x;
        int coordZ = (int) z;
    
        Tile leftTile = null;
        Tile rightTile = null;
        Tile bottomTile = null;
        Tile topTile = null;
        try {
            leftTile = snackManMap.getTileAt(coordX - 1, coordZ);
        } catch(IndexOutOfBoundsException e) {}
        try {
            rightTile = snackManMap.getTileAt(coordX + 1, coordZ);
        } catch(IndexOutOfBoundsException e) {}
        try {
            bottomTile = snackManMap.getTileAt(coordX, coordZ + 1);
        } catch(IndexOutOfBoundsException e) {}
        try {
            topTile = snackManMap.getTileAt(coordX, coordZ - 1);
        } catch(IndexOutOfBoundsException e) {}
              
    
        float[] distances = distancesToEdges(x, z);
        
        Vector3f direction = new Vector3f(0, 0, 0);
        float minDistance = Float.MAX_VALUE;
    
        // Check whether a Tile is Free
        if (leftTile != null && leftTile.getOccupationType() != OccupationType.WALL && distances[0] < minDistance) {
            minDistance = distances[0];
            direction.set(-1, 0, 0);
            vectorFound = true;
        }
    
        if (rightTile != null && rightTile.getOccupationType() != OccupationType.WALL && distances[1] < minDistance) {
            minDistance = distances[1];
            direction.set(1, 0, 0);
            vectorFound = true;
        }
    
        if (bottomTile != null && bottomTile.getOccupationType() != OccupationType.WALL && distances[2] < minDistance) {
            minDistance = distances[2];
            direction.set(0, 0, 1);
            vectorFound = true;
        }
    
        if (topTile != null && topTile.getOccupationType() != OccupationType.WALL && distances[3] < minDistance) {
            minDistance = distances[3];
            direction.set(0, 0, -1);
            vectorFound = true;
        }
    
        if(!vectorFound) {
            // If no Tile is Free, Move to a WALL Tile
            if (leftTile != null) {
                minDistance = distances[0];
                direction.add(-1, 0, 0);
                vectorFound = true;
            }
        
            if (rightTile != null) {
                minDistance = distances[1];
                direction.add(1, 0, 0);
                vectorFound = true;
            }
        
            if (bottomTile != null) {
                minDistance = distances[2];
                direction.add(0, 0, 1);
                vectorFound = true;
            }
        
            if (topTile != null) {
                minDistance = distances[3];
                direction.add(0, 0, -1);
                vectorFound = true;
            }
        } 

        return direction;
        
    }

    public float[] distancesToEdges(float x, float z) {

        x = x - (int)x;
        z = z - (int)z;

        float dLeft = x;
        float dRight = 1 - x;
        float dBottom = z;
        float dTop = 1 - z;

        float[] dist = {dLeft, dRight, dBottom, dTop};
        return dist;
    }

    public Vector3f getUnitVectorToEdgeInUnitSquare(float x, float z) {
        
        Vector3f vec = new Vector3f(0, 0, 0);
        
        float[] distances = distancesToEdges(x, z);
        float dLeft = distances[0];
        float dRight = distances[1];
        float dBottom = distances[2];
        float dTop = distances[3];

        // Finde die minimale Entfernung
        float dMin = Math.min(Math.min(dLeft, dRight), Math.min(dBottom, dTop));

        // Bestimme den Richtungsvektor
        if (dMin == dLeft) {
            vec.x = -1;
            vec.z = 0;
        } else if (dMin == dRight) {
            vec.x = 1;
            vec.z = 0;
        } else if (dMin == dBottom) {
            vec.x = 0;
            vec.z = -1;
        } else {
            vec.x = 0;
            vec.z = 1;
        }

        return vec;
    }

    private float calculateDistance(float x1, float x2, float z1, float z2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
    }

        /**
     * Calculates the difference in between two coordinates
     * @param y1
     * @param y2
     * @return heightDifference
     */
    private float calculateHeightDifference(float y1, float y2) {
        return y1 > y2 ? y1-y2 : y2-y1;
    }
}
