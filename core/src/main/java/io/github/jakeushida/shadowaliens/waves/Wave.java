package io.github.jakeushida.shadowaliens.waves;

import io.github.jakeushida.shadowaliens.entities.EnemyProjectile;
import io.github.jakeushida.shadowaliens.entities.EnemyShip;
import io.github.jakeushida.shadowaliens.entities.PowerupEntity;
import java.util.ArrayList;
import java.util.List;

public class Wave {
    private final List<EnemyShip> enemies;
    private final List<PowerupEntity> powerups;
    private final List<EnemyProjectile> enemyProjectiles;

    public Wave() {
        this.enemies = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.enemyProjectiles = new ArrayList<>();
    }

    public List<EnemyShip> getEnemies() {
        return enemies;
    }

    public List<PowerupEntity> getPowerups() {
        return powerups;
    }

    public List<EnemyProjectile> getEnemyProjectiles() {
        return enemyProjectiles;
    }

    public void update(float delta) {
        for (EnemyShip enemy : enemies) {
            enemy.update(delta);
        }
        for (PowerupEntity powerup : powerups) {
            powerup.update(delta);
        }
        for (EnemyProjectile projectile : enemyProjectiles) {
            projectile.update(delta);
        }
    }

    public boolean isComplete() {
        return enemies.isEmpty();
    }
}
