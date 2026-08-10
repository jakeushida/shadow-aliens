package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.managers.GameSession;

public class StartScreen implements Screen {
    private final Main game;
    private final GlyphLayout layout;

    public StartScreen(Main game) {
        this.game = game;
        this.layout = new GlyphLayout();
    }

    @Override
    public void show() {
        GameSession.getInstance().reset();
    }

    @Override
    public void render(float delta) {
        // Clear background
        float[] bgColor = parseColorFromConfig("background.colour");
        Gdx.gl.glClearColor(bgColor[0], bgColor[1], bgColor[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input
        handleInput();

        // Render UI
        game.batch.begin();

        // Draw title
        String titleText = ConfigManager.getInstance().getString("start.title.text");
        float titleSize = ConfigManager.getInstance().getInt("start.title.size");
        float titleY = ConfigManager.getInstance().getInt("start.title.posY");

        BitmapFont titleFont = game.font;
        titleFont.getData().setScale(titleSize / 24f); // Base size is 24
        layout.setText(titleFont, titleText);
        float titleX = (Gdx.graphics.getWidth() - layout.width) / 2;
        titleFont.setColor(Color.WHITE);
        titleFont.draw(game.batch, titleText, titleX, titleY);

        // Draw instructions
        String instructionsText = ConfigManager.getInstance().getString("start.instructionsList.text");
        float instructionsY = ConfigManager.getInstance().getInt("start.instructionsList.startPosY");

        BitmapFont instructionsFont = game.font;
        instructionsFont.getData().setScale(1f); // Normal size
        layout.setText(instructionsFont, instructionsText);
        float instructionsX = (Gdx.graphics.getWidth() - layout.width) / 2;
        instructionsFont.setColor(Color.LIGHT_GRAY);
        instructionsFont.draw(game.batch, instructionsText, instructionsX, instructionsY);

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

    private float[] parseColorFromConfig(String key) {
        String value = ConfigManager.getInstance().getString(key);
        String[] parts = value.split(",");
        return new float[]{
            Float.parseFloat(parts[0].trim()) / 255f,
            Float.parseFloat(parts[1].trim()) / 255f,
            Float.parseFloat(parts[2].trim()) / 255f
        };
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
