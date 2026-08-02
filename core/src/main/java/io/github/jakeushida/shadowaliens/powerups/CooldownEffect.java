package io.github.jakeushida.shadowaliens.powerups;

import io.github.jakeushida.shadowaliens.entities.PlayerShip;

public class CooldownEffect implements PowerupEffect {
    @Override
    public void apply(PlayerShip player) {
        player.setShotCooldownMultiplier(0.5f);
    }

    @Override
    public void remove(PlayerShip player) {
        player.setShotCooldownMultiplier(1f);
    }
}
