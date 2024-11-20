package de.hsrm.mi.swt.projekt.snackman.communication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

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
