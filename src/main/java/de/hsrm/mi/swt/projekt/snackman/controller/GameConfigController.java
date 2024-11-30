package de.hsrm.mi.swt.projekt.snackman.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.mi.swt.projekt.snackman.configuration.GameConfig;

@RestController
@RequestMapping("/lobby")
public class GameConfigController {

    private Logger logger = LoggerFactory.getLogger(GameConfigController.class);

    @PostMapping("/{n}")
    public ResponseEntity<String> updateGameConfig( 
            @PathVariable("n") Long lobbyID,
            @RequestBody GameConfig gameConfig) {
        logger.info("[GameConfig-Controller] Received Game Config: " + gameConfig);
        return ResponseEntity.ok("GameConfig for Lobby " + lobbyID + " successfully updated.");
    }
}
