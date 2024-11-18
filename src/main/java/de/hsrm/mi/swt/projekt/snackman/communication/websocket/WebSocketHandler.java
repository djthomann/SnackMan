package de.hsrm.mi.swt.projekt.snackman.communication.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handles connection and disconnection of clients, also send messages to clients
 * --> right now contains logic which will later be done in Controller Classes
 */
public class WebSocketHandler extends TextWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("New WebSocket Connection: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Message received: " + message.getPayload());
        
        String messageString = message.getPayload();
        String returnString = "Server Received: " + message.getPayload();

        // Should be handed to Controller --> Move logic
        if(messageString.startsWith("KEY")) {
            String key = messageString.split(":")[1];
            returnString = "MOVE:" + key;
        }

        session.sendMessage(new TextMessage(returnString));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket Connection closed: " + session.getId());
    }
}
