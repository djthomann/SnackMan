package de.hsrm.mi.swt.projekt.snackman.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.level.SnackManMap;

@SpringBootTest
class GameTests {

    private Game testGame;
    private GameManager mockManager;
    private Lobby mockLobby;
    private GameConfig mockConfig;
    private SnackManMap mockMap;
    private List<Client> mockClients;

    @BeforeEach
    void setup() {
        mockManager = Mockito.mock(GameManager.class);
        mockLobby = Mockito.mock(Lobby.class);
        mockConfig = Mockito.mock(GameConfig.class);
        mockMap = Mockito.mock(SnackManMap.class);
        mockClients = new ArrayList<>();

        Mockito.when(mockLobby.getId()).thenReturn(1L);
        Mockito.when(mockLobby.getGameConfig()).thenReturn(mockConfig);
        Mockito.when(mockLobby.getMap()).thenReturn(mockMap);
        Mockito.when(mockLobby.getClientsAsList()).thenReturn(mockClients);

        Mockito.when(mockConfig.getGameTime()).thenReturn(50);
        Mockito.when(mockConfig.getScoreToWin()).thenReturn(100);

        testGame = new Game(mockLobby, mockManager);
    }

    @Test
    void testRegisterCalorieListeners() {
        assertDoesNotThrow(() -> testGame.registerCalorieListeners());
    }

    @Test
    void testDetermineWinnerEmptyMap() {
        Map<Long, Integer> scores = new HashMap<>();
        assertEquals(-1, testGame.determineWinner(scores));
    }

    @Test
    void testDetermineWinnerSingleEntry() {
        Map<Long, Integer> scores = new HashMap<>();
        scores.put(2L, 125);

        assertEquals(2L, testGame.determineWinner(scores));
    }

    @Test
    void testDetermineWinnerMultipleEntries() {
        Map<Long, Integer> scores = new HashMap<>();
        scores.put(1L, 100);
        scores.put(2L, 125);
        scores.put(3L, 85);

        assertEquals(2L, testGame.determineWinner(scores));
    }
}