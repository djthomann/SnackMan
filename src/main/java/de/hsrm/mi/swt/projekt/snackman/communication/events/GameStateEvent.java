package de.hsrm.mi.swt.projekt.snackman.communication.events;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Moveable;

/**
 * Event that informs Frontend about changes in the existence or position of moveable objects and foods
 */
public class GameStateEvent extends Event {
    
    private List<Moveable> updatesMoveables;
    private List<Food> updatesFoods;

    public GameStateEvent(List<Moveable> updatesMoveables, List<Food> updatesFoods) {
        this.updatesMoveables = updatesMoveables;
        this.updatesFoods = updatesFoods;
    }
    public List<Moveable> getUpdatesMoveables() {
        return updatesMoveables;
    }
    
    public void setUpdatesMoveables(List<Moveable> updatesMoveables) {
        this.updatesMoveables = updatesMoveables;
    }
    public List<Food> getUpdatesFoods() {
        return updatesFoods;
    }
    public void setUpdatesFoods(List<Food> updatesFoods) {
        this.updatesFoods = updatesFoods;
    }

}
