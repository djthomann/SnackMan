package de.hsrm.mi.swt.projekt.snackman.communication.events;

/**
 * Event that informs Backend under what username a Player wants to be listed
 */
public class RegisterEvent extends Event {
    
    private String username;

    public RegisterEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
