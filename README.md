# Shadow Aliens

A retro top-down space shooter built with [libGDX](https://libgdx.com/). You pilot a
ship along the bottom of the screen and clear three waves of descending aliens: regular
ones dive straight down, strafing ones weave as they fall, and shooting ones fire back.
Powerups grant a shield, an extra life, a faster gun or a faster engine, one at a time.
Waves, enemy arrival times, scoring and difficulty all come from `.properties` files in
`assets/`, so the game can be retuned without recompiling.

**[Play it in your browser](https://jakeushida.github.io/shadow-aliens/)** (needs a keyboard).

## Controls

| Key | Action |
| --- | --- |
| `1` / `2` / `3` | Pick easy, medium or hard on the title screen |
| `A` / `D` | Move left and right |
| `SPACE` | Shoot (and restart from the end screen) |
| `ESC` | Pause and resume |
| `I` | Debug: toggle invincibility |
| `G` / `F` | Debug: speed time up and down |

## Design

The UML class diagram below describes the design of the game.

```mermaid
classDiagram
%% Application shell
    class Main {
        <<libGDX Game>>
        +SpriteBatch batch
        +Viewport viewport
        +TextureAtlas atlas
        +BitmapFont font
        +TextRenderer text
        +create()
        +dispose()
    }

%% Core Managers
    class ConfigManager {
        <<Singleton>>
        -Properties properties
        +load(String filepath)
        +loadDifficulty(String level)
        +getString(String key) String
        +getInt(String key) int
        +getFloat(String key) float
        +has(String key) boolean
    }
    class GameSession {
        <<Singleton>>
        -int currentScore
        -int currentLives
        -int currentWave
        -float timeScale
        -boolean invincibilityMode
        +reset()
    }

%% Screen State Machine
    class Screen {
        <<libGDX Interface>>
        +show()
        +render(float delta)
        +resize(int width, int height)
        +hide()
        +dispose()
    }
    class BaseScreen {
        <<Abstract>>
        #Main game
        #TextRenderer text
        #worldWidth() float
        #worldHeight() float
        #beginFrame()
    }
    class StartScreen
    class BattleScreen {
        -int score
        -int currentWaveNumber
        -loadWave(int waveNumber)
        +renderWorld()
    }
    class PauseScreen
    class EndScreen

    Screen <|.. BaseScreen
    BaseScreen <|-- StartScreen
    BaseScreen <|-- BattleScreen
    BaseScreen <|-- PauseScreen
    BaseScreen <|-- EndScreen

%% Rendering
    class RenderLayer {
        <<Enumeration>>
        BACKGROUND
        SHIPS
        PROJECTILES
        UI
    }
    class TextRenderer {
        +setSize(BitmapFont font, float pixelSize)
        +drawCentred(...) float
        +drawCentredRows(...)
    }

%% Game Entities Base
    class GameEntity {
        <<Abstract>>
        #float x
        #float y
        #float width
        #float height
        #RenderLayer layer
        +getCentreX() float
        +getCentreY() float
        +update(float delta)*
        +draw(SpriteBatch batch)*
    }

    class Collidable {
        <<Interface>>
        +getBoundingBox() Rectangle
        +onCollision(Collidable other)
    }

    class Movable {
        <<Interface>>
        +getSpeedX() float
        +getSpeedY() float
        +move(float delta)
    }

    class Shooter {
        <<Interface>>
        +shoot()
    }

%% Ships
    class PlayerShip {
        -int lives
        -PowerupEffect currentBuff
        -boolean shielded
        -float shotCooldownMultiplier
        -float engineMultiplier
        +setBuff(PowerupEffect effect)
    }
    class EnemyShip {
        <<Abstract>>
        #int arrivalTime
        +getArrivalTime() int
    }
    class ShootingEnemy {
        -float firingRate
        -boolean shotPending
        +consumePendingShot() boolean
    }

    GameEntity <|-- PlayerShip
    Collidable <|.. PlayerShip
    Movable <|.. PlayerShip
    Shooter <|.. PlayerShip

    GameEntity <|-- EnemyShip
    Collidable <|.. EnemyShip
    Movable <|.. EnemyShip

    EnemyShip <|-- RegularEnemy
    EnemyShip <|-- StrafingEnemy
    EnemyShip <|-- ShootingEnemy
    Shooter <|.. ShootingEnemy

%% Projectiles & Explosions
    class Projectile {
        <<Abstract>>
    }
    GameEntity <|-- Projectile
    Collidable <|.. Projectile
    Movable <|.. Projectile
    Projectile <|-- PlayerProjectile
    Projectile <|-- EnemyProjectile

    class Explosion {
        -float duration
        +isFinished() boolean
        +getProgress() float
    }
    GameEntity <|-- Explosion

%% UI Elements
    class LivesDisplay {
        -TextureRegion heartImage
        -float gap
    }
    GameEntity <|-- LivesDisplay

%% Strategy Pattern for Powerups
    class PowerupEffect {
        <<Interface>>
        +apply(PlayerShip player)
        +remove(PlayerShip player)
    }
    PowerupEffect <|.. ShieldEffect
    PowerupEffect <|.. LifeEffect
    PowerupEffect <|.. CooldownEffect
    PowerupEffect <|.. EngineEffect

    class PowerupEntity {
        -PowerupEffect effect
    }
    GameEntity <|-- PowerupEntity
    Collidable <|.. PowerupEntity
    Movable <|.. PowerupEntity

%% Waves & Aggregation
    class Wave {
        -List~EnemyShip~ enemies
        -List~PowerupEntity~ powerups
        -List~EnemyProjectile~ enemyProjectiles
        +update(float delta)
        +isComplete() boolean
    }

%% Core Relationships
    Main *-- Screen : Active screen
    Main *-- TextRenderer
    BaseScreen --> Main : Shared batch, viewport, assets

    PlayerShip o-- PowerupEffect : Strategy
    PowerupEntity o-- PowerupEffect : Contains

    BattleScreen *-- Wave : Manages
    BattleScreen *-- PlayerShip
    BattleScreen *-- LivesDisplay
    BattleScreen *-- Explosion : Spawns
    PauseScreen --> BattleScreen : Resumes

    Wave o-- EnemyShip
    Wave o-- PowerupEntity
    Wave o-- EnemyProjectile

    GameEntity --> RenderLayer : Uses
    StartScreen --> ConfigManager : Selects difficulty
    BattleScreen --> ConfigManager : Reads waves and scoring
    BattleScreen --> GameSession : Time scale, cheats, score
```

## Configuration

Gameplay is data-driven from `assets/`:

- `global.properties`: window size, background colour, and all on-screen text and layout.
- `easy.properties`, `medium.properties`, `hard.properties`: player stats, scoring, and the
  per-wave enemy and powerup tables. Selecting a difficulty reloads the globals and then
  layers the difficulty file on top.

Timing values such as `arrivalTime`, `player.shootCooldown` and `enemy.shooting.firingRate`
are counts of frames at 60fps (`GameSession.FRAMES_PER_SECOND`).

## Platforms

- `core`: main module with the application logic shared by all platforms.
- `lwjgl3`: primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: web backend, compiled to JavaScript and published to GitHub Pages by
  `.github/workflows/deploy-pages.yml` on every push to `main`.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.

Note that `gradle.properties` sets `org.gradle.logging.level=quiet`, so add
`-Dorg.gradle.logging.level=lifecycle` if you want to see task output.

Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `core:test`: runs the JUnit 5 unit tests.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the desktop application.
- `teavm:buildRelease`: builds the JavaScript application into `teavm/build/dist/webapp`.
- `teavm:runRelease`: serves the JavaScript application at http://localhost:8080 via a local Jetty server.

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

---

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
