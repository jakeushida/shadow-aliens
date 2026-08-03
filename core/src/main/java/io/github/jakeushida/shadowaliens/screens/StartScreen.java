package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.managers.GameSession;

public class StartScreen implements Screen {
    private final Main game;

    public StartScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        GameSession.getInstance().reset();
        ConfigManager.getInstance().loadDifficulty("medium");
    }

    @Override
    public void render(float delta) {
        // Clear background
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Example: draw a placeholder using the shared batch
        if (game.batch != null) {
            game.batch.begin();
            // TODO: draw title/menus using fonts and assets
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
    }

    @Override
    public void dispose() {
    }
}
