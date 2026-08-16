package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public abstract class GameEntity {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected RenderLayer layer;

    protected GameEntity(float x, float y, RenderLayer layer) {
        this(x, y, layer, 0f, 0f);
    }

    protected GameEntity(float x, float y, RenderLayer layer, float width, float height) {
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    /** Sprite width in pixels; hitboxes are sized from this so they match what is drawn. */
    public float getWidth() {
        return width;
    }

    /** Sprite height in pixels; hitboxes are sized from this so they match what is drawn. */
    public float getHeight() {
        return height;
    }

    public float getCentreX() {
        return x + width / 2f;
    }

    public float getCentreY() {
        return y + height / 2f;
    }

    public RenderLayer getLayer() {
        return layer;
    }

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch);
}
