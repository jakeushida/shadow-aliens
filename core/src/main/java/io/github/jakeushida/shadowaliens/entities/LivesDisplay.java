package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public class LivesDisplay extends GameEntity {
    private final Texture heartImage;
    private final float gap;

    public LivesDisplay(float x, float y, Texture heartImage, float gap) {
        super(x, y, RenderLayer.UI);
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
            float offsetX = x + i * (heartImage.getWidth() + gap);
            batch.draw(heartImage, offsetX, y);
        }
    }
}
