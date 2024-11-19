package de.hsrm.mi.swt.projekt.snackman.communication.websocket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    Set<Client> clients = new HashSet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("New WebSocket Connection: " + session.getId());
        clients.add(new Client(session));
        sendClientInfo();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Message received: " + message.getPayload());
        
        String messageString = message.getPayload();
        String returnString = "Server Received: " + message.getPayload();

        // Should be handed to Controller
        if(messageString.startsWith("KEY")) {
            String key = messageString.split(":")[1];
            returnString = "MOVE:" + key;
        }

        session.sendMessage(new TextMessage(returnString));
    }

    /**
     * Send information of connected Clients to all Clients --> should be handed to different class
     * @throws Exception
     */
    public void sendClientInfo() throws Exception {

        if (clients.size() < 2) {
            logger.info("Less than 2 connections, not informing clients");
            return;
        }

        logger.info("Informing Clients: " + clientsString());

        for (Client c : clients) {
            c.getSession().sendMessage(new TextMessage(clients.toString()));
        }
    }

    /**
     * Removes Client from Client Set and informs other Clients 
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket Connection closed: " + session.getId());
        
        clients.remove(new Client(session));

        sendClientInfo();
    }

    public String clientsString() {
        return clients.toString();
    }
}
