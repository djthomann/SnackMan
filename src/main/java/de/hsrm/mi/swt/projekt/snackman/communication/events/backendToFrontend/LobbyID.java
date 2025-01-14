package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;

public class LobbyID extends Event {
    private long lobbyID;

    public LobbyID(long lobbyID) {
        this.setType(EventType.LOBBY_ID);
    }

    public long getLobbyID() {
        return lobbyID;
    }

    public void setLobbyID(long lobbyID) {
        this.lobbyID = lobbyID;
    }
}