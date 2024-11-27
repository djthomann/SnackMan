package de.hsrm.mi.swt.projekt.snackman.communication.events;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;

public class EatEvent extends Event {

    private int calories;
    private Food eaten;

    public EatEvent() {
        super();
        setType(EventType.EAT);
    }

    public Food getEaten() {
        return eaten;
    }
    public void setEaten(Food eaten) {
        this.eaten = eaten;
    }

    public int getCalories() {
        return calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }


    
}
