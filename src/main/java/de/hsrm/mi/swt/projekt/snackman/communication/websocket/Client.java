package de.hsrm.mi.swt.projekt.snackman.communication.websocket;

import org.springframework.web.socket.WebSocketSession;

public class Client {

    private String username;
    private WebSocketSession session;

    public Client(WebSocketSession session) {
        this("", session);
    }

    public Client(String username, WebSocketSession session) {
        this.username = username;
        this.session = session;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public WebSocketSession getSession() {
        return session;
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
