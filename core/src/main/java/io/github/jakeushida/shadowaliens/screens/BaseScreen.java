package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.rendering.TextRenderer;

/**
 * Shared viewport, batch and text plumbing for every screen.
 *
 * <p>Screens used to read {@code Gdx.graphics.getWidth()} directly and never
 * touched a camera, so SpriteBatch kept the projection it was constructed with
 * and resizing the window drew everything in the wrong place. Everything now
 * lays out against the shared {@link Main#viewport} world size instead.
 */
public abstract class BaseScreen implements Screen {
    protected final Main game;
    protected final TextRenderer text;

    protected BaseScreen(Main game) {
        this.game = game;
        this.text = game.text;
    }

    protected float worldWidth() {
        return game.viewport.getWorldWidth();
    }

    protected float worldHeight() {
        return game.viewport.getWorldHeight();
    }

    /** Clears the screen and points the shared batch at the viewport camera. */
    protected void beginFrame(float red, float green, float blue) {
        Gdx.gl.glClearColor(red, green, blue, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
    }

    /** Clears using {@code background.colour} from the config. */
    protected void beginFrame() {
        float[] background = backgroundColour();
        beginFrame(background[0], background[1], background[2]);
    }

    /** Parses a {@code "r, g, b"} config value given in 0-255 into 0-1 floats. */
    protected static float[] backgroundColour() {
        String[] parts = ConfigManager.getInstance().getString("background.colour").split(",");
        return new float[]{
            Float.parseFloat(parts[0].trim()) / 255f,
            Float.parseFloat(parts[1].trim()) / 255f,
            Float.parseFloat(parts[2].trim()) / 255f
        };
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void show() {
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
