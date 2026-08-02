package io.github.jakeushida.shadowaliens.managers;

public final class GameSession {
    private static final GameSession INSTANCE = new GameSession();

    private int currentScore;
    private int currentLives;
    private int currentWave;
    private float timeScale;
    private boolean invincibilityMode;

    private GameSession() {
        reset();
    }

    public static GameSession getInstance() {
        return INSTANCE;
    }

    public void reset() {
        currentScore = 0;
        currentLives = 3;
        currentWave = 1;
        timeScale = 1f;
        invincibilityMode = false;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public int getCurrentLives() {
        return currentLives;
    }

    public void setCurrentLives(int currentLives) {
        this.currentLives = currentLives;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

    public float getTimeScale() {
        return timeScale;
    }

    public void setTimeScale(float timeScale) {
        this.timeScale = timeScale;
    }

    public boolean isInvincibilityMode() {
        return invincibilityMode;
    }

    public void setInvincibilityMode(boolean invincibilityMode) {
        this.invincibilityMode = invincibilityMode;
    }
}
