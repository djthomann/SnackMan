package de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;

public class LobbyCreateEvent extends Event {
    private int id;

    public LobbyCreateEvent(int id) {
        this.setType(EventType.LOBBY_CREATE_EVENT);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    } 
}
