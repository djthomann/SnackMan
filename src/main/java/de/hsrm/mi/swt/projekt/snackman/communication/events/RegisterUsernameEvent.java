package de.hsrm.mi.swt.projekt.snackman.communication.events;

public class RegisterUsernameEvent extends RegisterEvent {

    public RegisterUsernameEvent() {
        super();
        type = EventType.REGISTER_GHOST;
    }

}
