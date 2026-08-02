package io.github.jakeushida.shadowaliens.powerups;

import io.github.jakeushida.shadowaliens.entities.PlayerShip;

public class EngineEffect implements PowerupEffect {
    @Override
    public void apply(PlayerShip player) {
        player.setEngineMultiplier(1.5f);
    }

    @Override
    public void remove(PlayerShip player) {
        player.setEngineMultiplier(1f);
    }
}
