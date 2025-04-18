package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.FoodType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.GameObject;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.level.OccupationType;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

public class CollisionManager {

    private final float TRANSLATION = 0.5f;

    private SnackManMap snackManMap;
    private ArrayList<MovableAndSubscribable> allMovables;

    Logger logger = LoggerFactory.getLogger(GameManager.class);

    public CollisionManager(Game game, SnackManMap snackManMap, ArrayList<MovableAndSubscribable> allMovables) {
        this.snackManMap = snackManMap;
        this.allMovables = allMovables;
    }

    /**
     * Checks for collisions in the map.
     * 
     * @param wishedX       The x-coordinate of the tile to check for collisions.
     * @param wishedZ       The z-coordinate of the tile to check for collisions.
     * @param currentObject Game Object that collides with Wall, Item, etc and which
     *                      calls the checkCollision Method.
     * @return List of The types of entity/object collided with, or empty list if no
     *         collision is detected.
     */
    public synchronized List<CollisionType> checkCollision(float wishedX, float wishedZ, 
        GameObject currentObject) {
        
        ArrayList<CollisionType> collisions = new ArrayList<>();
        collisions.clear();
        Tile wishedTile = snackManMap.getTileAt((int) wishedX, (int) wishedZ);

        switch (wishedTile.getOccupationType()) {

            case WALL:
                logger.info(currentObject.toString() + " and wall Collision ! Tile :" + wishedTile.getX() + " , "
                        + wishedTile.getZ() + " .");
                collisions.add(CollisionType.WALL);
                break;

            case FREE:
                break;

            case ITEM:
                List<Food> nearbyFoods = new CopyOnWriteArrayList<>(snackManMap.getFoodsOfTile(wishedTile));
                List <Food> foodsToRemove = new ArrayList<>(); 
                if (currentObject instanceof SnackMan || currentObject instanceof Chicken) {
                    for (Food food: nearbyFoods) {
                        try {
                            float foodPosX = food.getX();
                            float foodPosZ = food.getZ();
                            float distance = calculateDistance(wishedX, foodPosX, wishedZ, foodPosZ);
                            float heightDistance = calculateHeightDifference(currentObject.getY(), food.getY());
                            if (distance < (currentObject.getRadius() + food.getRadius())) {
                                if (heightDistance <= currentObject.getHeight()/2 + food.getHeight()/2) {
                                    
                                    if (currentObject instanceof SnackMan) {
                                        foodsToRemove.add(food); 
                                        wishedTile.removeFromOccupation(food); 

                                        ((SnackMan) currentObject).eat(food);
                                    }
                                    else if (currentObject instanceof Chicken) {
                                        if (food.getFoodType() != FoodType.EGG)  {
                                            foodsToRemove.add(food);
                                            wishedTile.removeFromOccupation(food); 
                                            ((Chicken) currentObject).eat(food);
                                            break; 
                                        }
                                        else {
                                            collisions.clear(); 
                                            return collisions; 
                                        }
                                    }
                                }
                            }
                            else {
                                collisions.addAll(checkModelCollision(currentObject, wishedX, wishedZ)); 
                            }
                        } catch(NullPointerException e) {
                                logger.error(
                                    "no more food on tile: " + wishedTile.getX() + ", " + wishedTile.getZ());
                        }
                    }   
                    nearbyFoods.removeAll(foodsToRemove); 
                    if (nearbyFoods.size() == 0) wishedTile.setOccupationType(OccupationType.FREE);
                    else wishedTile.setOccupationType(OccupationType.ITEM);
                    collisions.add(CollisionType.ITEM);

                }
                break;
        }
        collisions.addAll(checkModelCollision(currentObject, wishedX, wishedZ));
        return collisions;

    }

