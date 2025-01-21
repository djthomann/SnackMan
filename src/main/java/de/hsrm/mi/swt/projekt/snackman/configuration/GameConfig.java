package de.hsrm.mi.swt.projekt.snackman.configuration;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class GameConfig {

    public final static int WALL_HEIGHT = 5;

    @Positive
    private int scoreToWin;
    @Positive
    private float speedModifier;
    @Positive
    private int snackManSpeed;
    @Positive
    private int ghostSpeed;
    @Positive
    private int chickenSpeed;
    @Positive
    private int mapWidth;
    @Positive
    private int mapHeight;
    @Positive
    private int gameTime;
    @PositiveOrZero
    @Max(4)
    private int chickenCount;
    @Positive
    private int jumpCalories;
    @Positive
    private int wallHeight;
    @Positive
    private float snackManHeight;
    @Positive
    private float ghostHeight;
    @Positive
    private float chickenHeight;
    @Positive
    private float foodHeight;
    @Positive
    private float snackManRadius;
    @Positive
    private float snackManStep;
    @Positive
    private float ghostRadius;
    @Positive
    private float ghostStep;
    @Positive
    private float chickenMinRadius;
    @Positive
    private float chickenMaxRadius;
    @Positive
    private float foodRadius;
    @Positive
    private int ghostCollisionCalories;
    @Positive 
    private int chickenMaxCalories; 

    public GameConfig() {
        this.scoreToWin = 100000;
        this.speedModifier = 1;
        this.snackManSpeed = 1;
        this.ghostSpeed = 1;
        this.chickenSpeed = 1;
        this.gameTime = 300;
        this.mapWidth = 40;
        this.mapHeight = 40;
        this.wallHeight = 3;
        this.snackManHeight = 1.5f;
        this.ghostHeight = 1.5f;
        this.chickenHeight = 1.5f;
        this.foodHeight = 1.0f;
        this.snackManRadius = 0.35f;
        this.snackManStep = 0.1f;
        this.ghostRadius = 0.35f;
        this.ghostStep = 0.1f;
        this.chickenMinRadius = 0.1f;
        this.chickenMaxRadius = 0.5f;
        this.foodRadius = 0.2f;
        this.chickenCount = 4;
        this.jumpCalories = 100;
        this.ghostCollisionCalories = 10;
        this.chickenMaxCalories = 10000; 
    }

    public float getSnackManHeight() {
        return snackManHeight;
    }

    public float getGhostHeight() {
        return ghostHeight;
    }

    public float getChickenHeight() {
        return chickenHeight;
    }

    public float getFoodHeight() {
        return foodHeight;
    }

    public int getWallHeight() {
        return wallHeight;
    }

    public int getChickenMaxCalories() {
        return chickenMaxCalories;
    }

    public void setChickenMaxCalories(int chickenMaxCalories) {
        this.chickenMaxCalories = chickenMaxCalories;
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

    public int getGhostCollisionCalories() {
        return ghostCollisionCalories;
    }

    public float getGhostStep() {
        return ghostStep;
    }

    public void setGhostStep(float ghostStep) {
        this.ghostStep = ghostStep;
    }
    
}
