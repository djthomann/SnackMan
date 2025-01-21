package de.hsrm.mi.swt.projekt.snackman.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

@SpringBootTest
class LobbyTests {

    private Lobby lobby;

    @BeforeEach
    void setup() {
        lobby = new Lobby();
    }

    @Test
    void testGetId() {
        long id = lobby.getId();
        assertTrue(id > 0);
    }

    @Test
    void testMulitpleLobbiesHaveDifferentIds() {
        List<Lobby> lobbies = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            lobbies.add(new Lobby());
        }

        for (int i = 0; i < lobbies.size(); i++) {
            for (int j = i + 1; j < lobbies.size(); j++) {
                assertTrue(lobbies.get(i).getId() != lobbies.get(j).getId());
            }
        }
    }

    @Test
    void testAddAndGetClient() {
        Client client = new Client(Mockito.mock(org.springframework.web.socket.WebSocketSession.class));
        lobby.addClient(client);
        assertEquals(client, lobby.getClient(client.getClientId()));
        assertTrue(lobby.getClientsAsList().contains(client));
    }

    @Test
    void testGameConfig() {
        GameConfig config = new GameConfig();
        lobby.setGameConfig(config);
        assertEquals(config, lobby.getGameConfig());
    }

    @Test
    void testMap() {
        SnackManMap map = new SnackManMap(10, 10);
        lobby.setMap(map);
        assertEquals(map, lobby.getMap());
    }

    @Test
    void testStartGame() {
        GameManager gm = Mockito.mock(GameManager.class);
        Game game = lobby.startGame(gm);
        assertNotNull(game);
    }

    @Test
    void testEqualityOfLobbyGameConfigAndGameGameConfig() {
        GameManager gm = Mockito.mock(GameManager.class);
        GameConfig config = new GameConfig();
        lobby.setGameConfig(config);
        Game game = lobby.startGame(gm);
        assertEquals(config, game.getGameConfig());
    }
}
