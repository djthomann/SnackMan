package de.hsrm.mi.swt.projekt.snackman.communication.websocket;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.hsrm.mi.swt.projekt.snackman.communication.events.*;
import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterGhostEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterSnackmanEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterUsernameEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.MapGenerationConfig;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;

public class WebSocketHandler extends TextWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    GameManager gameManager = new GameManager(this, "test");

    Map<WebSocketSession, Client> clients = new HashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("New WebSocket Connection: " + session.getId());
        clients.put(session, new Client(session));
    }

    /**
     * Receive Json from Frontend and turn into Java Objects (Events)...
     * 
     * @param session The session that sent the message.
     * @param message The message that was sent.
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        logger.info("Message received: " + message.getPayload());
        String jsonString = message.getPayload();

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();

            switch (type) {
                // User registered as Snackman
                case "REGISTERSNACKMAN" -> {
                    RegisterSnackmanEvent registerSnackmanEvent = gson.fromJson(jsonString,
                            RegisterSnackmanEvent.class);

                    clients.get(session).setUsername(registerSnackmanEvent.getUsername());
                    clients.get(session).setRole(registerSnackmanEvent.getRole());
                }
                // User registered as Ghost
                case "REGISTERGHOST" -> {
                    RegisterGhostEvent registerGhostEvent = gson.fromJson(jsonString,
                            RegisterGhostEvent.class);

                    clients.get(session).setUsername(registerGhostEvent.getUsername());
                    clients.get(session).setRole(registerGhostEvent.getRole());
                }
                // User registered without Role
                case "REGISTERUSERNAME" -> {
                    RegisterUsernameEvent registerUsernameEvent = gson.fromJson(jsonString,
                            RegisterUsernameEvent.class);

                    clients.get(session).setUsername(registerUsernameEvent.getUsername());
                }
                case "MAP" -> {
                    // Generate or Load a new Map Object, Map it to JSON and send it to frontend

                    SnackManMap map = new SnackManMap(MapGenerationConfig.SAVED_MAPS_PATH + "map_2024-11-26_19_17_39.csv");
                    // SnackManMap map = new SnackManMap(40, 40);
                    // SnackManMap map = new SnackManMap(MapGenerationConfig.SAVED_MAPS_PATH + "testFile.csv");
                    // map.saveAsCSV();

                    //logger.info("Map Data:" + map.toString());

                    // JSON-Conversion
                    ObjectMapper mapper = new ObjectMapper();
                    String returnString = "";
                    try {
                        String json = mapper.writeValueAsString(map);
                        returnString = "MAP;" + json;
                        logger.info("Final JSON: " + returnString);
                        session.sendMessage(new TextMessage(returnString));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                case "MOVE" -> {
                    MoveEvent moveEvent = gson.fromJson(jsonString, MoveEvent.class);
                    logger.info("GameId: " + moveEvent.getGameID() + "Vector x: " + moveEvent.getMovementVector().x);
                    gameManager.handleEvent(moveEvent);
                }

            }

        } catch (

        JsonSyntaxException e) {
            System.out.println("Invalid JSON: " + e.getMessage());
        
        }
    }

    /** Called by GameManager and converts Event to JSON to send to Frontend */
    public void sendMessage(Event event) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json;


        switch (event.getType()) {
                    case COLLISION -> throw new UnsupportedOperationException("Unimplemented case: " + event.getType());
                    case GAME_END -> throw new UnsupportedOperationException("Unimplemented case: " + event.getType());
                    case GAME_START -> throw new UnsupportedOperationException("Unimplemented case: " + event.getType());
                    case GAME_STATE -> throw new UnsupportedOperationException("Unimplemented case: " + event.getType());
                    case MOVE -> {
                        json = gson.toJson(event);
                    }
                    case REGISTER_GHOST -> throw new UnsupportedOperationException("Unimplemented case: " + event.getType());
                    case REGISTER_SNACKMAN -> throw new UnsupportedOperationException("Unimplemented case: " + event.getType());
                    case REGISTER_USERNAME -> throw new UnsupportedOperationException("Unimplemented case: " + event.getType());
                    case USER_INFO -> throw new UnsupportedOperationException("Unimplemented case: " + event.getType());
                    default -> throw new IllegalArgumentException("Unexpected value: " + event.getType()); 
        }

        // TODO: Damit wir das Event zurück senden bräuchten wir eine Verwaltung der Clients bzw. GameStateEvent satt move event. 
    }

    /**
     * Notifies the frontend about the new game state by sending back a JSON string
     * Right now, only the new position after a move event is sent back to the frontend
     * 
     * @param event
     */
    public void notifyFrontend(Event event) {

        // JSON-Conversion
        ObjectMapper mapper = new ObjectMapper();
        String returnString = "";

        // TODO This is a temporary solution, clarify to which sessions events are sent back
        for(WebSocketSession session : this.clients.keySet()) {

            try {
                String json = mapper.writeValueAsString(event);
                returnString = "MOVE;" + json;
                logger.info("Final JSON for move event: " + returnString);
                session.sendMessage(new TextMessage(returnString));

                logger.info("Move Event was sent back to client " + this.clients.get(session).getUsername());
    
            } catch (JsonProcessingException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Send information of connected Clients to all Clients --> TODO: should be handed to different class (GameStartEvent/LobbyEvent)
     * @throws Exception
     */
    public void sendClientInfo() throws Exception {

        if (clients.size() < 2) {
            logger.info("Less than 2 connections, not informing clients");
            return;
        }

        logger.info("Informing Clients: " + clientsString());
        String returnString = "";

        for (Client client : clients.values()) {

            // All Clients except themselves
            for (Client c : clients.values()) {
                logger.info("User: "+c.getUsername());
                if (!c.getSession().equals(client.getSession()) && !c.getUsername().equals("")) {
                    logger.info("Fügt hinzu: " + client.getUsername());
                    returnString += (":"+c.getUsername());
                } 
            }

            client.getSession().sendMessage(new TextMessage("OTHERPLAYERINFO" + returnString));
            returnString = "";
        }

    }

    /**
     * Removes Client from Client Set and informs other Clients
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket Connection closed: " + session.getId());
        clients.remove(session);
    }

    public String clientsString() {
        return clients.toString();
    }

    public Map<WebSocketSession, Client> getClients() {
        return clients;
    }
}
