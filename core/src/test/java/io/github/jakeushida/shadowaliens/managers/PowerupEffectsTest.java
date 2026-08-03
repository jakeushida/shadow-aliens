package io.github.jakeushida.shadowaliens.managers;

import io.github.jakeushida.shadowaliens.entities.PlayerShip;
import io.github.jakeushida.shadowaliens.powerups.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PowerupEffectsTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testShieldEffectApplyRemove() {
        PlayerShip player = new PlayerShip(0f,0f);
        ShieldEffect s = new ShieldEffect();
        s.apply(player);
        assertTrue(player.isShielded());
        s.remove(player);
        assertFalse(player.isShielded());
    }

    @Test
    public void testLifeEffectApply() {
        PlayerShip player = new PlayerShip(0f,0f);
        int before = player.getLives();
        LifeEffect l = new LifeEffect();
        l.apply(player);
        assertEquals(before + 1, player.getLives());
    }

    @Test
    public void testCooldownEffectApplyRemove() {
        PlayerShip player = new PlayerShip(0f,0f);
        CooldownEffect c = new CooldownEffect();
        c.apply(player);
        assertEquals(0.5f, player.getShotCooldownMultiplier());
        c.remove(player);
        assertEquals(1f, player.getShotCooldownMultiplier());
    }

    @Test
    public void testEngineEffectApplyRemove() {
        PlayerShip player = new PlayerShip(0f,0f);
        EngineEffect e = new EngineEffect();
        e.apply(player);
        assertEquals(1.5f, player.getEngineMultiplier());
        e.remove(player);
        assertEquals(1f, player.getEngineMultiplier());
    }
}
