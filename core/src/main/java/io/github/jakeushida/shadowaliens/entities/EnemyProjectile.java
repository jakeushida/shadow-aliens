package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EnemyProjectile extends Projectile {
    /** Matches the {@code enemyProjectile} region in sprites.atlas. */
    public static final float WIDTH = 30f;
    public static final float HEIGHT = 30f;

    public EnemyProjectile(float x, float y) {
        super(x, y, WIDTH, HEIGHT);
        setSpeedY(-220f);
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
    public void move(float delta) {
        y += speedY * delta;
    }
}
