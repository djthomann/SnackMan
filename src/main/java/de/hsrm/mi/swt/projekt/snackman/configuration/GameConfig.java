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
    private int wallHeight;
    private float snackManRadius;
    private float snackManStep;
    private float ghostRadius;
    private float chickenMinRadius;
    private float chickenMaxRadius;
    private float foodRadius;

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
        this.wallHeight = 3;
        this.snackManRadius = 0.35f;
        this.snackManStep = 0.1f;
        this.ghostRadius = 0.35f;
        this.chickenMinRadius = 0.1f;
        this.chickenMaxRadius = 0.5f;
        this.foodRadius = 0.2f;
        this.chickenCount = 4;
        this.jumpCalories = 100;
    }

    public int getWallHeight() {
        return wallHeight;
    }

    public void setWallHeight(int wallHeight) {
        this.wallHeight = wallHeight;
    }

    public float getSnackManRadius() {
        return snackManRadius;
    }

    public void setSnackManRadius(float snackManRadius) {
        this.snackManRadius = snackManRadius;
    }

    public float getSnackManStep() {
        return snackManStep;
    }

    public void setSnackManStep(float snackManStep) {
        this.snackManStep = snackManStep;
    }

    public float getGhostRadius() {
        return ghostRadius;
    }

    public void setGhostRadius(float ghostRadius) {
        this.ghostRadius = ghostRadius;
    }

    public float getChickenMinRadius() {
        return chickenMinRadius;
    }

    public void setChickenMinRadius(float chickenMinRadius) {
        this.chickenMinRadius = chickenMinRadius;
    }

    public float getChickenMaxRadius() {
        return chickenMaxRadius;
    }

    public void setChickenMaxRadius(float chickenMaxRadius) {
        this.chickenMaxRadius = chickenMaxRadius;
    }

    public float getFoodRadius() {
        return foodRadius;
    }

    public void setFoodRadius(float foodRadius) {
        this.foodRadius = foodRadius;
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
