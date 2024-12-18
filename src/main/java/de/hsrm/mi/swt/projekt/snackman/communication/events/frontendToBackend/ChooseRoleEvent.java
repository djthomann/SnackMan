package de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;

public class ChooseRoleEvent extends Event {
    private long clientID;
    private String username;
    private boolean isSnackMan;

    public ChooseRoleEvent() {
        this.setType(EventType.CHOOSE_ROLE);
    }

    public long getClientID() {
        return clientID;
    }

    public void setClientID(long clientID) {
        this.clientID = clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSnackMan() {
        return isSnackMan;
    }

    public void setSnackMan(boolean isSnackMan) {
        this.isSnackMan = isSnackMan;
    }
  
}
