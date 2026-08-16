package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.rendering.TextRenderer;

public class StartScreen extends BaseScreen {

    public StartScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        GameSession.getInstance().reset();
    }

    @Override
    public void render(float delta) {
        beginFrame();
        handleInput();

        ConfigManager config = ConfigManager.getInstance();
        float width = worldWidth();

        game.batch.begin();

        text.setSize(game.font, config.getInt("start.title.size"));
        text.drawCentred(game.batch, game.font,
            config.getString("start.title.text"),
            width, config.getInt("start.title.posY"), Color.WHITE);

        text.setSize(game.font, TextRenderer.BODY_SIZE);
        text.drawCentredRows(game.batch, game.font,
            config.getString("start.instructionsList.text"),
            width,
            config.getInt("start.instructionsList.startPosY"),
            config.getInt("start.instructionsList.rowGap"),
            Color.LIGHT_GRAY);

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            loadDifficultyAndStartGame("easy");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            loadDifficultyAndStartGame("medium");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            loadDifficultyAndStartGame("hard");
        }
    }

    private void loadDifficultyAndStartGame(String difficulty) {
        ConfigManager.getInstance().loadDifficulty(difficulty);
        game.setScreen(new BattleScreen(game));
    }
}
