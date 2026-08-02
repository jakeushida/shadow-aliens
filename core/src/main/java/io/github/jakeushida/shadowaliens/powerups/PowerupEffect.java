package io.github.jakeushida.shadowaliens.powerups;

import io.github.jakeushida.shadowaliens.entities.PlayerShip;

public interface PowerupEffect {
    void apply(PlayerShip player);

    void remove(PlayerShip player);
}
