package io.github.jakeushida.shadowaliens.powerups;

import io.github.jakeushida.shadowaliens.entities.PlayerShip;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PowerupEffectsDetailedTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    // ShieldEffect Tests
    @Test
    public void testShieldEffectImplementsInterface() {
        ShieldEffect effect = new ShieldEffect();
        assertTrue(effect instanceof PowerupEffect);
    }

    @Test
    public void testShieldEffectApply() {
        PlayerShip player = new PlayerShip(100f, 100f);
        ShieldEffect effect = new ShieldEffect();

        assertFalse(player.isShielded());
        effect.apply(player);
        assertTrue(player.isShielded());
    }

    @Test
    public void testShieldEffectRemove() {
        PlayerShip player = new PlayerShip(100f, 100f);
        ShieldEffect effect = new ShieldEffect();

        effect.apply(player);
        assertTrue(player.isShielded());

        effect.remove(player);
        assertFalse(player.isShielded());
    }

    @Test
    public void testShieldEffectMultipleApplications() {
        PlayerShip player = new PlayerShip(100f, 100f);
        ShieldEffect effect = new ShieldEffect();

        effect.apply(player);
        effect.apply(player);
        assertTrue(player.isShielded());

        effect.remove(player);
        assertFalse(player.isShielded());
    }

    // LifeEffect Tests
    @Test
    public void testLifeEffectImplementsInterface() {
        LifeEffect effect = new LifeEffect();
        assertTrue(effect instanceof PowerupEffect);
    }

    @Test
    public void testLifeEffectApplyIncreasesLives() {
        PlayerShip player = new PlayerShip(100f, 100f);
        LifeEffect effect = new LifeEffect();

        int initialLives = player.getLives();
        effect.apply(player);
        assertEquals(initialLives + 1, player.getLives());
    }

    @Test
    public void testLifeEffectRemoveDoesNothing() {
        PlayerShip player = new PlayerShip(100f, 100f);
        LifeEffect effect = new LifeEffect();

        effect.apply(player);
        int livesAfterApply = player.getLives();

        effect.remove(player);
        assertEquals(livesAfterApply, player.getLives());
    }

    @Test
    public void testLifeEffectMultipleApplications() {
        PlayerShip player = new PlayerShip(100f, 100f);
        LifeEffect effect = new LifeEffect();

        int initialLives = player.getLives();
        effect.apply(player);
        effect.apply(player);
        effect.apply(player);

        assertEquals(initialLives + 3, player.getLives());
    }

    @Test
    public void testLifeEffectUpdatesGameSession() {
        PlayerShip player = new PlayerShip(100f, 100f);
        LifeEffect effect = new LifeEffect();

        int initialLives = GameSession.getInstance().getCurrentLives();
        effect.apply(player);

        assertEquals(initialLives + 1, GameSession.getInstance().getCurrentLives());
    }

    // CooldownEffect Tests
    @Test
    public void testCooldownEffectImplementsInterface() {
        CooldownEffect effect = new CooldownEffect();
        assertTrue(effect instanceof PowerupEffect);
    }

    @Test
    public void testCooldownEffectApplyReducesCooldown() {
        PlayerShip player = new PlayerShip(100f, 100f);
        CooldownEffect effect = new CooldownEffect();

        assertEquals(1f, player.getShotCooldownMultiplier());
        effect.apply(player);
        assertEquals(0.5f, player.getShotCooldownMultiplier());
    }

    @Test
    public void testCooldownEffectRemoveRestoresCooldown() {
        PlayerShip player = new PlayerShip(100f, 100f);
        CooldownEffect effect = new CooldownEffect();

        effect.apply(player);
        assertEquals(0.5f, player.getShotCooldownMultiplier());

        effect.remove(player);
        assertEquals(1f, player.getShotCooldownMultiplier());
    }

    @Test
    public void testCooldownEffectValueIsHalved() {
        PlayerShip player = new PlayerShip(100f, 100f);
        CooldownEffect effect = new CooldownEffect();

        effect.apply(player);
        assertTrue(player.getShotCooldownMultiplier() < 1f);
        assertEquals(0.5f, player.getShotCooldownMultiplier(), 0.001f);
    }

    // EngineEffect Tests
    @Test
    public void testEngineEffectImplementsInterface() {
        EngineEffect effect = new EngineEffect();
        assertTrue(effect instanceof PowerupEffect);
    }

    @Test
    public void testEngineEffectApplyIncreasesSpeed() {
        PlayerShip player = new PlayerShip(100f, 100f);
        EngineEffect effect = new EngineEffect();

        assertEquals(1f, player.getEngineMultiplier());
        effect.apply(player);
        assertEquals(1.5f, player.getEngineMultiplier());
    }

    @Test
    public void testEngineEffectRemoveRestoresSpeed() {
        PlayerShip player = new PlayerShip(100f, 100f);
        EngineEffect effect = new EngineEffect();

        effect.apply(player);
        assertEquals(1.5f, player.getEngineMultiplier());

        effect.remove(player);
        assertEquals(1f, player.getEngineMultiplier());
    }

    @Test
    public void testEngineEffectValueIsOneAndHalf() {
        PlayerShip player = new PlayerShip(100f, 100f);
        EngineEffect effect = new EngineEffect();

        effect.apply(player);
        assertTrue(player.getEngineMultiplier() > 1f);
        assertEquals(1.5f, player.getEngineMultiplier(), 0.001f);
    }

    @Test
    public void testEngineEffectAffectsMovement() {
        PlayerShip player = new PlayerShip(100f, 100f);
        EngineEffect effect = new EngineEffect();

        player.setSpeedX(100f);

        // Move without boost
        player.move(1.0f);
        float normalDistance = player.getX() - 100f;

        // Reset
        player = new PlayerShip(100f, 100f);
        player.setSpeedX(100f);

        // Move with boost
        effect.apply(player);
        player.move(1.0f);
        float boostedDistance = player.getX() - 100f;

        assertTrue(boostedDistance > normalDistance);
        assertEquals(normalDistance * 1.5f, boostedDistance, 0.001f);
    }

    // Cross-effect tests
    @Test
    public void testDifferentEffectsHaveDifferentBehaviors() {
        PlayerShip player = new PlayerShip(100f, 100f);

        ShieldEffect shield = new ShieldEffect();
        LifeEffect life = new LifeEffect();
        CooldownEffect cooldown = new CooldownEffect();
        EngineEffect engine = new EngineEffect();

        // Each effect should modify different properties
        assertFalse(player.isShielded());
        int initialLives = player.getLives();
        assertEquals(1f, player.getShotCooldownMultiplier());
        assertEquals(1f, player.getEngineMultiplier());

        shield.apply(player);
        assertTrue(player.isShielded());

        life.apply(player);
        assertEquals(initialLives + 1, player.getLives());

        cooldown.apply(player);
        assertEquals(0.5f, player.getShotCooldownMultiplier());

        engine.apply(player);
        assertEquals(1.5f, player.getEngineMultiplier());
    }

    @Test
    public void testAllEffectsCanBeRemoved() {
        PlayerShip player = new PlayerShip(100f, 100f);

        ShieldEffect shield = new ShieldEffect();
        CooldownEffect cooldown = new CooldownEffect();
        EngineEffect engine = new EngineEffect();

        shield.apply(player);
        cooldown.apply(player);
        engine.apply(player);

        shield.remove(player);
        assertFalse(player.isShielded());

        cooldown.remove(player);
        assertEquals(1f, player.getShotCooldownMultiplier());

        engine.remove(player);
        assertEquals(1f, player.getEngineMultiplier());
    }

    @Test
    public void testPowerupEffectInterfaceContract() {
        PowerupEffect[] effects = {
            new ShieldEffect(),
            new LifeEffect(),
            new CooldownEffect(),
            new EngineEffect()
        };

        PlayerShip player = new PlayerShip(100f, 100f);

        // All effects should have apply and remove methods
        for (PowerupEffect effect : effects) {
            assertDoesNotThrow(() -> effect.apply(player));
            assertDoesNotThrow(() -> effect.remove(player));
        }
    }
}
