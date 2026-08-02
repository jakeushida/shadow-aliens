package io.github.jakeushida.shadowaliens.powerups;

import io.github.jakeushida.shadowaliens.entities.PlayerShip;

public class ShieldEffect implements PowerupEffect {
    @Override
    public void apply(PlayerShip player) {
        player.setShielded(true);
    }

    @Override
    public void remove(PlayerShip player) {
        player.setShielded(false);
    }
}
