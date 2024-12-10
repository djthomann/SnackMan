package de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;

public class RegisterSnackmanEvent extends RegisterEvent {

    private SnackMan role;

    public RegisterSnackmanEvent() {
        this.setType(EventType.REGISTER_SNACKMAN);
    }

    public SnackMan getRole() {
        return role;
    }

    public void setRole(SnackMan role) {
        this.role = role;
    }

}
