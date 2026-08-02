package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.jakeushida.shadowaliens.contracts.Collidable;
import io.github.jakeushida.shadowaliens.contracts.Movable;
import io.github.jakeushida.shadowaliens.powerups.PowerupEffect;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public class PowerupEntity extends GameEntity implements Collidable, Movable {
    private final Rectangle boundingBox;
    private final PowerupEffect effect;
    private float speedX;
    private float speedY;

    public PowerupEntity(float x, float y, PowerupEffect effect) {
        super(x, y, RenderLayer.PROJECTILES);
        this.effect = effect;
        this.boundingBox = new Rectangle(x, y, 20f, 20f);
        this.speedY = -40f;
    }

    public PowerupEffect getEffect() {
        return effect;
    }

    @Override
    public void update(float delta) {
        move(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Sprite rendering will be wired once assets are integrated.
    }

    @Override
    public Rectangle getBoundingBox() {
        boundingBox.setPosition(x, y);
        return boundingBox;
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof PlayerShip) {
            effect.apply((PlayerShip) other);
        }
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

    @Override
    public void move(float delta) {
        x += speedX * delta;
        y += speedY * delta;
    }
}
