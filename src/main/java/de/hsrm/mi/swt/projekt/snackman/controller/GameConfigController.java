package de.hsrm.mi.swt.projekt.snackman.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;

@RestController
@RequestMapping("/lobby")
public class GameConfigController {

    private final Logger logger = LoggerFactory.getLogger(GameConfigController.class);

    private final Map<Long, GameConfig> gameConfigs = new HashMap<>();

    @CrossOrigin(origins = "http://localhost:5173/")
    @PostMapping("/{n}")
    public ResponseEntity<String> updateGameConfig( 
            @PathVariable("n") Long lobbyID,
            @RequestBody GameConfig gameConfig) {

        gameConfigs.put(lobbyID, gameConfig);
        logger.info("[GameConfig-Controller] Received Game Config");

        return ResponseEntity.ok("GameConfig for Lobby " + lobbyID + " successfully updated.");
    }

    @CrossOrigin(origins = "http://localhost:5173/")
    @GetMapping("/{n}")
    public ResponseEntity<GameConfig> getGameConfig(@PathVariable("n") Long lobbyID) {
        GameConfig gameConfig = gameConfigs.get(lobbyID);

        if (gameConfig == null) {
            logger.info("[GameConfig-Controller] No config for lobby " + lobbyID + " found. Using defaults.");
            gameConfig = new GameConfig();
        }
        logger.info("[GameConfig-Controller] Loading config for lobby " + lobbyID);

        return ResponseEntity.ok(gameConfig);
    }

    public Map<Long, GameConfig> getGameConfigs() {
        return gameConfigs;
    }
}
