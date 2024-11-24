package de.hsrm.mi.swt.projekt.snackman.communication.events;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;

public class RegisterGhostEvent extends RegisterEvent {

    private Ghost role;

    public RegisterGhostEvent() {
        super();
    }

    public Ghost getRole() {
        return role;
    }

    public void setRole(Ghost role) {
        this.role = role;
    }

}
