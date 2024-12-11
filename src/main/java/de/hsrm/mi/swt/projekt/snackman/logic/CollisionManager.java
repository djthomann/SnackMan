package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;

public class CollisionManager {

    private SnackManMap snackManMap;
    private ArrayList<MovableAndSubscribable> allMovables;
    Logger logger = LoggerFactory.getLogger(GameManager.class);

    public CollisionManager(SnackManMap snackManMap, ArrayList<MovableAndSubscribable> allMovables) {
        this.snackManMap = snackManMap;
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
    public String checkCollision(float wishedX, float wishedZ) {

        Tile wishedTile = snackManMap.getTileAt((int) wishedX, (int) wishedZ);

        switch (wishedTile.getOccupationType()) {
            case WALL:
                logger.info(
                        "snackman and wall Collision ! Tile :" + wishedTile.getX() + " , " + wishedTile.getZ() + " .");
                return "wall";
            case ITEM:
                logger.info(
                        "snackman and item Collision ! Tile :" + wishedTile.getX() + " , " + wishedTile.getZ() + " .");
                return "item";
            default:
                return "none";
        }

    }
}
