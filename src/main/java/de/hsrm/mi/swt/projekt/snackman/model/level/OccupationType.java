package de.hsrm.mi.swt.projekt.snackman.model.level;

/**
 * Enum for Backend-Use, to mark tile's occupation
 */
public enum OccupationType {

    FREE('\u2591'),
    WALL('\u2588'),
    ITEM('\u25CF'),
    OCCUPIED('\u2591'); // same character as 'FREE' to prevent errors while loading/saving maps from/to files

    public final char c;

    OccupationType(char c){
        this.c = c;
    }

}
