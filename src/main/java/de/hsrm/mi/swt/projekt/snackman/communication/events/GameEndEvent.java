package de.hsrm.mi.swt.projekt.snackman.communication.events;

import java.util.Map;

/**
 * Event that informs Frontend about End of a Game, who won and the scores of the players
 */
public class GameEndEvent extends Event {
    
    private int winner;
    private Map<Integer, Integer> scores;

    public GameEndEvent(int winner, Map<Integer, Integer> scores) {
        type = EventType.GAME_END;
        this.winner = winner;
        this.scores = scores;
    }
    public int getWinner() {
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

}
