package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import java.util.List;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.PlayerRecord;

/**
 * Event that informs Frontend about End of a Game, who won and the scores of
 * the players
 */
public class GameEndEvent extends Event {

    private String winningTeam;
    private String winnerName;
    private int winnerCaloryCount;
    private List<PlayerRecord> players;

    public GameEndEvent() {
        this.setType(EventType.GAME_END);
    }

    public GameEndEvent(String winningTeam, String winnerName, int winnerCaloryCount, List<PlayerRecord> players) {
        this.setType(EventType.GAME_END);
        this.winningTeam = winningTeam;
        this.winnerName = winnerName;
        this.winnerCaloryCount = winnerCaloryCount;
        this.players = players;
    }

    public String getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(String winningTeam) {
        this.winningTeam = winningTeam;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public int getWinnerCaloryCount() {
        return winnerCaloryCount;
    }

    public void setWinnerCaloryCount(int winnerCaloryCount) {
        this.winnerCaloryCount = winnerCaloryCount;
    }

    public List<PlayerRecord> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerRecord> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "GameEndEvent{" +
                "winningTeam='" + winningTeam + '\'' +
                ", winnerName='" + winnerName + '\'' +
                ", winnerCaloryCount=" + winnerCaloryCount +
                ", players=" + players +
                '}';
    }
}