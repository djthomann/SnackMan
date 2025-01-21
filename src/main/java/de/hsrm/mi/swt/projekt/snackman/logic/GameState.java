package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameStateEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.ChickenRecord;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.FoodRecord;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.GhostRecord;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.SnackManRecord;

public class GameState {

    private final Game game;

    private Set<GhostRecord> changedGhosts;
    private Set<SnackManRecord> changedSnackMen;
    private Set<ChickenRecord> changedChicken;
    private Set<FoodRecord> eatenFoods;
    private Set<FoodRecord> laidEggs;
    private long lastSentTime;
    private boolean firstSend = true;
    private GameStateThread thread = new GameStateThread();

    /**
     * synchronized (changes to game state variables need to be declared to all
     * threads) thread that sends out gamestateevent
     * and then clears all lists every 1/30 seconds while uninterrupted.
     */
    public class GameStateThread extends Thread {

        @Override
        public void run() {
            synchronized (this) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        this.interrupt();
                    }
                    if (!changedGhosts.isEmpty() || !changedSnackMen.isEmpty() || !changedChicken.isEmpty()
                            || !eatenFoods.isEmpty() || !laidEggs.isEmpty() || lastSentTime != game.getRemainingSeconds() || firstSend) {

                        // clone lists to prevent concurrent modification
                        GameStateEvent gameStateEvent = new GameStateEvent(
                                new ArrayList<>(changedGhosts),
                                new ArrayList<>(changedSnackMen),
                                new ArrayList<>(changedChicken),
                                new ArrayList<>(eatenFoods),
                                new ArrayList<>(laidEggs),
                                game.getRemainingSeconds()
                        );
                        for(Client client : game.getClients()) {
                            game.getGameManager().notifyChange(client, gameStateEvent);
                        }
                        if(firstSend) {
                            firstSend = false;
                        }

                        changedGhosts.clear();
                        changedSnackMen.clear();
                        changedChicken.clear();
                        eatenFoods.clear();
                        laidEggs.clear();
                        lastSentTime = game.getRemainingSeconds();
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
        this.changedGhosts = new HashSet<>();
        this.changedSnackMen = new HashSet<>();
        this.changedChicken = new HashSet<>();
        this.eatenFoods = new HashSet<>();
        this.laidEggs = new HashSet<>();
        GameStateThread thread = new GameStateThread();
        thread.start();
    }

    public synchronized void addChangedGhost(Ghost ghost) {
        changedGhosts.add(ghost.toRecord());
    }

    public synchronized void addChangedSnackMan(SnackMan snackman) {
        changedSnackMen.add(snackman.toRecord());
    }

    public synchronized void addChangedChicken(Chicken chicken) {
        changedChicken.add(chicken.toRecord());
    }

    public synchronized void addEatenFood(Food food) {
        eatenFoods.add(food.toRecord());
    }

    public synchronized void addLaidEgg(Food food) {
        laidEggs.removeIf(record -> record.objectId() == food.getObjectId());
        laidEggs.add(food.toRecord());
    }

    public Set<GhostRecord> getChangedGhosts() {
        return changedGhosts;
    }

    public void setChangedGhosts(Set<GhostRecord> changedGhosts) {
        this.changedGhosts = changedGhosts;
    }

    public Set<SnackManRecord> getChangedSnackMen() {
        return changedSnackMen;
    }

    public void setChangedSnackMen(Set<SnackManRecord> changedSnackMen) {
        this.changedSnackMen = changedSnackMen;
    }

    public Set<ChickenRecord> getChangedChicken() {
        return changedChicken;
    }

    public void setChangedChicken(Set<ChickenRecord> changedChicken) {
        this.changedChicken = changedChicken;
    }

    public Set<FoodRecord> getEatenFoods() {
        return eatenFoods;
    }

    public void setEatenFoods(Set<FoodRecord> eatenFoods) {
        this.eatenFoods = eatenFoods;
    }

    public Set<FoodRecord> getLaidEggs() {
        return laidEggs;
    }

    public void setLaidEggs(Set<FoodRecord> laidEggs) {
        this.laidEggs = laidEggs;
    }

    public void interrupt() {
        thread.interrupt();
    }

}
