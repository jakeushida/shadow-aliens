package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.math.Rectangle;
import io.github.jakeushida.shadowaliens.contracts.Collidable;
import io.github.jakeushida.shadowaliens.contracts.Movable;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public abstract class EnemyShip extends GameEntity implements Collidable, Movable {
    /** Size of the plain {@code regularEnemy} region in sprites.atlas. */
    public static final float DEFAULT_WIDTH = 56f;
    public static final float DEFAULT_HEIGHT = 56f;

    protected final Rectangle boundingBox;
    protected int arrivalTime;
    protected float speedX;
    protected float speedY;

    protected EnemyShip(float x, float y, int arrivalTime) {
        this(x, y, arrivalTime, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    protected EnemyShip(float x, float y, int arrivalTime, float width, float height) {
        super(x, y, RenderLayer.SHIPS, width, height);
        this.arrivalTime = arrivalTime;
        this.boundingBox = new Rectangle(x, y, width, height);
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
