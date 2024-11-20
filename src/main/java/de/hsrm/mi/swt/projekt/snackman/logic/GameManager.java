package de.hsrm.mi.swt.projekt.snackman.logic;

import java.util.ArrayList;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;

public class GameManager {
    
    public ArrayList<Game> allGames;
    private int nextGameId;

    public GameManager() {
        this.allGames = new ArrayList<Game>();
        this.nextGameId = 0;
    }

    /**
     * Passes on the received event to the game with the matching game id
     * 
     * @param type
     * @param gameId
     */
    public void handleEvent(String type, int gameId, int objectId) {

        for (Game currentGame : allGames) {

            if(currentGame.id == gameId) {
                currentGame.receiveEvent(type, objectId);
            }
        }
    }

    /**
     * Creates a new game
     */
    private void createGame() {
        
        Game newGame = new Game(nextGameId, new GameConfig(), new ArrayList<>());
        allGames.add(newGame);

        nextGameId++;
    }
} 
