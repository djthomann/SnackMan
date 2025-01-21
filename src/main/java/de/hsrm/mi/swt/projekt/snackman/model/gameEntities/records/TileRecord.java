package de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records;


import de.hsrm.mi.swt.projekt.snackman.model.level.*;

public record TileRecord(int x, int z, OccupationType occupationType, FoodRecord[] foods) {
    
}
