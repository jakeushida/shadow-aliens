package io.github.jakeushida.shadowaliens.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectileAndExplosionTest {

    @BeforeEach
    public void setUp() {
        io.github.jakeushida.shadowaliens.managers.GameSession.getInstance().reset();
    }

    @Test
    public void testPlayerProjectileMovement() {
        PlayerProjectile p = new PlayerProjectile(10f, 10f);
        float beforeY = p.getY();
        p.update(1.0f);
        // PlayerProjectile speedY = 250f
        assertEquals(beforeY + 250f, p.getY(), 0.001f);
    }

    @Test
    public void testEnemyProjectileMovement() {
        EnemyProjectile p = new EnemyProjectile(10f, 100f);
        float beforeY = p.getY();
        p.update(0.5f);
        // EnemyProjectile speedY = -220f, so after 0.5s y decreases by 110
        assertEquals(beforeY - 110f, p.getY(), 0.001f);
    }

    @Test
    public void testExplosionLifecycle() {
        Explosion ex = new Explosion(0f,0f,1); // duration = 1
        assertFalse(ex.isFinished());
        ex.update(0.5f);
        assertFalse(ex.isFinished());
        ex.update(0.6f);
        assertTrue(ex.isFinished());
    }
}
