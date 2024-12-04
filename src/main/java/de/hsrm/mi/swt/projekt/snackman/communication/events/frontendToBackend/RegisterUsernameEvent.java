package de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend;

import de.hsrm.mi.swt.projekt.snackman.communication.events.EventType;

public class RegisterUsernameEvent extends RegisterEvent {

    public RegisterUsernameEvent() {
        super();
        this.setType(EventType.REGISTER_GHOST);
    }

}
