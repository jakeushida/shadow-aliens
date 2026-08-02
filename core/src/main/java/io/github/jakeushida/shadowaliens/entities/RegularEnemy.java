package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RegularEnemy extends EnemyShip {
    public RegularEnemy(float x, float y, int arrivalTime) {
        super(x, y, arrivalTime);
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
