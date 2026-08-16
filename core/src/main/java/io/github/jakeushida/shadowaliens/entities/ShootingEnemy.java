package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.contracts.Shooter;
import io.github.jakeushida.shadowaliens.managers.GameSession;

public class ShootingEnemy extends EnemyShip implements Shooter {
    /** Matches the wide {@code shootingEnemy} region in sprites.atlas. */
    public static final float WIDTH = 111f;
    public static final float HEIGHT = 56f;

    /** Used when a difficulty file omits {@code enemy.shooting.firingRate}. */
    public static final float DEFAULT_FIRING_RATE = 90f;

    /** Frames between shots, matching the units used by the .properties files. */
    private float firingRate = DEFAULT_FIRING_RATE;
    private float fireTimer = DEFAULT_FIRING_RATE;
    private boolean shotPending;

    public ShootingEnemy(float x, float y, int arrivalTime) {
        super(x, y, arrivalTime, WIDTH, HEIGHT);
        setSpeedY(-45f);
    }

    public float getFiringRate() {
        return firingRate;
    }

    /** Sets the gap between shots in frames; the first shot waits a full interval. */
    public void setFiringRate(float framesBetweenShots) {
        this.firingRate = Math.max(1f, framesBetweenShots);
        this.fireTimer = this.firingRate;
    }

    @Override
    public void update(float delta) {
        move(delta);

        fireTimer -= delta * GameSession.FRAMES_PER_SECOND;
        if (fireTimer <= 0f) {
            fireTimer += firingRate;
            shoot();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Sprite rendering is driven by BattleScreen using the shared atlas.
    }

    @Override
    public void move(float delta) {
        y += speedY * delta;
    }

    @Override
    public void shoot() {
        // Flags a shot rather than spawning one directly: the enemy has no
        // handle on the wave's projectile list, so BattleScreen collects it.
        shotPending = true;
    }

    /** True once per pending shot, clearing the flag so the shot fires only once. */
    public boolean consumePendingShot() {
        boolean pending = shotPending;
        shotPending = false;
        return pending;
    }
}
