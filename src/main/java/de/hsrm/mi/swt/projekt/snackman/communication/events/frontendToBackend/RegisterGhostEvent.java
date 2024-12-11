package de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;

public class RegisterGhostEvent extends RegisterEvent {

    private Ghost role;

    public RegisterGhostEvent() {
        this.setType(EventType.REGISTER_USERNAME);
    }

    public Ghost getRole() {
        return role;
    }

    public void setRole(Ghost role) {
        this.role = role;
    }

}
