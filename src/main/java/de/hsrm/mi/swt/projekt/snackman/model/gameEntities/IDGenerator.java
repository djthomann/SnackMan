package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

public class IDGenerator {

    private static IDGenerator instance;

    private long counter;

    private IDGenerator() {
        counter = 1;
    }

    public static IDGenerator getInstance() {
        if(instance == null) {
            instance = new IDGenerator();
        }
        return instance;
    }

    synchronized public long getUniqueID() {
        counter++;
        return counter;
    }

}