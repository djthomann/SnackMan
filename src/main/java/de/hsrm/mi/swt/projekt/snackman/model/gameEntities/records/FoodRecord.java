package de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.*;

public record FoodRecord(long gameId, long objectId, float x, float y, float z, int calories, FoodType foodType){}
