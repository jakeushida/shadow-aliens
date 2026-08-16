package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public class Explosion extends GameEntity {
    /**
     * Lifetime in seconds. {@link #update(float)} is fed a delta in seconds, so
     * BattleScreen used to pass 30 here and leave every explosion on screen for
     * half a minute.
     */
    private final float duration;
    private float elapsed;

    public Explosion(float x, float y, float duration) {
        super(x, y, RenderLayer.UI);
        this.duration = duration;
    }

    public int getDuration() {
        return (int) duration;
    }

    public boolean isFinished() {
        return elapsed >= duration;
    }

    /** Fraction of the lifetime elapsed, in 0..1, for fading the sprite out. */
    public float getProgress() {
        if (duration <= 0f) {
            return 1f;
        }
        return Math.min(1f, elapsed / duration);
    }

    @Override
    public void update(float delta) {
        elapsed += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Animation rendering is driven by BattleScreen using the shared atlas.
    }
}
