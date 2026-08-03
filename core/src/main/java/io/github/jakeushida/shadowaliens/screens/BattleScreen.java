package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.entities.PlayerShip;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.waves.Wave;

public class BattleScreen implements Screen {
    private final Main game;
    private final Wave wave;
    private final PlayerShip playerShip;

    public BattleScreen(Main game) {
        this.game = game;
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
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float scaledDelta = delta * GameSession.getInstance().getTimeScale();
        playerShip.update(scaledDelta);
        wave.update(scaledDelta);

        if (game.batch != null) {
            game.batch.begin();
            // TODO: draw player, enemies, UI
            game.batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // No resources to release in base implementation.
    }

    @Override
    public void dispose() {
    }
}
