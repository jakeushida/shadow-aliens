package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class StrafingEnemy extends EnemyShip {
    private float elapsed;

    public StrafingEnemy(float x, float y, int arrivalTime) {
        super(x, y, arrivalTime);
        setSpeedY(-60f);
        setSpeedX(50f);
    }

    @Override
    public void update(float delta) {
        elapsed += delta;
        move(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Sprite rendering will be wired once assets are integrated.
    }

    @Override
    public void move(float delta) {
        y += speedY * delta;
        x += MathUtils.sin(elapsed * 2f) * speedX * delta;
    }
}
