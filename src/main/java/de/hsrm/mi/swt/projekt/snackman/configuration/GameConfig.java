package de.hsrm.mi.swt.projekt.snackman.configuration;

public class GameConfig {

    private int scoreToWin;
    private float speedModifier;
    private int snackManSpeed;
    private int ghostSpeed;
    private int chickenSpeed;
    private int mapWidth;
    private int mapHeight;
    private int gameTime;
    private int chickenCount;
    private int jumpCalories;

    public GameConfig() {
        // Placeholder default values for the beginning -> change later to reasonable default values
        this.scoreToWin = 1;
        this.speedModifier = 1;
        this.snackManSpeed = 1;
        this.ghostSpeed = 1;
        this.chickenSpeed = 1;
        this.gameTime = 10;
        this.mapWidth = 10;
        this.mapHeight = 10;
        this.chickenCount = 4;
        this.jumpCalories = 100;
    }

    public int getScoreToWin() {
        return scoreToWin;
    }

    public void setScoreToWin(int scoreToWin) {
        this.scoreToWin = scoreToWin;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }

    public void setSpeedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
    }

    public int getSnackManSpeed() {
        return snackManSpeed;
    }

    public void setSnackManSpeed(int snackManSpeed) {
        this.snackManSpeed = snackManSpeed;
    }

    public int getGhostSpeed() {
        return ghostSpeed;
    }

    public void setGhostSpeed(int ghostSpeed) {
        this.ghostSpeed = ghostSpeed;
    }

    public int getChickenSpeed() {
        return chickenSpeed;
    }

    public void setChickenSpeed(int chickenSpeed) {
        this.chickenSpeed = chickenSpeed;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public int getChickenCount() {
        return chickenCount;
    }

    public void setChickenCount(int chickenCount) {
        this.chickenCount = chickenCount;
    }

    public int getJumpCalories() {
        return jumpCalories;
    }

    public void setJumpCalories(int jumpCalories) {
        this.jumpCalories = jumpCalories;
    }
    
}
