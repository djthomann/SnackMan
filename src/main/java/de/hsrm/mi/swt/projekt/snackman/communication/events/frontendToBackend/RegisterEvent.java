package de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;

/**
 * Event that informs Backend under what username a Player wants to be listed as.
 */
public abstract class RegisterEvent extends Event {
    
    private String username;

    public RegisterEvent() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
