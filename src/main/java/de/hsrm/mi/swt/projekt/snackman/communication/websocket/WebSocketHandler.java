package de.hsrm.mi.swt.projekt.snackman.communication.websocket;


import java.util.HashMap;
import java.util.Map;

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

import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterGhostEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterSnackmanEvent;
import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterUsernameEvent;

public class WebSocketHandler extends TextWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

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

            }

        } catch (

        JsonSyntaxException e) {
            System.out.println("Invalid JSON: " + e.getMessage());
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