    public ArrayList<CollisionType> checkModelCollision(GameObject currentObject, float wishedX, float wishedZ) {

        ArrayList<CollisionType> collisions = new ArrayList<>();

        for (MovableAndSubscribable collisionPartner : this.allMovables) {

            if (collisionPartner != currentObject) {
                float distance = calculateDistance(wishedX, ((GameObject) collisionPartner).getX(),
                        wishedZ,
                        ((GameObject) collisionPartner).getZ());

                switch (currentObject) {
                    case SnackMan s1 -> {

                        float heightDistance = calculateHeightDifference(currentObject.getY(),
                                ((GameObject) collisionPartner).getY());

                        if ((distance <= ((GameObject) collisionPartner).getRadius() + currentObject.getRadius())
                                && (heightDistance <= s1.getHeight() / 2
                                        + ((GameObject) collisionPartner).getHeight() / 2)) {

                            switch (collisionPartner) {

                                case SnackMan s2 -> {
                                    // logger.info("Collision between Snackman #" + s1.getObjectId() + "and Snackman #" + s2.getObjectId());
                                    collisions.add(CollisionType.SNACKMAN);
                                }
                                case Ghost g -> {
                                    // logger.info("Collision between Snackman #" + s1.getObjectId() + "and Ghost #" + g.getObjectId());
                                    s1.reactToGhostCollision();
                                    collisions.add(CollisionType.GHOST);
                                }
                                case Chicken c -> {
                                    // logger.info("Collision between Snackman #" + s1.getObjectId() + "and Chicken #" + c.getObjectId());
                                    collisions.add(CollisionType.CHICKEN);
                                }

                                default -> logger.error("Collision Partner is not a Movable!");
                            }
                        }
                    }
                    case Ghost g1 -> {
                        if (distance <= ((GameObject) collisionPartner).getRadius() + currentObject.getRadius()) {
                            switch (collisionPartner) {
                                case SnackMan s -> {
                                    // logger.info("Collision between Ghost #" + g1.getObjectId() + "and Snackman #" + s.getObjectId());
                                    s.reactToGhostCollision();
                                    collisions.add(CollisionType.SNACKMAN);
                                }
                                case Ghost g2 -> {
                                    // logger.info("Collision between Ghost #" + g1.getObjectId() + "and Ghost #" + g2.getObjectId());
                                }
                                case Chicken c -> {
                                    // logger.info("Collision between Ghost #" + g1.getObjectId() + "and Chicken #"
                                    //        + c.getObjectId());
                                    collisions.add(CollisionType.CHICKEN);
                                }
                                default -> logger.error("Collision Partner is not a Movable!");
                            }
                        }
                    }
                    case Chicken c1 -> {
                        if (distance <= ((GameObject) collisionPartner).getRadius() + currentObject.getRadius()) {
                            switch (collisionPartner) {
                                case SnackMan s -> {
                                    // logger.info("Collision between Chicken #" + c1.getObjectId() + "and Snackman #"
                                    //        + s.getObjectId());
                                    collisions.add(CollisionType.SNACKMAN);
                                }
                                case Ghost g -> {
                                    // logger.info("Collision between Chicken #" + c1.getObjectId() + "and Ghost #"
                                    //        + g.getObjectId());
                                    c1.reactToGhostCollision();
                                    collisions.add(CollisionType.GHOST);
                                }
                                case Chicken c2 -> {
                                    // logger.info("Collision between Chicken #" + c1.getObjectId() + "and Chicken #"
                                    //        + c2.getObjectId());
                                    collisions.add(CollisionType.CHICKEN);
                                }
                                default -> logger.error("Collision Partner is not a Movable!");
                            }
                        }
                    }
                    default -> logger.error("Current Object is not a Movable!");
                }

            }

        }

        return collisions;

    }


    public boolean isBetweenWalls(float x, float z) {
        boolean stuck = false;
        Tile rightTile = snackManMap.getTileAt((int) x + 1, (int) z);
        Tile leftTile = snackManMap.getTileAt((int) x - 1, (int) z);

        Tile upTile = snackManMap.getTileAt((int) x, (int) z + 1);
        Tile downTile = snackManMap.getTileAt((int) x, (int) z - 1);

        if (rightTile.getOccupationType() == OccupationType.WALL
                && leftTile.getOccupationType() == OccupationType.WALL) {
            stuck = true;
            ;
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
        Tile tileAtPosition = snackManMap.getTileAt((int) x, (int) z);
        switch (tileAtPosition.getOccupationType()) {
            case WALL:
                return true;
            default:
                return false;
        }
    }

    public Vector3f getResolveVector(float x, float z) {

        Tile[][] environment = snackManMap.getSurroundingTiles(snackManMap.getTileAt((int) x, (int) z));

        Vector3f direction = new Vector3f(0, 0, 0);

        int center = 1;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Tile c = environment[center + i][center + j];
                if (c != null && c.getOccupationType() != OccupationType.WALL) {
                    direction.add(new Vector3f(i, 0, j));
                    return direction;
                }
            }
        }

        return direction;

    }

    public float[] distancesToEdges(float x, float z) {

        x = x - (int) x;
        z = z - (int) z;

        float dLeft = x;
        float dRight = 1 - x;
        float dBottom = z;
        float dTop = 1 - z;

        float[] dist = { dLeft, dRight, dBottom, dTop };
        return dist;
    }

    public Vector3f getUnitVectorToEdgeInUnitSquare(float x, float z) {

        Vector3f vec = new Vector3f(0, 0, 0);

        float[] distances = distancesToEdges(x, z);
        float dLeft = distances[0];
        float dRight = distances[1];
        float dBottom = distances[2];
        float dTop = distances[3];

        float dMin = Math.min(Math.min(dLeft, dRight), Math.min(dBottom, dTop));

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
     * 
     * @param y1
     * @param y2
     * @return heightDifference
     */
    private float calculateHeightDifference(float y1, float y2) {
        return y1 > y2 ? y1 - y2 : y2 - y1;
    }
}