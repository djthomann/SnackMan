package de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;

public class ClientIdEvent extends Event{

    private long id;

    public ClientIdEvent(long id) {
        this.setType(EventType.CLIENT_ID);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
}
