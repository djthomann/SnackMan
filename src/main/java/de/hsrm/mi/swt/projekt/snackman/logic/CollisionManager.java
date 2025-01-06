package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.DisappearEvent;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.GameObject;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.level.OccupationType;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

public class CollisionManager {

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
    public String checkCollision(float wishedX, float wishedZ, GameObject collisionPartner) {
        Tile wishedTile = snackManMap.getTileAt((int) wishedX, (int) wishedZ);

        switch (wishedTile.getOccupationType()) {
            case WALL:
                logger.info(
                        "collisionPartner and wall Collision ! Tile :" + wishedTile.getX() + " , " + wishedTile.getZ() + " .");
                return "wall";
            case ITEM:
                logger.info(
                        "collisionPartner and item Collision ! Tile :" + wishedTile.getX() + " , " + wishedTile.getZ() + " .");
                Food nearbyFood = snackManMap.getFoodOfTile(wishedTile);
                if (collisionPartner instanceof SnackMan) {
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
                        return "item";
                    }

                } else {
                    return "none";
                }
            default:
                return "none";
        }

    }

    private float calculateDistance(float x1, float x2, float z1, float z2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
    }
}
