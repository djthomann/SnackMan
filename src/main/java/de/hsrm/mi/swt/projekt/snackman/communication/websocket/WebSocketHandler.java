package de.hsrm.mi.swt.projekt.snackman.communication.websocket;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.hsrm.mi.swt.projekt.snackman.communication.events.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterGhostEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterSnackmanEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterUsernameEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hsrm.mi.swt.projekt.snackman.configuration.MapGenerationConfig;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;

public class WebSocketHandler extends TextWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    GameManager gameManager = new GameManager(this);

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

                    SnackManMap map = new SnackManMap(MapGenerationConfig.SAVED_MAPS_PATH + "map_2024-11-24_19_50_17.csv");
                    // SnackManMap map = new SnackManMap(40, 40);
                    // SnackManMap map = new SnackManMap(MapGenerationConfig.SAVED_MAPS_PATH + "testFile.csv");
                    // map.saveAsCSV();

                    logger.info("Map Data:" + map.toString());

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

        String messageString = message.getPayload();
        String returnString = "(Default) Server Received: " + message.getPayload();

        // TODO: Should be handed to Controller
        if(messageString.startsWith("KEY")) {
            String key = messageString.split(":")[1];
            returnString = "MOVE:" + key;
        } else if (messageString.startsWith("USERNAME")){
            returnString = "USERNAME:";
            // Client is assigned their username...
            logger.info("Session: " + session.getId());
            String username = messageString.split(":")[1];
            clients.get(session).setUsername(username);
            returnString += username;
            // inform other clients...
            sendClientInfo();
        } else if(messageString.startsWith("MAP")) {

            
        }
        // session.sendMessage(new TextMessage(returnString));
        returnString = "";
    }

    /**
     * Send information of connected Clients to all Clients --> TODO: should be handed to different class
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
