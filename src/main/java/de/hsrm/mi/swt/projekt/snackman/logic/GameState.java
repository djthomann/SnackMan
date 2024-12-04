package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class GameState {

    private List<Ghost> changedGhosts;
    private List<SnackMan> changedSnackMen;
    private List<Chicken> changedChicken;
    private List<Food> eatenFoods;

    /** 
     * 
     */
    public GameState() {
    }

    public void addChangedGhost(Ghost ghost) {
        changedGhosts.add(ghost);
    }

    public void addChangedSnackMan(SnackMan snackman) {
        changedSnackMen.add(snackman);
    }

    public void addChangedChicken(Chicken chicken) {
        changedChicken.add(chicken);
    }

    public void addEatenFood(Food food) {
        eatenFoods.add(food);
    }

    public List<Ghost> getChangedGhosts() {
        return changedGhosts;
    }

    public void setChangedGhosts(List<Ghost> changedGhosts) {
        this.changedGhosts = changedGhosts;
    }

    public List<SnackMan> getChangedSnackMen() {
        return changedSnackMen;
    }

    public void setChangedSnackMen(List<SnackMan> changedSnackMen) {
        this.changedSnackMen = changedSnackMen;
    }

    public List<Chicken> getChangedChicken() {
        return changedChicken;
    }

    public void setChangedChicken(List<Chicken> changedChicken) {
        this.changedChicken = changedChicken;
    }

    public List<Food> getEatenFoods() {
        return eatenFoods;
    }

    public void setEatenFoods(List<Food> eatenFoods) {
        this.eatenFoods = eatenFoods;
    }

    
}
