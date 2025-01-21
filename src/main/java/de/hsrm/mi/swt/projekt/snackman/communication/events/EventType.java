package de.hsrm.mi.swt.projekt.snackman.communication.events;

/**
 * Event Type enumeration for handling in EventController
 */
public enum EventType {
    MOVE, 
    REGISTER_USERNAME,
    CHOOSE_ROLE, 
    COLLISION, 
    GAME_CONFIG, 
    GAME_STATE, GAME_START, GAME_END, 
    USER_INFO, 
    EAT,
    LAY_EGG, 
    LOBBY_CREATE_EVENT,
    CLIENT_ID,
    START_GAME,
    DISAPPEAR,
    REQUEST_GAMESTART,
    NEW_LOBBY_CREATE,
    NEW_LOBBY_JOIN,
    LOBBY_ID,
    CHAT
}
