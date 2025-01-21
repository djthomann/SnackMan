package de.hsrm.mi.swt.projekt.snackman.communication.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
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
import de.hsrm.mi.swt.projekt.snackman.communication.events.GameConfigEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.ClientIdEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.backendToFrontend.GameEndEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.ChatEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.ChooseRoleEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.LobbyCreateEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.MoveEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.RegisterUsernameEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.frontendToBackend.StartGameEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.logic.Game;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;
import de.hsrm.mi.swt.projekt.snackman.logic.Lobby;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.GameObjectType;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.records.LobbyRecord;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

public class WebSocketHandler extends TextWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    GameManager gameManager = new GameManager(this);

    Map<WebSocketSession, Client> clients = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
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
    public void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        logger.info("Message received: " + message.getPayload());
        String jsonString = message.getPayload();

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();

            switch (type) {
                // User chooses role
                case "CHOOSEROLE" -> {
                    ChooseRoleEvent chooseRoleEvent = gson.fromJson(jsonString, ChooseRoleEvent.class);
                    Client client = clients.get(session);

                    client.setUsername(chooseRoleEvent.getUsername());

                    if(chooseRoleEvent.isSnackMan()){
                        client.setRole(GameObjectType.SNACKMAN);
                    } else {
                        client.setRole(GameObjectType.GHOST);
                    }
                }

                 // User registers without Role, sets Unsername and sends back Client ID for
                 // later use...
                case "REGISTERUSERNAME" -> {
                    RegisterUsernameEvent registerUsernameEvent = gson.fromJson(jsonString,
                            RegisterUsernameEvent.class);

                    clients.get(session).setUsername(registerUsernameEvent.getUsername());
                    ClientIdEvent event = new ClientIdEvent(clients.get(session).getClientId());
                    String json = gson.toJson(event);
                    // logger.info("Final JSON for event" + event.getType().toString() + "; " + json);
                    session.sendMessage(new TextMessage(event.getType().toString() + ";" + json));
                }
                case "MAPUPLOAD" -> {
                    SnackManMap map = new SnackManMap(jsonObject.get("content").getAsString(), false);

                    Lobby lobby = gameManager.getLobbyFromClient(clients.get(session));
                    if (lobby != null) {
                        lobby.setMap(map);
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
                    ObjectMapper mapper = new ObjectMapper();

                    // Set GameConfig from event as GameConfig object in gameManager
                    GameConfigEvent gameConfigEvent = gson.fromJson(jsonString, GameConfigEvent.class);
                    gameManager.setGameConfig(gameConfigEvent.getGameConfig(), gameConfigEvent.getGameID());
                    try {
                        String json = mapper.writeValueAsString(gameConfigEvent.getGameConfig());
                        notifyClients(session, gameConfigEvent.getGameID(), "GAME_CONFIG;" + json);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                case "GET_GAME_CONFIG" -> {
                    // Get existing GameConfigs from GameManager
                    GameConfigEvent gameConfigEvent = gson.fromJson(jsonString, GameConfigEvent.class);
                    GameConfig existingConfig = gameManager.getGameConfig(gameConfigEvent.getGameID());
                    ObjectMapper mapper = new ObjectMapper();
                    String returnString = "";

                    // if there is no GameConfig for the Lobby or the Reset-Button has been pressed,
                    // the form should recieve default values
                    if (existingConfig == null || gameConfigEvent.getGameID() == 0) existingConfig = new GameConfig();
                    try {
                        String json = mapper.writeValueAsString(existingConfig);
                        returnString = "GAME_CONFIG;" + json;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    session.sendMessage(new TextMessage(returnString));
                    notifyClients(session, gameConfigEvent.getGameID(), returnString);
                }
                case "RESET_GAME_CONFIG" -> {
                    ObjectMapper mapper = new ObjectMapper();
                    GameConfigEvent gameConfigEvent = gson.fromJson(jsonString, GameConfigEvent.class);
                    GameConfig newGameConfig = new GameConfig();
                    gameManager.setGameConfig(newGameConfig, gameConfigEvent.getGameID());

                    String payload = "GAME_CONFIG;" + mapper.writeValueAsString(newGameConfig);
                    session.sendMessage(new TextMessage(payload));
                    notifyClients(session, gameConfigEvent.getGameID(), payload);
                }
                case "JOIN_LOBBY" -> {
                    gameManager.addClientToLobby(clients.get(session), jsonObject.get("lobbyCode").getAsLong());
                    broadcastMessage("NEW_LOBBY_JOIN");
                }
                case "LEAVE_LOBBY" -> {
                    gameManager.removeClientFromLobby(clients.get(session), jsonObject.get("lobbyCode").getAsLong());
                    broadcastMessage("NEW_LOBBY_LEAVE");
                    break;
                }
                case "GET_PLAYERS" -> {
                    JsonObject jo = new JsonObject();
                    long lobbyCode = Long.parseLong(String.valueOf(jsonObject.get("lobbyCode")));
                    jo.add("players", gson.toJsonTree(gameManager.getPlayersInLobby(lobbyCode)));

                    // JSON-Objekt als String ausgeben
                    String js = gson.toJson(jo);

                    for (WebSocketSession s: gameManager.getLobbyMap().get(lobbyCode).getAllSessions()) {
                        s.sendMessage(new TextMessage("PLAYERS;" + js));
                    }

                }
                case "ROLE" -> {
                    GameObjectType gameObjectType = (jsonObject.get("snackman").getAsBoolean()) ? GameObjectType.SNACKMAN : GameObjectType.GHOST;
                    clients.get(session).setRole(gameObjectType);
                }
                case "LOBBY_CREATE_EVENT" -> {
                    LobbyCreateEvent lobbyCreateEvent = gson.fromJson(jsonString, LobbyCreateEvent.class);
                    Lobby newLobby = null;

                    if (lobbyCreateEvent.getId() == 0) {
                        newLobby = gameManager.createLobby();
                    }
                    logger.info("Lobby with ID: " + newLobby.getId() + "created");

                    // Inform everyone
                    broadcastMessage("NEW_LOBBY_CREATE");

                    // TODO: Send Lobby ID to the creator of the lobby so that he can join the right lobby
                    // session.sendMessage(new TextMessage("LOBBY_ID;" + newLobby.getId()));
                }
                case "LOBBY_SHOW_EVENT" -> {
                    ObjectMapper mapper = new ObjectMapper();
                    List<LobbyRecord> lobbyRecords = gameManager.getAllLobbies().stream().map(Lobby::toRecord).toList();
                    String json = mapper.writeValueAsString(lobbyRecords);
                    String returnString = "ALL_LOBBIES;" + json;
                    logger.info("Show all Lobbies: " + returnString);
                    session.sendMessage(new TextMessage(returnString));
                }

                case "MAP_DATA_REQUEST" -> {
                    long gameID = Long.parseLong(String.valueOf(jsonObject.get("gameID")));
                    String mapData = gameManager.getGameById(gameID).getMap().original().toString();

                    session.sendMessage(new TextMessage("MAP_DATA;" + mapData));
                }

                case "START_GAME" -> {
                    StartGameEvent startGameEvent = gson.fromJson(jsonString, StartGameEvent.class);
                    notifyClients(session, startGameEvent.getGameID(), "FOREIGN_GAMESTART");

                    gameManager.createGame(startGameEvent.getGameID());

                    // TODO gameManager.removeLobby();
                    broadcastMessage("NEW_LOBBY_DELETE"); // now that the game has started, the lobby is no longer needed
                }

                case "END_GAME" -> {
                    GameEndEvent gameEndEvent = gson.fromJson(jsonObject, GameEndEvent.class);
                    Game currentGame = gameManager.getGameById(gameEndEvent.getGameID());
                    GameEndEvent result = (currentGame != null) ? currentGame.generateGameEndEvent() : gameManager.getLastGameEndEvent();
                    SnackManMap map = (currentGame != null) ? currentGame.getMap().original() : gameManager.getLastMapOriginal();
                    gameManager.removeGame(gameEndEvent.getGameID());
                    gameManager.removeLobby(gameEndEvent.getGameID());

                    sendMapData(map, session);
                    logger.info("GameEndEvent generated: " + result);

                    TextMessage messageToSend = new TextMessage(result.getType().toString() + ";" + gson.toJson(result));
                    logger.info("Sending GameEndEvent to all clients: " + messageToSend);
                    session.sendMessage(new TextMessage(result.getType().toString() + ";" + gson.toJson(result)));
                }
                case "CHAT" -> {
                    ChatEvent chatEvent = gson.fromJson(jsonObject, ChatEvent.class);
                    logger.info("ChatEvent received: " + chatEvent);

                    notifyClients(session, chatEvent.getLobbyID(), "CHAT;" + gson.toJson(chatEvent));
                }

                default -> logger.warn("unknown message from FE: " + type);

            }
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid JSON: " + e.getMessage());
        }
    }

    /**
     * Sends all clients a String (except the source)
     * @param source WebSocketSession that is the source of the message
     * @param gameId ID of the looby/game
     * @param payload Message to send
     */
    private void notifyClients(WebSocketSession source, long gameId, String payload) {
        Lobby lobby = gameManager.getLobbyById(gameId);

        for (Client c: lobby.getClientsAsList()) {
            if (c.getSession() != source) {
                try {
                    c.getSession().sendMessage(new TextMessage(payload));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMapData(SnackManMap map, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("MAP_DATA;" + map.toString()));
    }

    /**
     * Notifies the frontend about the new game state by sending back a JSON string
     * Right now, only the new position after a move event is sent back to the
     * frontend
     * 
     * @param event said event
     */
    public void notifyFrontend(Client client, Event event) {

        // JSON-Conversion
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json;

        // TODO This is a temporary solution, clarify to which sessions events are sent back
        try {
            json = gson.toJson(event);
            // logger.info("Final JSON for event " + event.getType().toString() + ": " + json);
            // Synchronize this block to avoid sending messages during invalid states (e.g.
            // enables moving while jumping)
            synchronized (client.getSession()) {
                client.getSession().sendMessage(new TextMessage(event.getType().toString() + ";" + json));
            }

            } catch (IOException e) {
                e.printStackTrace();
            }
        
    }

    /**
     * Removes Client from Client Set and informs other Clients
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) {
        logger.info("WebSocket Connection closed: " + session.getId());
        clients.remove(session);
    }

    public Map<WebSocketSession, Client> getClients() {
        return clients;
    }

    /**
     * Send a message to all connected clients
     * 
     * @param message The message to send
     */
    public void broadcastMessage(String message) {
        for (WebSocketSession session : clients.keySet()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.error("Error sending message " + session.getId(), e);
            }
        }
    }
}
