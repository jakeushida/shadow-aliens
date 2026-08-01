# Shadow Aliens
```mermaid
classDiagram
%% Core Managers
    class ConfigManager {
        <<Singleton>>
        -Properties properties
        +load(String filepath)
        +loadDifficulty(String level)
        +getString(String key)
        +getInt(String key)
    }
    class GameSession {
        <<Singleton>>
        +int currentScore
        +int currentLives
        +int currentWave
        +float timeScale
        +boolean invincibilityMode
    }

%% Screen State Machine
    class Screen {
        <<Interface>>
        +show()
        +render(float delta)
        +hide()
    }
    class StartScreen
    class BattleScreen
    class PauseScreen
    class EndScreen

    Screen <|.. StartScreen
    Screen <|.. BattleScreen
    Screen <|.. PauseScreen
    Screen <|.. EndScreen

%% Z-Order
    class RenderLayer {
        <<Enumeration>>
        BACKGROUND
        SHIPS
        PROJECTILES
        UI
    }

%% Game Entities Base
    class GameEntity {
        <<Abstract>>
        +float x
        +float y
        +RenderLayer layer
        +update(float delta)*
        +draw(SpriteBatch batch)*
    }

    class Collidable {
        <<Interface>>
        +Rectangle getBoundingBox()
        +onCollision(Collidable other)
    }

    class Movable {
        <<Interface>>
        +float speedX
        +float speedY
        +move(float delta)
    }

    class Shooter {
        <<Interface>>
        +shoot()
    }

%% Ships
    class PlayerShip {
        +int lives
        -PowerupEffect currentBuff
        +setBuff(PowerupEffect effect)
    }
    class EnemyShip {
        <<Abstract>>
        +int arrivalTime
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
        +int duration
        +boolean isFinished()
    }
    GameEntity <|-- Explosion

%% UI Elements
    class TextElement {
        +String text
        +BitmapFont font
        +Color color
    }
    class LivesDisplay {
        +Texture heartImage
        +float gap
    }
    GameEntity <|-- TextElement
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
    PlayerShip *-- PowerupEffect : Strategy
    PowerupEntity o-- PowerupEffect : Contains
    BattleScreen *-- Wave : Manages
    BattleScreen *-- PlayerShip
    GameEntity --> RenderLayer : Uses
    ConfigManager <-- StartScreen : Selects Difficulty
```

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
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
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `teavm:build`: builds the JavaScript application into the build/dist/webapp folder.
- `teavm:run`: serves the JavaScript application at http://localhost:8080 via a local Jetty server.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
