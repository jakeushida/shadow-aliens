package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.contracts.Shooter;

public class ShootingEnemy extends EnemyShip implements Shooter {
    public ShootingEnemy(float x, float y, int arrivalTime) {
        super(x, y, arrivalTime);
        setSpeedY(-45f);
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

    @Override
    public void shoot() {
        // Enemy projectile creation is handled by wave/battle orchestration.
    }
}
