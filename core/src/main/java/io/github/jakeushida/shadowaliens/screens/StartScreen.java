package io.github.jakeushida.shadowaliens.screens;

import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.managers.GameSession;

public class StartScreen implements Screen {
    @Override
    public void show() {
        GameSession.getInstance().reset();
        ConfigManager.getInstance().loadDifficulty("medium");
    }

    @Override
    public void render(float delta) {
        // Intro/start menu rendering and input handling are introduced in gameplay pass.
    }

    @Override
    public void hide() {
        // No resources to release in base implementation.
    }
}
