package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;

/**
 * Event that informs Frontend about End of a Game, who won and the scores of the players
 */
public class GameEndEvent extends Event {
    
    /*private long id;
    // private Map<Integer, Integer> scores;

    public GameEndEvent(long id) {
        this.setType(EventType.GAME_END);
        this.id = id;
        // this.winner = winner;
        // this.scores = scores;
    }
    /*public int getWinner() {
        return winner;
    }
    public void setWinner(int winner) {
        this.winner = winner;
    }
    public Map<Integer, Integer> getScores() {
        return scores;
    }
    public void setScores(Map<Integer, Integer> scores) {
        this.scores = scores;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }*/

}
