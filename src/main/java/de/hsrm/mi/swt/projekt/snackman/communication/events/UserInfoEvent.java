package de.hsrm.mi.swt.projekt.snackman.communication.events;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.PlayerObject;

/** Informs player of other Users in the lobby, including their Username and Role. */
public class UserInfoEvent {

    private String username;
    private PlayerObject role;

    public UserInfoEvent(String username, PlayerObject role) {
        this.username = username;
        this.role = role;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public PlayerObject getRole() {
        return role;
    }
    public void setRole(PlayerObject role) {
        this.role = role;
    }

    


    
}
