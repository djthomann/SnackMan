package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import org.python.modules.synchronize;

public class IDFactory {
    
    private static long counter;

    private static IDFactory instance;

    private IDFactory() {
        counter = 0;
    }

    public static IDFactory getInstance() {
        if(instance == null) {
            return new IDFactory();
        } else {
            return instance;
        }
    }

    synchronized public long getUniqueID() {
        counter++;
        return counter;
    }

}
