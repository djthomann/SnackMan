package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
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
    public synchronized ArrayList<CollisionType> checkCollision(float wishedX, float wishedZ, GameObject collisionPartner) {
        
        // Changed return type from string to array list as several collisions can happen at once, e.g. ghost and item
        ArrayList<CollisionType> collisions = new ArrayList<>();
        collisions.clear();
        Tile wishedTile = snackManMap.getTileAt((int) wishedX, (int) wishedZ);

        switch (wishedTile.getOccupationType()) {
            case WALL:
                //logger.info(
                    //collisionPartner.toString() + " and wall Collision ! Tile :" + wishedTile.getX() + " , " + wishedTile.getZ() + " .");
                collisions.add(CollisionType.WALL);
            case ITEM:
                // logger.info(
                //         collisionPartner.toString() + " and item Collision ! Tile :" + wishedTile.getX() + " , " + wishedTile.getZ() + " .");
                if (collisionPartner instanceof SnackMan || collisionPartner instanceof Chicken) {
                    
                    Food nearbyFood = snackManMap.getFoodOfTile(wishedTile);

                    try {
                        float foodPosX = nearbyFood.getX();
                        float foodPosZ = nearbyFood.getZ();
                        float distance = calculateDistance(wishedX, foodPosX, wishedZ, foodPosZ);
                        if (distance < (collisionPartner.getRadius() + nearbyFood.getRadius())) {
                            if (collisionPartner instanceof SnackMan) ((SnackMan) collisionPartner).eat(nearbyFood);
                            else ((Chicken) collisionPartner).eat(nearbyFood); 
                            wishedTile.setOccupationType(OccupationType.FREE);
                            collisions.add(CollisionType.ITEM);
                        }
                    } catch(NullPointerException e) {
                        logger.error(
                            "no more food on tile: " + wishedTile.getX() + ", " + wishedTile.getZ());
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
                //logger.info("\nDistance between SnackMen: " + distance + "\n");

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

    public boolean isBetweenWalls (float x, float z) {
        boolean stuck = false; 
        Tile rightTile = snackManMap.getTileAt((int) x + 1, (int) z); 
        Tile leftTile = snackManMap.getTileAt((int) x - 1, (int) z); 

        Tile upTile = snackManMap.getTileAt((int) x, (int) z + 1);
        Tile downTile = snackManMap.getTileAt((int) x, (int) z - 1); 

        if (rightTile.getOccupationType() == OccupationType.WALL && leftTile.getOccupationType() == OccupationType.WALL) {
            stuck = true;; 
        }
        if (upTile.getOccupationType() == OccupationType.WALL && downTile.getOccupationType() == OccupationType.WALL) {
            stuck = true; 
        }      
        return stuck; 

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

        Tile[][] environment = snackManMap.getSurroundingTiles(snackManMap.getTileAt((int)x, (int)z));

        Vector3f direction = new Vector3f(0, 0, 0);

        int center = 1;

        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                Tile c = environment[center + i][center + j];
                if(c != null && c.getOccupationType() != OccupationType.WALL) {
                    direction.add(new Vector3f(i, 0, j));
                    return direction;
                }
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
