package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public class Explosion extends GameEntity {
    private final int duration;
    private float elapsed;

    public Explosion(float x, float y, int duration) {
        super(x, y, RenderLayer.UI);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isFinished() {
        return elapsed >= duration;
    }

    @Override
    public void update(float delta) {
        elapsed += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Animation rendering will be wired once assets are integrated.
    }
}
