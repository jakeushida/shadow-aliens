package io.github.jakeushida.shadowaliens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.screens.StartScreen;

/** Main game entry that shares a SpriteBatch with screens. */
public class Main extends Game {
    // Shared SpriteBatch for all screens to use
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Initialize configuration here if needed

        // Launch the initial Start Screen
        this.setScreen(new StartScreen(this));
    }

    @Override
    public void render() {
        // Delegate rendering to the active screen
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        super.dispose();
    }
}
