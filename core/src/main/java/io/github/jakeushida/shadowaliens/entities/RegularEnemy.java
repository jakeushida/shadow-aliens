package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RegularEnemy extends EnemyShip {
    /** Matches the {@code regularEnemy} region in sprites.atlas. */
    public static final float WIDTH = 56f;
    public static final float HEIGHT = 56f;

    public RegularEnemy(float x, float y, int arrivalTime) {
        super(x, y, arrivalTime, WIDTH, HEIGHT);
        setSpeedY(-80f);
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
