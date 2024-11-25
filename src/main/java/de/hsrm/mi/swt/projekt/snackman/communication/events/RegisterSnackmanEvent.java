package de.hsrm.mi.swt.projekt.snackman.communication.events;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class RegisterSnackmanEvent extends RegisterEvent {

    private SnackMan role;

    public RegisterSnackmanEvent() {
        super();
        type = EventType.REGISTER_SNACKMAN;
    }

    public SnackMan getRole() {
        return role;
    }

    public void setRole(SnackMan role) {
        this.role = role;
    }

}
