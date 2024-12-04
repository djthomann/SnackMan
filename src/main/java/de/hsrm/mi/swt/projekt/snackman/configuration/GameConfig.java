package de.hsrm.mi.swt.projekt.snackman.configuration;

public class GameConfig {

    public int scoreToWin;
    public float speedModifier;
    public int snackManSpeed;
    public int ghostSpeed;
    public int chickenSpeed;
    public int mapWidth;
    public int mapHeight;
    public float snackManRadius;
    public float snackManStep;
    public float ghostRadius;
    public float chickenMinRadius;
    public float chickenMaxRadius;
    public float foodRadius;


    // In seconds
    public int gameTime;
    public int chickenCount;
    public int jumpCalories;

    public GameConfig() {
        // Placeholder default values for the beginning -> change later to reasonable default values
        this.scoreToWin = 1;
        this.speedModifier = 1;
        this.snackManSpeed = 1;

        this.ghostSpeed = 1;
        this.chickenSpeed = 1;
        this.gameTime = 10;
        this.mapWidth = 40;
        this.mapHeight = 40;
        this.snackManRadius = 0.35f;
        this.snackManStep = 0.2f;
        this.ghostRadius = 0.35f;
        this.chickenMinRadius = 0.1f;
        this.chickenMaxRadius = 0.5f;
        this.foodRadius = 0.2f;
        this.chickenCount = 4;
        this.jumpCalories = 100;
    }
    
}
