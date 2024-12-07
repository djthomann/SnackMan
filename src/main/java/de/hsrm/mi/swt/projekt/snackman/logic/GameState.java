package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameStateEvent;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class GameState {

    private Game game;

    private List<Ghost> changedGhosts;
    private List<SnackMan> changedSnackMen;
    private List<Chicken> changedChicken;
    private List<Food> eatenFoods;

    /** synchronized (chnages to gamestate variables need to be declared to all threads) thread that sends out gamestateevent 
     * and then clears all lists every 1/30 seconds while uninterrupted.*/
    public class GameStateThread extends Thread {

        @Override
        public void run() {
            synchronized (this) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(33);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GameStateEvent gameStateEvent = new GameStateEvent(changedGhosts,changedSnackMen,changedChicken,eatenFoods);
                    game.getGameManager().notifyChange(gameStateEvent);
                    changedGhosts.clear();
                    changedSnackMen.clear();
                    changedChicken.clear();
                    eatenFoods.clear();
                }
            }

        }

    }

    /** 
     *  Documents all updates that have been made to Game Objects such as Ghosts, Snackman, Chicken, as well as the food that was consumed.
     */
    public GameState(Game game) {
        this.game = game;
        GameStateThread thread = new GameStateThread();
        thread.start();
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
