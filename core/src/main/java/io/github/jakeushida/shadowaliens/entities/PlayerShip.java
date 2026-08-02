package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.jakeushida.shadowaliens.contracts.Collidable;
import io.github.jakeushida.shadowaliens.contracts.Movable;
import io.github.jakeushida.shadowaliens.contracts.Shooter;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.powerups.PowerupEffect;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public class PlayerShip extends GameEntity implements Collidable, Movable, Shooter {
    private static final float DEFAULT_WIDTH = 48f;
    private static final float DEFAULT_HEIGHT = 32f;

    private final Rectangle boundingBox;
    private int lives;
    private PowerupEffect currentBuff;
    private float speedX;
    private float speedY;
    private float shotCooldownMultiplier;
    private float engineMultiplier;
    private boolean shielded;

    public PlayerShip(float x, float y) {
        super(x, y, RenderLayer.SHIPS);
        this.lives = 3;
        this.boundingBox = new Rectangle(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.shotCooldownMultiplier = 1f;
        this.engineMultiplier = 1f;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
        GameSession.getInstance().setCurrentLives(lives);
    }

    public void setBuff(PowerupEffect effect) {
        if (currentBuff != null) {
            currentBuff.remove(this);
        }
        currentBuff = effect;
        if (currentBuff != null) {
            currentBuff.apply(this);
        }
    }

    public boolean isShielded() {
        return shielded;
    }

    public void setShielded(boolean shielded) {
        this.shielded = shielded;
    }

    public float getShotCooldownMultiplier() {
        return shotCooldownMultiplier;
    }

    public void setShotCooldownMultiplier(float shotCooldownMultiplier) {
        this.shotCooldownMultiplier = shotCooldownMultiplier;
    }

    public float getEngineMultiplier() {
        return engineMultiplier;
    }

    public void setEngineMultiplier(float engineMultiplier) {
        this.engineMultiplier = engineMultiplier;
    }

    public void incrementLives(int amount) {
        setLives(lives + amount);
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
        if (!shielded) {
            setLives(lives - 1);
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
        x += speedX * engineMultiplier * delta;
        y += speedY * engineMultiplier * delta;
    }

    @Override
    public void shoot() {
        // Projectile spawning is intentionally deferred to BattleScreen orchestration.
    }
}
