package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.math.Rectangle;
import io.github.jakeushida.shadowaliens.contracts.Collidable;
import io.github.jakeushida.shadowaliens.contracts.Movable;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public abstract class Projectile extends GameEntity implements Collidable, Movable {
    protected final Rectangle boundingBox;
    protected float speedX;
    protected float speedY;

    protected Projectile(float x, float y, float width, float height) {
        super(x, y, RenderLayer.PROJECTILES, width, height);
        this.boundingBox = new Rectangle(x, y, width, height);
    }

    @Override
    public Rectangle getBoundingBox() {
        boundingBox.setPosition(x, y);
        return boundingBox;
    }

    @Override
    public void onCollision(Collidable other) {
        // Damage handling is implemented in gameplay pass.
    }

    @Override
    public float getSpeedX() {
        return speedX;
    }

    @Override
    public float getSpeedY() {
        return speedY;
    }

    @Override
    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    @Override
    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }
}
