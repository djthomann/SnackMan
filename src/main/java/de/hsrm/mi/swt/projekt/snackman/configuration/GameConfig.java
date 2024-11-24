package de.hsrm.mi.swt.projekt.snackman.configuration;

public class GameConfig {

    public int scoreToWin;
    public float speedModifier;
    public int snackManSpeed;
    public int ghostSpeed;
    public int chickenSpeed;
    public int mapWidth;
    public int mapHeight;
    
    // In seconds
    public int gameTime;

    public GameConfig() {
        // Placeholder default values for the beginning -> change later to reasonable default values
        this.scoreToWin = 1;
        this.speedModifier = 1;
        this.snackManSpeed = 1;
        this.ghostSpeed = 1;
        this.chickenSpeed = 1;
        this.gameTime = 30;
        this.mapWidth = 10;
        this.mapHeight = 10;
    }
    
}
