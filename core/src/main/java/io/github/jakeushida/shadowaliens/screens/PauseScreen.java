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

public class PauseScreen implements Screen {
    private final Main game;
    private final BattleScreen battleScreen;
    private final GlyphLayout layout;

    public PauseScreen(Main game, BattleScreen battleScreen) {
        this.game = game;
        this.battleScreen = battleScreen;
        this.layout = new GlyphLayout();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(battleScreen);
            return;
        }

        game.batch.begin();

        // Draw title
        String titleText = ConfigManager.getInstance().getString("pausedTitle.text");
        float titleSize = ConfigManager.getInstance().getInt("pausedTitle.size");
        float titleY = ConfigManager.getInstance().getInt("pausedTitle.posY");

        BitmapFont titleFont = game.font;
        titleFont.getData().setScale(titleSize / 24f);
        layout.setText(titleFont, titleText);
        float titleX = (Gdx.graphics.getWidth() - layout.width) / 2;
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(game.batch, titleText, titleX, titleY);

        // Draw controls list
        String controlsText = ConfigManager.getInstance().getString("controlsList.text");
        float controlsY = ConfigManager.getInstance().getInt("controlsList.startPosY");

        BitmapFont controlsFont = game.font;
        controlsFont.getData().setScale(0.8f);
        layout.setText(controlsFont, controlsText);
        float controlsX = (Gdx.graphics.getWidth() - layout.width) / 2;
        controlsFont.setColor(Color.WHITE);
        controlsFont.draw(game.batch, controlsText, controlsX, controlsY);

        // Draw timescale info
        String timescaleText = ConfigManager.getInstance().getString("timescale.text");
        String[] timescalePos = ConfigManager.getInstance().getString("timescale.pos").split(",");
        float timescaleX = Float.parseFloat(timescalePos[0].trim());
        float timescaleY = Float.parseFloat(timescalePos[1].trim());

        controlsFont.getData().setScale(1f);
        controlsFont.setColor(Color.LIGHT_GRAY);
        float currentTimescale = GameSession.getInstance().getTimeScale();
        controlsFont.draw(game.batch, timescaleText + " " + String.format("%.1f", currentTimescale), timescaleX, timescaleY);

        // Draw resume instruction
        controlsFont.setColor(Color.CYAN);
        String resumeText = "PRESS ESC TO RESUME";
        layout.setText(controlsFont, resumeText);
        float resumeX = (Gdx.graphics.getWidth() - layout.width) / 2;
        controlsFont.draw(game.batch, resumeText, resumeX, 150);

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
