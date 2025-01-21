package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import de.hsrm.mi.swt.projekt.snackman.communication.websocket.Client;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.logic.CollisionManager;
import de.hsrm.mi.swt.projekt.snackman.logic.GameManager;

@SpringBootTest
class SnackManTests {

    private SnackMan snackMan;
    private GameManager gameManager;
    private CollisionManager collisionManager;
    private GameConfig config;

    @BeforeEach
    void setUp() {
        gameManager = Mockito.mock(GameManager.class);
        collisionManager = Mockito.mock(CollisionManager.class);
        config = new GameConfig();
        List<Client> clients = new ArrayList<>();

        // AI Generated SnackMan
        snackMan = new SnackMan(
                "testUser",
                1L,
                clients,
                999L,
                0f,
                0.8f,
                0f,
                gameManager,
                config,
                collisionManager);
    }

    @Test
    void testInitialCalorieCount() {
        assertTrue(snackMan.getGainedCalories() > 0);
    }

    @Test
    void testJumpConsumesCalories() {
        int before = snackMan.getGainedCalories();
        snackMan.jump();
        assertTrue(snackMan.getGainedCalories() < before);
    }

    @Test
    void testResetCalories() {
        snackMan.resetGainedCalories();
        assertEquals(0, snackMan.getGainedCalories());
    }

    @Test
    void testStunnedAfterGhostCollision() {
        snackMan.reactToGhostCollision();
        assertTrue(snackMan.isStunned());
    }
}