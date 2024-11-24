package de.hsrm.mi.swt.projekt.snackman.communication.websocket;

import java.util.ArrayList;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Food;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.level.OccupationType;
import de.hsrm.mi.swt.projekt.snackman.model.level.Tile;
import de.hsrm.mi.swt.projekt.snackman.communication.events.MapData;
import de.hsrm.mi.swt.projekt.snackman.configuration.MapGenerationConfig;

public class WebSocketHandler extends TextWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    Map<WebSocketSession,Client> clients = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("New WebSocket Connection: " + session.getId());
        clients.put(session,new Client(session));
        sendClientInfo();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Message received: " + message.getPayload());
        
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

            SnackManMap map = new SnackManMap(MapGenerationConfig.SAVED_MAPS_PATH + "map_2024-11-24_19_50_17.csv");
            // SnackManMap map = new SnackManMap(40, 40);
            // SnackManMap map = new SnackManMap(MapGenerationConfig.SAVED_MAPS_PATH + "testFile.csv");
            map.saveAsCSV();

            logger.info("Phony Data:" + map.toString());

            // JSON-Konvertierung
            ObjectMapper mapper = new ObjectMapper();
            returnString = "";
            try {
                String json = mapper.writeValueAsString(map);
                returnString = "MAP;" + json;
                logger.info("Final JSON: " + returnString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        session.sendMessage(new TextMessage(returnString));
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

        sendClientInfo();
    }

    public String clientsString() {
        return clients.toString();
    }

    public Map<WebSocketSession, Client> getClients() {
        return clients;
    }
}
