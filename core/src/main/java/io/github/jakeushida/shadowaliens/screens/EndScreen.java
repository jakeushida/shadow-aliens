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

public class EndScreen implements Screen {
    private final Main game;
    private final int finalScore;
    private final boolean won;
    private final GlyphLayout layout;

    public EndScreen(Main game, int finalScore, boolean won) {
        this.game = game;
        this.finalScore = finalScore;
        this.won = won;
        this.layout = new GlyphLayout();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new StartScreen(game));
            return;
        }

        game.batch.begin();

        BitmapFont font = game.font;

        // Draw win/lose title
        String titleText;
        float titleSize;
        float titleY;
        Color titleColor;

        if (won) {
            titleText = ConfigManager.getInstance().getString("end.win.text");
            titleSize = ConfigManager.getInstance().getInt("end.win.size");
            titleY = ConfigManager.getInstance().getInt("end.win.posY");
            titleColor = Color.GREEN;
        } else {
            titleText = ConfigManager.getInstance().getString("end.lose.text");
            titleSize = ConfigManager.getInstance().getInt("end.lose.size");
            titleY = ConfigManager.getInstance().getInt("end.lose.posY");
            titleColor = Color.RED;
        }

        font.getData().setScale(titleSize / 24f);
        layout.setText(font, titleText);
        float titleX = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.setColor(titleColor);
        font.draw(game.batch, titleText, titleX, titleY);

        // Draw final score
        String scoreText = "FINAL SCORE: " + finalScore;
        font.getData().setScale(1.5f);
        layout.setText(font, scoreText);
        float scoreX = (Gdx.graphics.getWidth() - layout.width) / 2;
        float scoreY = titleY - 80;
        font.setColor(Color.YELLOW);
        font.draw(game.batch, scoreText, scoreX, scoreY);

        // Draw instructions
        String instructionsText = ConfigManager.getInstance().getString("end.instructionsList.text");
        float instructionsY = ConfigManager.getInstance().getInt("end.instructionsList.startPosY");

        font.getData().setScale(1f);
        layout.setText(font, instructionsText);
        float instructionsX = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.setColor(Color.CYAN);
        font.draw(game.batch, instructionsText, instructionsX, instructionsY);

        game.batch.end();
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
