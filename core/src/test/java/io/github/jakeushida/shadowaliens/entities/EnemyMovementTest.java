package io.github.jakeushida.shadowaliens.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyMovementTest {

    @BeforeEach
    public void setUp() {
        // Ensure GameSession default state
        io.github.jakeushida.shadowaliens.managers.GameSession.getInstance().reset();
    }

    @Test
    public void testRegularEnemyMovement() {
        RegularEnemy enemy = new RegularEnemy(100f, 200f, 0);
        float beforeY = enemy.getY();
        enemy.update(1.0f); // 1 second
        // RegularEnemy sets speedY to -80f in constructor
        assertEquals(beforeY - 80f, enemy.getY(), 0.0001f);
    }

    @Test
    public void testStrafingEnemyMovement() {
        StrafingEnemy enemy = new StrafingEnemy(100f, 200f, 0);
        float beforeX = enemy.getX();
        float beforeY = enemy.getY();
        enemy.update(0.5f);
        // strafing enemy should move in Y and change X slightly due to sine movement
        assertNotEquals(beforeY, enemy.getY());
        assertNotEquals(beforeX, enemy.getX());
    }

    @Test
    public void testShootingEnemyMovement() {
        ShootingEnemy enemy = new ShootingEnemy(50f, 150f, 0);
        float beforeY = enemy.getY();
        enemy.update(2.0f);
        // speedY set to -45f, so after 2 seconds y decreases by 90
        assertEquals(beforeY - 90f, enemy.getY(), 0.001f);
    }
}
