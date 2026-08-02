package io.github.jakeushida.shadowaliens.powerups;

import io.github.jakeushida.shadowaliens.entities.PlayerShip;

public class LifeEffect implements PowerupEffect {
    @Override
    public void apply(PlayerShip player) {
        player.incrementLives(1);
    }

    @Override
    public void remove(PlayerShip player) {
        // Life gain is immediate and does not require rollback.
    }
}
