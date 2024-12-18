package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;


import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;

public class DisappearEvent extends Event {
    
    private Food food; 
    private long gameId; 

    public DisappearEvent(long id, Food food) {
        super(); 
        setType(EventType.DISAPPEAR); 
        this.food = food;
        gameId = id; 
    }

    public Food getFood() {
        return food; 
    }
}
