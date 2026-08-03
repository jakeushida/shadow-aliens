package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import io.github.jakeushida.shadowaliens.Main;

public class PauseScreen implements Screen {
    private final Main game;

    public PauseScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Pause initialization can be added when pause UX is implemented.
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.batch != null) {
            game.batch.begin();
            // TODO: draw pause overlay
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
