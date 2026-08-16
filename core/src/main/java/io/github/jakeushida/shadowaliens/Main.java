package io.github.jakeushida.shadowaliens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.rendering.TextRenderer;
import io.github.jakeushida.shadowaliens.screens.StartScreen;

/** Main game entry that shares a SpriteBatch, viewport and assets with screens. */
public class Main extends Game {
    // Shared SpriteBatch for all screens to use
    public SpriteBatch batch;

    /**
     * Fixed design resolution shared by every screen. Screens position text
     * against these numbers, so a FitViewport keeps the layout intact when the
     * window is resized; without it SpriteBatch keeps the projection it was
     * built with and everything is drawn at the wrong place after a resize.
     */
    public Viewport viewport;

    // Shared assets accessible to all screens
    public TextureAtlas atlas;
    public BitmapFont font;
    public TextureRegion heartRegion;
    public TextRenderer text;

    /** 1x1 white pixel, tinted and stretched to draw flat colour quads. */
    public Texture blankTexture;

    @Override
    public void create() {
        // Load global configuration first: the viewport size comes from it.
        ConfigManager config = ConfigManager.getInstance();
        config.load("global.properties");

        batch = new SpriteBatch();
        text = new TextRenderer();
        viewport = new FitViewport(config.getInt("window.width"), config.getInt("window.height"));

        // Load shared assets
        atlas = new TextureAtlas(Gdx.files.internal("sprites.atlas"));
        font = new BitmapFont(Gdx.files.internal("upheaval.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Keep the atlas region rather than its backing texture: the backing
        // texture is the whole 512x128 sprite sheet, so drawing it as a heart
        // painted every sprite in the game at once.
        heartRegion = atlas.findRegion("playerLife");

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        blankTexture = new Texture(pixmap);
        pixmap.dispose();

        // Launch the initial Start Screen
        setScreen(new StartScreen(this));
    }

    @Override
    public void dispose() {
        // Game.dispose() only hides the active screen, it never disposes it.
        Screen current = getScreen();
        if (current != null) {
            setScreen(null);
            current.dispose();
        }
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (blankTexture != null) {
            blankTexture.dispose();
            blankTexture = null;
        }
        // Dispose the atlas last, and never dispose heartRegion's texture
        // separately: it is a page owned by the atlas, so the old code disposed
        // the same GL texture twice.
        if (atlas != null) {
            atlas.dispose();
            atlas = null;
        }
        heartRegion = null;
        super.dispose();
    }
}
