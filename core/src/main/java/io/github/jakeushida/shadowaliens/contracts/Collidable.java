package io.github.jakeushida.shadowaliens.contracts;

import com.badlogic.gdx.math.Rectangle;

public interface Collidable {
    Rectangle getBoundingBox();

    void onCollision(Collidable other);
}
