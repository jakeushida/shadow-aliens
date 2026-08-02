package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public abstract class GameEntity {
    protected float x;
    protected float y;
    protected RenderLayer layer;

    protected GameEntity(float x, float y, RenderLayer layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public RenderLayer getLayer() {
        return layer;
    }

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch);
}
