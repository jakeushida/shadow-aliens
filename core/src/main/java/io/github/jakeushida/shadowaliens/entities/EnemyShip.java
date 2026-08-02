package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.math.Rectangle;
import io.github.jakeushida.shadowaliens.contracts.Collidable;
import io.github.jakeushida.shadowaliens.contracts.Movable;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public abstract class EnemyShip extends GameEntity implements Collidable, Movable {
    private static final float DEFAULT_WIDTH = 40f;
    private static final float DEFAULT_HEIGHT = 28f;

    protected final Rectangle boundingBox;
    protected int arrivalTime;
    protected float speedX;
    protected float speedY;

    protected EnemyShip(float x, float y, int arrivalTime) {
        super(x, y, RenderLayer.SHIPS);
        this.arrivalTime = arrivalTime;
        this.boundingBox = new Rectangle(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public Rectangle getBoundingBox() {
        boundingBox.setPosition(x, y);
        return boundingBox;
    }

    @Override
    public void onCollision(Collidable other) {
        // Damage and destruction rules are introduced in gameplay pass.
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
