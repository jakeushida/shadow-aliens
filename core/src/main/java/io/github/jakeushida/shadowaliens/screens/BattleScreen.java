package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.contracts.Collidable;
import io.github.jakeushida.shadowaliens.entities.*;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.powerups.*;
import io.github.jakeushida.shadowaliens.waves.Wave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BattleScreen implements Screen {
    private final Main game;
    private final Wave wave;
    private final PlayerShip playerShip;
    private final List<PlayerProjectile> playerProjectiles;
    private final List<Explosion> explosions;
    private final TextureAtlas atlas;
    private final BitmapFont font;

    private float elapsedTime;
    private float shootCooldown;
    private float playerHitInvincibility;
    private int currentWaveNumber;
    private boolean waveCompleted;
    private int score;

    public BattleScreen(Main game) {
        this.game = game;
        this.wave = new Wave();
        this.atlas = game.atlas;
        this.font = game.font;

        // Initialize player at configured position
        float playerY = Float.parseFloat(ConfigManager.getInstance().getString("player.posY"));
        this.playerShip = new PlayerShip(Gdx.graphics.getWidth() / 2f, playerY);
        this.playerProjectiles = new ArrayList<>();
        this.explosions = new ArrayList<>();

        this.elapsedTime = 0f;
        this.shootCooldown = 0f;
        this.playerHitInvincibility = 0f;
        this.currentWaveNumber = 1;
        this.waveCompleted = false;
        this.score = 0;

        // Initialize player lives from config
        int initialLives = ConfigManager.getInstance().getInt("player.initialLives");
        playerShip.setLives(initialLives);
    }

    public Wave getWave() {
        return wave;
    }

    public PlayerShip getPlayerShip() {
        return playerShip;
    }

    @Override
    public void show() {
        loadWave(currentWaveNumber);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float scaledDelta = delta * GameSession.getInstance().getTimeScale();

        // Handle input
        handleInput(scaledDelta);

        // Update timers
        elapsedTime += scaledDelta * 60f; // Convert to frame-based timing
        if (shootCooldown > 0) {
            shootCooldown -= scaledDelta * 60f;
        }
        if (playerHitInvincibility > 0) {
            playerHitInvincibility -= scaledDelta * 60f;
        }

        // Spawn wave entities based on arrival time
        spawnWaveEntities();

        // Update entities
        playerShip.update(scaledDelta);
        wave.update(scaledDelta);

        for (PlayerProjectile projectile : playerProjectiles) {
            projectile.update(scaledDelta);
        }

        for (Explosion explosion : explosions) {
            explosion.update(scaledDelta);
        }

        // Check collisions
        checkCollisions();

        // Remove off-screen entities
        removeOffScreenEntities();

        // Remove finished explosions
        explosions.removeIf(Explosion::isFinished);

        // Check wave completion
        if (wave.isComplete() && !waveCompleted) {
            waveCompleted = true;
            score += ConfigManager.getInstance().getInt("score.waveCompleted");
            currentWaveNumber++;
            // Note: In a full implementation, you'd load the next wave or transition to EndScreen
        }

        // Check game over
        if (playerShip.getLives() <= 0) {
            game.setScreen(new EndScreen(game, score, false));
            return;
        }

        // Render everything
        game.batch.begin();

        // Draw player ship
        game.batch.draw(atlas.findRegion("playerSpaceship"), playerShip.getX(), playerShip.getY());

        // Draw enemies
        for (EnemyShip enemy : wave.getEnemies()) {
            String spriteName;
            if (enemy instanceof RegularEnemy) {
                spriteName = "regularEnemy";
            } else if (enemy instanceof StrafingEnemy) {
                spriteName = "strafingEnemy";
            } else if (enemy instanceof ShootingEnemy) {
                spriteName = "shootingEnemy";
            } else {
                spriteName = "enemy";
            }
            game.batch.draw(atlas.findRegion(spriteName), enemy.getX(), enemy.getY());
        }

        // Draw projectiles
        for (PlayerProjectile projectile : playerProjectiles) {
            game.batch.draw(atlas.findRegion("playerProjectile"), projectile.getX(), projectile.getY());
        }
        for (EnemyProjectile projectile : wave.getEnemyProjectiles()) {
            game.batch.draw(atlas.findRegion("enemyProjectile"), projectile.getX(), projectile.getY());
        }

        // Draw powerups
        for (PowerupEntity powerup : wave.getPowerups()) {
            String powerupSprite;
            PowerupEffect effect = powerup.getEffect();
            if (effect instanceof ShieldEffect) {
                powerupSprite = "shieldPowerup";
            } else if (effect instanceof LifeEffect) {
                powerupSprite = "lifePowerup";
            } else if (effect instanceof CooldownEffect) {
                powerupSprite = "cooldownPowerup";
            } else if (effect instanceof EngineEffect) {
                powerupSprite = "enginePowerup";
            } else {
                powerupSprite = "shieldPowerup";
            }
            game.batch.draw(atlas.findRegion(powerupSprite), powerup.getX(), powerup.getY());
        }

        // Draw explosions
        for (Explosion explosion : explosions) {
            game.batch.draw(atlas.findRegion("explosionSmall"), explosion.getX(), explosion.getY());
        }

        // Draw UI
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "SCORE: " + score, 10, Gdx.graphics.getHeight() - 10);
        font.draw(game.batch, "WAVE: " + currentWaveNumber, 10, Gdx.graphics.getHeight() - 40);
        font.draw(game.batch, "LIVES: " + playerShip.getLives(), 10, Gdx.graphics.getHeight() - 70);

        game.batch.end();
    }

    private void handleInput(float delta) {
        float playerSpeed = ConfigManager.getInstance().getInt("player.speed") * 60f; // Convert to per-second speed

        // Movement
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerShip.setSpeedX(-playerSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerShip.setSpeedX(playerSpeed);
        } else {
            playerShip.setSpeedX(0);
        }

        // Keep player on screen
        if (playerShip.getX() < 0) {
            playerShip.setSpeedX(0);
        }
        if (playerShip.getX() > Gdx.graphics.getWidth() - 48) {
            playerShip.setSpeedX(0);
        }

        // Shooting
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shootCooldown <= 0) {
            playerShip.shoot();
            float projectileX = playerShip.getX() + 24 - 5; // Center of ship minus half projectile width
            float projectileY = playerShip.getY() + 32; // Top of ship
            playerProjectiles.add(new PlayerProjectile(projectileX, projectileY));

            float baseCooldown = ConfigManager.getInstance().getInt("player.shootCooldown");
            shootCooldown = baseCooldown * playerShip.getShotCooldownMultiplier();
        }

        // Pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
        }

        // Cheat codes
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            boolean invincible = !GameSession.getInstance().isInvincibilityMode();
            GameSession.getInstance().setInvincibilityMode(invincible);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.G)) {
            float timeScale = GameSession.getInstance().getTimeScale();
            GameSession.getInstance().setTimeScale(Math.min(3f, timeScale + delta));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            float timeScale = GameSession.getInstance().getTimeScale();
            GameSession.getInstance().setTimeScale(Math.max(0.1f, timeScale - delta));
        }
    }

    private void spawnWaveEntities() {
        ConfigManager config = ConfigManager.getInstance();
        int waveNum = currentWaveNumber;

        // Spawn enemies
        int enemyIndex = 0;
        while (true) {
            String typeKey = "wave." + waveNum + ".enemy." + enemyIndex + ".type";
            try {
                String type = config.getString(typeKey);
                int arrivalTime = config.getInt("wave." + waveNum + ".enemy." + enemyIndex + ".arrivalTime");
                float posX = Float.parseFloat(config.getString("wave." + waveNum + ".enemy." + enemyIndex + ".posX"));

                // Check if it's time to spawn
                if (elapsedTime >= arrivalTime && !isEnemySpawned(posX, arrivalTime)) {
                    EnemyShip enemy = createEnemy(type, posX, arrivalTime);
                    wave.getEnemies().add(enemy);
                }
                enemyIndex++;
            } catch (IllegalArgumentException e) {
                break; // No more enemies for this wave
            }
        }

        // Spawn powerups
        int powerupIndex = 0;
        while (true) {
            String typeKey = "wave." + waveNum + ".powerup." + powerupIndex + ".type";
            try {
                String type = config.getString(typeKey);
                int arrivalTime = config.getInt("wave." + waveNum + ".powerup." + powerupIndex + ".arrivalTime");
                float posX = Float.parseFloat(config.getString("wave." + waveNum + ".powerup." + powerupIndex + ".posX"));

                // Check if it's time to spawn
                if (elapsedTime >= arrivalTime && !isPowerupSpawned(posX, arrivalTime)) {
                    PowerupEffect effect = createPowerupEffect(type);
                    PowerupEntity powerup = new PowerupEntity(posX, Gdx.graphics.getHeight(), effect);
                    wave.getPowerups().add(powerup);
                }
                powerupIndex++;
            } catch (IllegalArgumentException e) {
                break; // No more powerups for this wave
            }
        }
    }

    private boolean isEnemySpawned(float posX, int arrivalTime) {
        for (EnemyShip enemy : wave.getEnemies()) {
            if (Math.abs(enemy.getX() - posX) < 1 && enemy.getArrivalTime() == arrivalTime) {
                return true;
            }
        }
        return false;
    }

    private boolean isPowerupSpawned(float posX, int arrivalTime) {
        // Simple check: if powerup exists at this position (approximately)
        for (PowerupEntity powerup : wave.getPowerups()) {
            if (Math.abs(powerup.getX() - posX) < 1) {
                return true;
            }
        }
        return false;
    }

    private EnemyShip createEnemy(String type, float x, int arrivalTime) {
        float y = Gdx.graphics.getHeight();
        switch (type) {
            case "regular":
                return new RegularEnemy(x, y, arrivalTime);
            case "strafing":
                return new StrafingEnemy(x, y, arrivalTime);
            case "shooting":
                return new ShootingEnemy(x, y, arrivalTime);
            default:
                return new RegularEnemy(x, y, arrivalTime);
        }
    }

    private PowerupEffect createPowerupEffect(String type) {
        switch (type) {
            case "shield":
                return new ShieldEffect();
            case "life":
                return new LifeEffect();
            case "cooldown":
                return new CooldownEffect();
            case "engine":
                return new EngineEffect();
            default:
                return new ShieldEffect();
        }
    }

    private void checkCollisions() {
        // Player projectiles vs enemies
        Iterator<PlayerProjectile> projIter = playerProjectiles.iterator();
        while (projIter.hasNext()) {
            PlayerProjectile projectile = projIter.next();
            Rectangle projBox = projectile.getBoundingBox();

            Iterator<EnemyShip> enemyIter = wave.getEnemies().iterator();
            while (enemyIter.hasNext()) {
                EnemyShip enemy = enemyIter.next();
                Rectangle enemyBox = enemy.getBoundingBox();

                if (projBox.overlaps(enemyBox)) {
                    // Hit!
                    projIter.remove();
                    enemyIter.remove();
                    explosions.add(new Explosion(enemy.getX(), enemy.getY(), 30));

                    // Award score
                    String enemyType = enemy.getClass().getSimpleName();
                    if (enemyType.contains("Regular")) {
                        score += ConfigManager.getInstance().getInt("score.destroyedEnemy.regular");
                    } else if (enemyType.contains("Strafing")) {
                        score += ConfigManager.getInstance().getInt("score.destroyedEnemy.strafing");
                    } else if (enemyType.contains("Shooting")) {
                        score += ConfigManager.getInstance().getInt("score.destroyedEnemy.shooting");
                    }

                    score += ConfigManager.getInstance().getInt("score.hitProjectile");
                    break;
                }
            }
        }

        // Enemy projectiles vs player
        if (playerHitInvincibility <= 0 && !GameSession.getInstance().isInvincibilityMode()) {
            Iterator<EnemyProjectile> enemyProjIter = wave.getEnemyProjectiles().iterator();
            while (enemyProjIter.hasNext()) {
                EnemyProjectile projectile = enemyProjIter.next();
                Rectangle projBox = projectile.getBoundingBox();
                Rectangle playerBox = playerShip.getBoundingBox();

                if (projBox.overlaps(playerBox)) {
                    enemyProjIter.remove();
                    if (!playerShip.isShielded()) {
                        playerShip.setLives(playerShip.getLives() - 1);
                        playerHitInvincibility = ConfigManager.getInstance().getInt("player.hitInvincibilityTime");
                        score -= ConfigManager.getInstance().getInt("score.gotHit");
                        if (score < 0) score = 0;
                    }
                    break;
                }
            }
        }

        // Enemies vs player
        if (playerHitInvincibility <= 0 && !GameSession.getInstance().isInvincibilityMode()) {
            Iterator<EnemyShip> enemyIter = wave.getEnemies().iterator();
            while (enemyIter.hasNext()) {
                EnemyShip enemy = enemyIter.next();
                Rectangle enemyBox = enemy.getBoundingBox();
                Rectangle playerBox = playerShip.getBoundingBox();

                if (enemyBox.overlaps(playerBox)) {
                    enemyIter.remove();
                    explosions.add(new Explosion(enemy.getX(), enemy.getY(), 30));

                    if (!playerShip.isShielded()) {
                        playerShip.setLives(playerShip.getLives() - 1);
                        playerHitInvincibility = ConfigManager.getInstance().getInt("player.hitInvincibilityTime");
                        score -= ConfigManager.getInstance().getInt("score.gotHit");
                        if (score < 0) score = 0;
                    }
                    break;
                }
            }
        }

        // Powerups vs player
        Iterator<PowerupEntity> powerupIter = wave.getPowerups().iterator();
        while (powerupIter.hasNext()) {
            PowerupEntity powerup = powerupIter.next();
            Rectangle powerupBox = powerup.getBoundingBox();
            Rectangle playerBox = playerShip.getBoundingBox();

            if (powerupBox.overlaps(playerBox)) {
                powerup.onCollision(playerShip);
                powerupIter.remove();
                score += ConfigManager.getInstance().getInt("score.gotPowerup");
                break;
            }
        }
    }

    private void removeOffScreenEntities() {
        // Remove player projectiles that went off-screen
        playerProjectiles.removeIf(p -> p.getY() > Gdx.graphics.getHeight());

        // Remove enemy projectiles that went off-screen
        wave.getEnemyProjectiles().removeIf(p -> p.getY() < 0);

        // Remove enemies that went off-screen
        wave.getEnemies().removeIf(e -> e.getY() < -50);

        // Remove powerups that went off-screen
        wave.getPowerups().removeIf(p -> p.getY() < 0);
    }

    private void loadWave(int waveNumber) {
        wave.getEnemies().clear();
        wave.getPowerups().clear();
        wave.getEnemyProjectiles().clear();
        elapsedTime = 0f;
        waveCompleted = false;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
