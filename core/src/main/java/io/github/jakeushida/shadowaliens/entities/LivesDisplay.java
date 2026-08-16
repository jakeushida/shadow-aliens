package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public class LivesDisplay extends GameEntity {
    /** Hearts are drawn at this size regardless of the source region's size. */
    private static final float ICON_SIZE = 24f;

    /**
     * A region rather than a Texture: the heart lives inside sprites.png, so a
     * Texture handle here is the whole 512x128 sheet and drawing it painted
     * every sprite in the game for each life.
     */
    private final TextureRegion heartImage;
    private final float gap;

    public LivesDisplay(float x, float y, TextureRegion heartImage, float gap) {
        super(x, y, RenderLayer.UI, ICON_SIZE, ICON_SIZE);
        this.heartImage = heartImage;
        this.gap = gap;
    }

    @Override
    public void update(float delta) {
        // UI display reflects session state when drawn.
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (heartImage == null) {
            return;
        }

        int lives = GameSession.getInstance().getCurrentLives();
        for (int i = 0; i < lives; i++) {
            float offsetX = x + i * (ICON_SIZE + gap);
            batch.draw(heartImage, offsetX, y, ICON_SIZE, ICON_SIZE);
        }
    }
}
