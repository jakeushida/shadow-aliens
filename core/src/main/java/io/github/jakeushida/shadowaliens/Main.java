package io.github.jakeushida.shadowaliens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.screens.StartScreen;

/** Main game entry that shares a SpriteBatch with screens. */
public class Main extends Game {
    // Shared SpriteBatch for all screens to use
    public SpriteBatch batch;

    // Shared assets accessible to all screens
    public TextureAtlas atlas;
    public BitmapFont font;
    public Texture heartTexture;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Load global configuration
        ConfigManager.getInstance().load("global.properties");

        // Load shared assets
        atlas = new TextureAtlas(Gdx.files.internal("sprites.atlas"));
        font = new BitmapFont(Gdx.files.internal("upheaval.fnt"));

        // Use atlas region texture for hearts
        heartTexture = atlas.findRegion("playerLife").getTexture();

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
        if (atlas != null) {
            atlas.dispose();
            atlas = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (heartTexture != null) {
            heartTexture.dispose();
            heartTexture = null;
        }
        super.dispose();
    }
}
