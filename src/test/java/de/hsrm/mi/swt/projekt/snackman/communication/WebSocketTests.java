package de.hsrm.mi.swt.projekt.snackman.communication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.communication.websocket.WebSocketHandler;

@SpringBootTest
public class WebSocketTests {
    
    private WebSocketHandler webSocketHandler;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        webSocketHandler = new WebSocketHandler();
        session = Mockito.mock(WebSocketSession.class);
    }

    @Test
    void testAfterConnectionEstablished() throws Exception {
        // Act
        webSocketHandler.afterConnectionEstablished(session);
        verify(session, never()).sendMessage(any(TextMessage.class));
    }

    /**
     * Tests whether messages can be sent and server sends back messages he doesn't know
     * @throws Exception
     */
    @Test
    void testHandleTextMessage_withRegularMessage() throws Exception {

        String payload = "Hello Server";
        TextMessage message = new TextMessage(payload);
        when(session.isOpen()).thenReturn(true);

        webSocketHandler.handleTextMessage(session, message);

        verify(session).sendMessage(new TextMessage("(Default) Server Received: Hello Server"));
    }

    /**
     * Tests whether Handler extracts Event from Keyboard Input and sends the corresponding Event
     * @throws Exception
     */
    @Test
    void testHandleTextMessage_withKeyMessage() throws Exception {
        
        String payload = "KEY:KeyD";
        TextMessage message = new TextMessage(payload);
        when(session.isOpen()).thenReturn(true);

        webSocketHandler.handleTextMessage(session, message);

        verify(session).sendMessage(new TextMessage("MOVE:KeyD"));
    }

    /** Tests whether Username is sent correctly 
     * @throws Exception
    */
    @Test
    void testHandleTextMessage_withUsernameMessage() throws Exception {
        webSocketHandler.getClients().put(session,new Client(session));
        String payload = "USERNAME:larissa";

        TextMessage message = new TextMessage(payload);
        when(session.isOpen()).thenReturn(true);
        webSocketHandler.handleTextMessage(session, message);
        verify(session).sendMessage(new TextMessage("USERNAME:larissa"));
    }

    /** Tests whether clients are extracted and their information being sent
     * @throws Exception
     */
    @Test
    void testSendClientInfo () throws Exception {
         WebSocketSession sessionLarissa = Mockito.mock(WebSocketSession.class);
         WebSocketSession sessionAtta = Mockito.mock(WebSocketSession.class); 
         WebSocketSession sessionDavid= Mockito.mock(WebSocketSession.class); 
         webSocketHandler.getClients().put(sessionAtta,new Client("Atta",sessionAtta));
         webSocketHandler.getClients().put(sessionLarissa,new Client("Larissa",sessionLarissa));
         webSocketHandler.getClients().put(sessionDavid,new Client("David",sessionDavid));

         when(session.isOpen()).thenReturn(true);
         webSocketHandler.sendClientInfo();

         verify(sessionAtta).sendMessage(new TextMessage("OTHERPLAYERINFO:Larissa:David"));
         verify(sessionLarissa).sendMessage(new TextMessage("OTHERPLAYERINFO:Atta:David"));
         verify(sessionDavid).sendMessage(new TextMessage("OTHERPLAYERINFO:Atta:Larissa"));
    }

    /**
     * Test whether problems happen during connection closing
     * @throws Exception
     */
    @Test
    void testAfterConnectionClosed() throws Exception {
        
        webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // No specific session message expected on close
        verify(session, never()).sendMessage(any(TextMessage.class));
        
    }

}
