package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameStateEvent;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.*;

public class GameState {

    private Game game;

    private List<GhostRecord> changedGhosts;
    private List<SnackManRecord> changedSnackMen;
    private List<ChickenRecord> changedChicken;
    private List<FoodRecord> eatenFoods;

    /**
     * synchronized (chnages to gamestate variables need to be declared to all
     * threads) thread that sends out gamestateevent
     * and then clears all lists every 1/30 seconds while uninterrupted.
     */
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
                    if (!changedGhosts.isEmpty() || !changedSnackMen.isEmpty() || !changedChicken.isEmpty()
                            || !eatenFoods.isEmpty()) {
                        GameStateEvent gameStateEvent = new GameStateEvent(changedGhosts, changedSnackMen,
                                changedChicken, eatenFoods);
                        game.getGameManager().notifyChange(gameStateEvent);
                        changedGhosts.clear();
                        changedSnackMen.clear();
                        changedChicken.clear();
                        eatenFoods.clear();
                    }
                }
            }

        }

    }

    /**
     * Documents all updates that have been made to Game Objects such as Ghosts,
     * Snackman, Chicken, as well as the food that was consumed.
     */
    public GameState(Game game) {
        this.game = game;
        this.changedGhosts = new ArrayList<GhostRecord>();
        this.changedSnackMen = new ArrayList<SnackManRecord>();
        this.changedChicken = new ArrayList<ChickenRecord>();
        this.eatenFoods = new ArrayList<FoodRecord>();
        GameStateThread thread = new GameStateThread();
        thread.start();
    }

    public void addChangedGhost(Ghost ghost) {
        changedGhosts.add(ghost.toRecord());
    }

    public void addChangedSnackMan(SnackMan snackman) {
        changedSnackMen.add(snackman.toRecord());
    }

    public void addChangedChicken(Chicken chicken) {
        changedChicken.add(chicken.toRecord());
    }

    public void addEatenFood(Food food) {
        eatenFoods.add(food.toRecord());
    }

    public List<GhostRecord> getChangedGhosts() {
        return changedGhosts;
    }

    public void setChangedGhosts(List<GhostRecord> changedGhosts) {
        this.changedGhosts = changedGhosts;
    }

    public List<SnackManRecord> getChangedSnackMen() {
        return changedSnackMen;
    }

    public void setChangedSnackMen(List<SnackManRecord> changedSnackMen) {
        this.changedSnackMen = changedSnackMen;
    }

    public List<ChickenRecord> getChangedChicken() {
        return changedChicken;
    }

    public void setChangedChicken(List<ChickenRecord> changedChicken) {
        this.changedChicken = changedChicken;
    }

    public List<FoodRecord> getEatenFoods() {
        return eatenFoods;
    }

    public void setEatenFoods(List<FoodRecord> eatenFoods) {
        this.eatenFoods = eatenFoods;
    }

}
