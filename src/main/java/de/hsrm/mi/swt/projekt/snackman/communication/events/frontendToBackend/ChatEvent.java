package de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;

public class ChatEvent extends Event {
    private int lobbyID;
    private String username;
    private String text;

    public ChatEvent(int lobbyID, String username, String text) {
        this.setType(EventType.CHAT);
        this.lobbyID = lobbyID;
        this.username = username;
        this.text = text;
    }

    public int getLobbyID() {
        return lobbyID;
    }

    public void setLobbyID(int lobbyID) {
        this.lobbyID = lobbyID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ChatEvent{" +
                "lobbyID=" + lobbyID +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
