package io.github.jakeushida.shadowaliens.screens;

import io.github.jakeushida.shadowaliens.entities.PlayerShip;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.waves.Wave;

public class BattleScreen implements Screen {
    private final Wave wave;
    private final PlayerShip playerShip;

    public BattleScreen() {
        this.wave = new Wave();
        this.playerShip = new PlayerShip(200f, 40f);
    }

    public Wave getWave() {
        return wave;
    }

    public PlayerShip getPlayerShip() {
        return playerShip;
    }

    @Override
    public void show() {
        // Battle entry setup is expanded in gameplay pass.
    }

    @Override
    public void render(float delta) {
        float scaledDelta = delta * GameSession.getInstance().getTimeScale();
        playerShip.update(scaledDelta);
        wave.update(scaledDelta);
    }

    @Override
    public void hide() {
        // No resources to release in base implementation.
    }
}
