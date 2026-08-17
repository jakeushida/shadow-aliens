package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.entities.*;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.powerups.*;
import io.github.jakeushida.shadowaliens.rendering.TextRenderer;
import io.github.jakeushida.shadowaliens.waves.Wave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BattleScreen extends BaseScreen {
    private static final float EXPLOSION_SECONDS = 0.45f;
    private static final float HUD_MARGIN = 10f;

    // TextureAtlas.findRegion does a linear scan of every region, so the lookups
    // are done once here instead of dozens of times per frame while drawing.
    private final TextureRegion playerRegion;
    private final TextureRegion shieldRegion;
    private final TextureRegion regularEnemyRegion;
    private final TextureRegion strafingEnemyRegion;
    private final TextureRegion shootingEnemyRegion;
    private final TextureRegion playerBoltRegion;
    private final TextureRegion enemyBoltRegion;
    private final TextureRegion explosionRegion;
    private final TextureRegion shieldPowerupRegion;
    private final TextureRegion lifePowerupRegion;
    private final TextureRegion cooldownPowerupRegion;
    private final TextureRegion enginePowerupRegion;

    private final Wave wave;
    private final PlayerShip playerShip;
    private final LivesDisplay livesDisplay;
    private final List<PlayerProjectile> playerProjectiles;
    private final List<Explosion> explosions;

    /** Rows for the current wave, read from the config once when the wave loads. */
    private final List<SpawnDefinition> enemySpawns;
    private final List<SpawnDefinition> powerupSpawns;

    private float elapsedFrames;
    private float shootCooldown;
    private float playerHitInvincibility;
    private int currentWaveNumber;
    private int score;
    private boolean waveLoaded;

    public BattleScreen(Main game) {
        super(game);
        TextureAtlas atlas = game.atlas;
        this.playerRegion = atlas.findRegion("playerSpaceship");
        this.shieldRegion = atlas.findRegion("invincible");
        this.regularEnemyRegion = atlas.findRegion("regularEnemy");
        this.strafingEnemyRegion = atlas.findRegion("strafingEnemy");
        this.shootingEnemyRegion = atlas.findRegion("shootingEnemy");
        this.playerBoltRegion = atlas.findRegion("playerProjectile");
        this.enemyBoltRegion = atlas.findRegion("enemyProjectile");
        this.explosionRegion = atlas.findRegion("explosionLarge");
        this.shieldPowerupRegion = atlas.findRegion("shieldPowerup");
        this.lifePowerupRegion = atlas.findRegion("lifePowerup");
        this.cooldownPowerupRegion = atlas.findRegion("cooldownPowerup");
        this.enginePowerupRegion = atlas.findRegion("enginePowerup");

        this.wave = new Wave();
        this.playerProjectiles = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.enemySpawns = new ArrayList<>();
        this.powerupSpawns = new ArrayList<>();
        this.currentWaveNumber = 1;

        ConfigManager config = ConfigManager.getInstance();
        this.playerShip = new PlayerShip(
            (worldWidth() - PlayerShip.DEFAULT_WIDTH) / 2f,
            config.getFloat("player.posY"));
        this.playerShip.setLives(config.getInt("player.initialLives"));

        this.livesDisplay = new LivesDisplay(HUD_MARGIN, HUD_MARGIN, game.heartRegion, 6f);
    }

    public Wave getWave() {
        return wave;
    }

    public PlayerShip getPlayerShip() {
        return playerShip;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentWaveNumber() {
        return currentWaveNumber;
    }

    @Override
    public void show() {
        // Game.setScreen calls show() again when PauseScreen hands control back,
        // so guard the load: it used to clear every enemy and reset the timer,
        // wiping the wave in progress every time the player paused.
        if (!waveLoaded) {
            loadWave(currentWaveNumber);
        }
    }

    @Override
    public void render(float delta) {
        // Skip the world update on the frame that opens the pause screen, but
        // always draw: returning early would leave the swap chain presenting a
        // stale back buffer and flicker on every screen transition.
        if (!handleInput(delta)) {
            updateWorld(delta * GameSession.getInstance().getTimeScale());
        }
        renderWorld();
    }

    // ---------------------------------------------------------------- input

    /** Returns true when the input handling swapped in a different screen. */
    private boolean handleInput(float delta) {
        ConfigManager config = ConfigManager.getInstance();
        float playerSpeed = config.getFloat("player.speed") * GameSession.FRAMES_PER_SECOND;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerShip.setSpeedX(-playerSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerShip.setSpeedX(playerSpeed);
        } else {
            playerShip.setSpeedX(0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shootCooldown <= 0f) {
            playerShip.shoot();
            // Spawn at the nose of the ship so the bolt emerges rather than
            // appearing on top of the sprite.
            playerProjectiles.add(new PlayerProjectile(
                playerShip.getCentreX() - PlayerProjectile.WIDTH / 2f,
                playerShip.getY() + playerShip.getHeight() - 10f));
            shootCooldown = config.getFloat("player.shootCooldown") * playerShip.getShotCooldownMultiplier();
        }

        // Cheat codes. Speed keys use the unscaled delta so the time scale
        // cannot make itself harder or easier to adjust.
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            GameSession session = GameSession.getInstance();
            session.setInvincibilityMode(!session.isInvincibilityMode());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.G)) {
            GameSession session = GameSession.getInstance();
            session.setTimeScale(Math.min(3f, session.getTimeScale() + delta));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            GameSession session = GameSession.getInstance();
            session.setTimeScale(Math.max(0.1f, session.getTimeScale() - delta));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new StartScreen(game));
            return true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return true;
        }
        return false;
    }

    // --------------------------------------------------------------- update

    /** Returns true when the update swapped in a different screen. */
    private boolean updateWorld(float delta) {
        elapsedFrames += delta * GameSession.FRAMES_PER_SECOND;
        shootCooldown = Math.max(0f, shootCooldown - delta * GameSession.FRAMES_PER_SECOND);
        playerHitInvincibility = Math.max(0f, playerHitInvincibility - delta * GameSession.FRAMES_PER_SECOND);

        spawnWaveEntities();

        playerShip.update(delta);
        playerShip.setX(MathUtils.clamp(playerShip.getX(), 0f, worldWidth() - playerShip.getWidth()));

        wave.update(delta);
        collectEnemyFire();

        for (PlayerProjectile projectile : playerProjectiles) {
            projectile.update(delta);
        }
        for (Explosion explosion : explosions) {
            explosion.update(delta);
        }

        checkCollisions();
        removeOffScreenEntities();
        explosions.removeIf(Explosion::isFinished);

        GameSession.getInstance().setCurrentScore(score);
        GameSession.getInstance().setCurrentWave(currentWaveNumber);

        if (playerShip.getLives() <= 0) {
            game.setScreen(new EndScreen(game, score, false));
            return true;
        }
        return advanceWaveIfCleared();
    }

    /**
     * Moves on once every enemy this wave defines has spawned and been cleared.
     * The old code only incremented a counter and never loaded the next wave, so
     * the game sat on an empty screen forever after wave one.
     */
    private boolean advanceWaveIfCleared() {
        for (SpawnDefinition definition : enemySpawns) {
            if (!definition.spawned) {
                return false;
            }
        }
        if (!wave.isComplete()) {
            return false;
        }

        score += ConfigManager.getInstance().getInt("score.waveCompleted");
        currentWaveNumber++;

        if (hasWave(currentWaveNumber)) {
            loadWave(currentWaveNumber);
            return false;
        }

        GameSession.getInstance().setCurrentScore(score);
        game.setScreen(new EndScreen(game, score, true));
        return true;
    }

    private void spawnWaveEntities() {
        for (SpawnDefinition definition : enemySpawns) {
            if (!definition.spawned && elapsedFrames >= definition.arrivalTime) {
                // Flagging the definition is what stops respawning. The old
                // check scanned the live enemy list instead, so a destroyed
                // enemy no longer "existed" and was spawned again every frame.
                definition.spawned = true;
                wave.getEnemies().add(createEnemy(definition));
            }
        }
        for (SpawnDefinition definition : powerupSpawns) {
            if (!definition.spawned && elapsedFrames >= definition.arrivalTime) {
                definition.spawned = true;
                wave.getPowerups().add(new PowerupEntity(
                    definition.posX - PowerupEntity.WIDTH / 2f,
                    worldHeight(),
                    createPowerupEffect(definition.type)));
            }
        }
    }

    /** Turns pending shots from shooting enemies into real projectiles. */
    private void collectEnemyFire() {
        for (EnemyShip enemy : wave.getEnemies()) {
            if (!(enemy instanceof ShootingEnemy)) {
                continue;
            }
            if (((ShootingEnemy) enemy).consumePendingShot()) {
                wave.getEnemyProjectiles().add(new EnemyProjectile(
                    enemy.getCentreX() - EnemyProjectile.WIDTH / 2f,
                    enemy.getY() - EnemyProjectile.HEIGHT));
            }
        }
    }

    private void checkCollisions() {
        ConfigManager config = ConfigManager.getInstance();

        // Player projectiles vs enemies
        Iterator<PlayerProjectile> projectileIterator = playerProjectiles.iterator();
        while (projectileIterator.hasNext()) {
            Rectangle projectileBox = projectileIterator.next().getBoundingBox();
            Iterator<EnemyShip> enemyIterator = wave.getEnemies().iterator();
            while (enemyIterator.hasNext()) {
                EnemyShip enemy = enemyIterator.next();
                if (!projectileBox.overlaps(enemy.getBoundingBox())) {
                    continue;
                }
                projectileIterator.remove();
                enemyIterator.remove();
                spawnExplosion(enemy);
                score += scoreForEnemy(enemy) + config.getInt("score.hitProjectile");
                break;
            }
        }

        boolean vulnerable = playerHitInvincibility <= 0f && !GameSession.getInstance().isInvincibilityMode();

        // Enemy projectiles vs player
        Iterator<EnemyProjectile> enemyProjectileIterator = wave.getEnemyProjectiles().iterator();
        while (enemyProjectileIterator.hasNext()) {
            if (!enemyProjectileIterator.next().getBoundingBox().overlaps(playerShip.getBoundingBox())) {
                continue;
            }
            enemyProjectileIterator.remove();
            if (vulnerable) {
                damagePlayer();
            }
            break;
        }

        // Enemies vs player
        Iterator<EnemyShip> enemyIterator = wave.getEnemies().iterator();
        while (enemyIterator.hasNext()) {
            EnemyShip enemy = enemyIterator.next();
            if (!enemy.getBoundingBox().overlaps(playerShip.getBoundingBox())) {
                continue;
            }
            enemyIterator.remove();
            spawnExplosion(enemy);
            if (vulnerable) {
                damagePlayer();
            }
            break;
        }

        // Powerups vs player
        Iterator<PowerupEntity> powerupIterator = wave.getPowerups().iterator();
        while (powerupIterator.hasNext()) {
            PowerupEntity powerup = powerupIterator.next();
            if (!powerup.getBoundingBox().overlaps(playerShip.getBoundingBox())) {
                continue;
            }
            powerup.onCollision(playerShip);
            powerupIterator.remove();
            score += config.getInt("score.gotPowerup");
            break;
        }
    }

    private void damagePlayer() {
        ConfigManager config = ConfigManager.getInstance();
        if (playerShip.isShielded()) {
            // A shield absorbs one hit and is then spent.
            playerShip.setBuff(null);
        } else {
            playerShip.setLives(playerShip.getLives() - 1);
            score = Math.max(0, score - config.getInt("score.gotHit"));
        }
        playerHitInvincibility = config.getFloat("player.hitInvincibilityTime");
    }

    private int scoreForEnemy(EnemyShip enemy) {
        ConfigManager config = ConfigManager.getInstance();
        if (enemy instanceof StrafingEnemy) {
            return config.getInt("score.destroyedEnemy.strafing");
        }
        if (enemy instanceof ShootingEnemy) {
            return config.getInt("score.destroyedEnemy.shooting");
        }
        return config.getInt("score.destroyedEnemy.regular");
    }

    private void spawnExplosion(GameEntity source) {
        explosions.add(new Explosion(
            source.getCentreX() - explosionRegion.getRegionWidth() / 2f,
            source.getCentreY() - explosionRegion.getRegionHeight() / 2f,
            EXPLOSION_SECONDS));
    }

    private void removeOffScreenEntities() {
        float top = worldHeight();
        playerProjectiles.removeIf(projectile -> projectile.getY() > top);
        wave.getEnemyProjectiles().removeIf(projectile -> projectile.getY() + projectile.getHeight() < 0f);
        wave.getEnemies().removeIf(enemy -> enemy.getY() + enemy.getHeight() < 0f);
        wave.getPowerups().removeIf(powerup -> powerup.getY() + powerup.getHeight() < 0f);
    }

    // ----------------------------------------------------------- wave setup

    private void loadWave(int waveNumber) {
        wave.getEnemies().clear();
        wave.getPowerups().clear();
        wave.getEnemyProjectiles().clear();
        playerProjectiles.clear();
        explosions.clear();

        enemySpawns.clear();
        powerupSpawns.clear();
        readSpawns("enemy", waveNumber, enemySpawns);
        readSpawns("powerup", waveNumber, powerupSpawns);

        elapsedFrames = 0f;
        waveLoaded = true;
    }

    private static boolean hasWave(int waveNumber) {
        return ConfigManager.getInstance().has("wave." + waveNumber + ".enemy.0.type");
    }

    private static void readSpawns(String kind, int waveNumber, List<SpawnDefinition> target) {
        ConfigManager config = ConfigManager.getInstance();
        for (int index = 0; ; index++) {
            String prefix = "wave." + waveNumber + "." + kind + "." + index + ".";
            if (!config.has(prefix + "type")) {
                return;
            }
            target.add(new SpawnDefinition(
                config.getString(prefix + "type").trim(),
                config.getInt(prefix + "arrivalTime"),
                config.getFloat(prefix + "posX")));
        }
    }

    private EnemyShip createEnemy(SpawnDefinition definition) {
        float top = worldHeight();
        // posX in the config is where the enemy should be centred.
        switch (definition.type) {
            case "strafing":
                return new StrafingEnemy(definition.posX - StrafingEnemy.WIDTH / 2f, top, definition.arrivalTime);
            case "shooting":
                ShootingEnemy shooter =
                    new ShootingEnemy(definition.posX - ShootingEnemy.WIDTH / 2f, top, definition.arrivalTime);
                shooter.setFiringRate(firingRate());
                return shooter;
            case "regular":
            default:
                return new RegularEnemy(definition.posX - RegularEnemy.WIDTH / 2f, top, definition.arrivalTime);
        }
    }

    /** The difficulty files spell this key both per wave and globally, so try both. */
    private float firingRate() {
        ConfigManager config = ConfigManager.getInstance();
        String waveKey = "wave." + currentWaveNumber + ".enemy.shooting.firingRate";
        if (config.has(waveKey)) {
            return config.getFloat(waveKey);
        }
        if (config.has("enemy.shooting.firingRate")) {
            return config.getFloat("enemy.shooting.firingRate");
        }
        return ShootingEnemy.DEFAULT_FIRING_RATE;
    }

    private static PowerupEffect createPowerupEffect(String type) {
        switch (type) {
            case "life":
                return new LifeEffect();
            case "cooldown":
                return new CooldownEffect();
            case "engine":
                return new EngineEffect();
            case "shield":
            default:
                return new ShieldEffect();
        }
    }

    // --------------------------------------------------------------- render

    /**
     * Draws the battle without advancing it. PauseScreen reuses this to show the
     * frozen battle underneath its overlay.
     */
    public void renderWorld() {
        beginFrame();

        SpriteBatch batch = game.batch;
        batch.begin();
        batch.setColor(Color.WHITE);

        // Blink the player while the post-hit grace period runs so the state is visible.
        boolean hidden = playerHitInvincibility > 0f && ((int) (playerHitInvincibility / 5f)) % 2 == 1;
        if (!hidden) {
            batch.draw(playerRegion, playerShip.getX(), playerShip.getY(),
                playerShip.getWidth(), playerShip.getHeight());
        }
        if (playerShip.isShielded() || GameSession.getInstance().isInvincibilityMode()) {
            batch.setColor(1f, 1f, 1f, 0.55f);
            batch.draw(shieldRegion,
                playerShip.getCentreX() - shieldRegion.getRegionWidth() / 2f,
                playerShip.getCentreY() - shieldRegion.getRegionHeight() / 2f);
            batch.setColor(Color.WHITE);
        }

        for (EnemyShip enemy : wave.getEnemies()) {
            batch.draw(regionFor(enemy), enemy.getX(), enemy.getY(),
                enemy.getWidth(), enemy.getHeight());
        }
        for (PlayerProjectile projectile : playerProjectiles) {
            batch.draw(playerBoltRegion, projectile.getX(), projectile.getY(),
                projectile.getWidth(), projectile.getHeight());
        }
        for (EnemyProjectile projectile : wave.getEnemyProjectiles()) {
            batch.draw(enemyBoltRegion, projectile.getX(), projectile.getY(),
                projectile.getWidth(), projectile.getHeight());
        }
        for (PowerupEntity powerup : wave.getPowerups()) {
            batch.draw(regionFor(powerup.getEffect()), powerup.getX(), powerup.getY(),
                powerup.getWidth(), powerup.getHeight());
        }
        for (Explosion explosion : explosions) {
            batch.setColor(1f, 1f, 1f, 1f - explosion.getProgress());
            batch.draw(explosionRegion, explosion.getX(), explosion.getY());
        }
        batch.setColor(Color.WHITE);

        drawHud(batch);
        batch.end();
    }

    private void drawHud(SpriteBatch batch) {
        float top = worldHeight();
        text.setSize(game.font, TextRenderer.BODY_SIZE);

        text.draw(batch, game.font, "SCORE: " + score, HUD_MARGIN, top - HUD_MARGIN, Color.WHITE);
        text.draw(batch, game.font, "WAVE: " + currentWaveNumber, HUD_MARGIN, top - HUD_MARGIN - 26f, Color.WHITE);

        if (GameSession.getInstance().getTimeScale() != 1f) {
            text.draw(batch, game.font,
                String.format("TIMESCALE: %.1f", GameSession.getInstance().getTimeScale()),
                HUD_MARGIN, top - HUD_MARGIN - 52f, Color.CYAN);
        }
        if (GameSession.getInstance().isInvincibilityMode()) {
            text.draw(batch, game.font, "INVINCIBLE", HUD_MARGIN, top - HUD_MARGIN - 78f, Color.GOLD);
        }

        batch.setColor(Color.WHITE);
        livesDisplay.draw(batch);
    }

    private TextureRegion regionFor(EnemyShip enemy) {
        if (enemy instanceof StrafingEnemy) {
            return strafingEnemyRegion;
        }
        if (enemy instanceof ShootingEnemy) {
            return shootingEnemyRegion;
        }
        return regularEnemyRegion;
    }

    private TextureRegion regionFor(PowerupEffect effect) {
        if (effect instanceof LifeEffect) {
            return lifePowerupRegion;
        }
        if (effect instanceof CooldownEffect) {
            return cooldownPowerupRegion;
        }
        if (effect instanceof EngineEffect) {
            return enginePowerupRegion;
        }
        return shieldPowerupRegion;
    }

    /** One {@code wave.N.<kind>.I.*} row, with a flag so it only ever spawns once. */
    private static final class SpawnDefinition {
        private final String type;
        private final int arrivalTime;
        private final float posX;
        private boolean spawned;

        private SpawnDefinition(String type, int arrivalTime, float posX) {
            this.type = type;
            this.arrivalTime = arrivalTime;
            this.posX = posX;
        }
    }
}
