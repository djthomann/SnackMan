package de.hsrm.mi.swt.projekt.snackman.logic;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.hsrm.mi.swt.projekt.snackman.communication.events.RegisterEvent;
import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Chicken;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Ghost;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.MovableAndSubscribable;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.SnackMan;
import de.hsrm.mi.swt.projekt.snackman.model.gameEntities.Subscribable;
import de.hsrm.mi.swt.projekt.snackman.model.level.Map;

public class GameTests {

    private Game game;
    private GameConfig gameConfig = new GameConfig();
    // ArrayList with Mock-Objects
    private ArrayList<MovableAndSubscribable> allMovables = new ArrayList<>() {{

        add(Mockito.mock(SnackMan.class));
        add(Mockito.mock(Ghost.class));
        add(Mockito.mock(Chicken.class));

    }};
    private Map map = new Map(gameConfig.mapWidth, gameConfig.mapHeight);

    @BeforeEach
    void setUp() {
        game = new Game(0, gameConfig, allMovables, map);
    }

    /**
     * Tests whether every event the game receives gets passed on to every subscriber of its eventBus
     */
    @Test
    void testReveiveEvent() {
        RegisterEvent testEvent = new RegisterEvent("Testname");
        game.receiveEvent(testEvent);

        for (Subscribable actSubscriber : game.getAllSubscribers()) {
            verify(actSubscriber).handle(testEvent);
        }
    }
    
}
