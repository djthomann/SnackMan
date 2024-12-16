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

import de.hsrm.mi.swt.projekt.snackman.communication.events.Event;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.LobbyCreateEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.RegisterGhostEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.RegisterSnackmanEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.RegisterUsernameEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.GameConfigEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.ClientIdEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.logic.Lobby;

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
                /**
                 * User registers without Role, sets Unsername and sends back Client ID for
                 * later use...
                 */
                case "REGISTERUSERNAME" -> {
                    RegisterUsernameEvent registerUsernameEvent = gson.fromJson(jsonString,
                            RegisterUsernameEvent.class);

                    clients.get(session).setUsername(registerUsernameEvent.getUsername());
                    ClientIdEvent event = new ClientIdEvent(clients.get(session).getClientId());
                    String json = gson.toJson(event);
                    logger.info("Final JSON for event" + event.getType().toString() + "; " + json);
                    session.sendMessage(new TextMessage(event.getType().toString() + ";" + json));
                }
                case "MAPREQUEST" -> {
                    // Generate or Load a new Map Object, Map it to JSON and send it to frontend

                    long gameId = 2L;  // TODO to be fixed : Hardcoded for Test Game
                    SnackManMap map = new SnackManMap("map_2024-11-26_19_17_39.csv", true);
                    gameManager.createGame(new GameConfig(), gameId, map);
                    
                    // SnackManMap map = new SnackManMap(40, 40);
                    // SnackManMap map = new SnackManMap(MapGenerationConfig.SAVED_MAPS_PATH +
                    // "testFile.csv");
                    // map.saveAsCSV();

                    // logger.info("Map Data:" + map.toString());

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
                case "MAPUPLOAD" -> {
                    SnackManMap map = new SnackManMap(jsonObject.get("content").getAsString(), false);

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
                    logger.info("GameId: " + moveEvent.getGameID() + " | Vector x: " + moveEvent.getMovementVector().x
                            + " | Vector y: " + moveEvent.getMovementVector().y + " | Vector z: "
                            + moveEvent.getMovementVector().z);
                    gameManager.handleEvent(moveEvent);
                }
                case "SET_GAME_CONFIG" -> {
                    // Set GameConfig from event as GameConfig object in gameManager
                    GameConfigEvent gameConfigEvent = gson.fromJson(jsonString, GameConfigEvent.class);
                    gameManager.setGameConfig(gameConfigEvent.getGameConfig(), gameConfigEvent.getGameID());
                }
                case "GET_GAME_CONFIG" -> {
                    // Get existing GameConfigs from GameManager
                    GameConfigEvent gameConfigEvent = gson.fromJson(jsonString, GameConfigEvent.class);
                    GameConfig existingConfig = gameManager.getGameConfig(gameConfigEvent.getGameID());
                    ObjectMapper mapper = new ObjectMapper();
                    String returnString = "";

                    // if there is no GameConfig for the Lobby or the Reset-Button has been pressed,
                    // the form should recieve default values
                    if (existingConfig == null || gameConfigEvent.getGameID() == 0) {
                        try {
                            String json = mapper.writeValueAsString(new GameConfig());
                            returnString = "GAME_CONFIG;" + json;
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            String json = mapper.writeValueAsString(existingConfig);
                            returnString = "GAME_CONFIG;" + json;
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                    session.sendMessage(new TextMessage(returnString));
                }
                case "LOBBY_CREATE_EVENT" -> {
                    LobbyCreateEvent lobbyCreateEvent = gson.fromJson(jsonString, LobbyCreateEvent.class);
                    Lobby newLobby = null;

                    if (lobbyCreateEvent.getId() == 0) {
                        newLobby = gameManager.createLobby();
                    }
                    logger.info("Lobby with ID: " + newLobby.getId() + "created");
                }
                case "LOBBY_SHOW_EVENT" -> {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(gameManager.getAllLobbies());
                    String returnString = "ALL_LOBBIES;" + json;
                    logger.info("Show all Lobbies: " + returnString);
                    session.sendMessage(new TextMessage(returnString));
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid JSON: " + e.getMessage());
        }
    }

    /**
     * Notifies the frontend about the new game state by sending back a JSON string
     * Right now, only the new position after a move event is sent back to the
     * frontend
     * 
     * @param event
     */
    public void notifyFrontend(Event event) {

        // JSON-Conversion
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json;

        // TODO This is a temporary solution, clarify to which sessions events are sent
        // back
        for (WebSocketSession session : this.clients.keySet()) {
            try {
                json = gson.toJson(event);
                logger.info("Final JSON for event" + event.getType().toString() + ": " + json);

                // Synchronize this block to avoid sending messages during invalid states (e.g.
                // enables moving while jumping)
                synchronized (session) {
                    session.sendMessage(new TextMessage(event.getType().toString() + ";" + json));
                }

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send information of connected Clients to all Clients --> TODO: should be
     * handed to different class (GameStartEvent/LobbyEvent)
     * 
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
                logger.info("User: " + c.getUsername());
                if (!c.getSession().equals(client.getSession()) && !c.getUsername().equals("")) {
                    logger.info("Fügt hinzu: " + client.getUsername());
                    returnString += (":" + c.getUsername());
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
