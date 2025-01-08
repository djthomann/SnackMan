package de.hsrm.mi.swt.projekt.snackman.communication.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.*;

public class Client {

    Logger logger = LoggerFactory.getLogger(Client.class);

    private long clientId;

    private String username;
    private GameObjectType role;
    private WebSocketSession session;

    public Client(WebSocketSession session) {
        this.username = "";
        this.session = session;
        this.role = null;
        this.clientId = IDGenerator.getInstance().getUniqueID();
        logger.info("New Client: Session -" + session + "Id - "+ clientId);

        //set Role to Snackman for developing purposes
        // should later be checked for null before starting game
        role = GameObjectType.SNACKMAN;
    }

    public long getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GameObjectType getRole() {
        return role;
    }

    public void setRole(GameObjectType role) {
        this.role = role;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((session == null) ? 0 : session.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Client other = (Client) obj;
        if (session == null) {
            if (other.session != null)
                return false;
        } else if (!session.equals(other.session))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Client [username=" + username + ", session=" + session + "]";
    }

}
