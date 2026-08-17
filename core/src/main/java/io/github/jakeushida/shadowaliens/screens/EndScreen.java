package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.rendering.TextRenderer;

public class EndScreen extends BaseScreen {
    private final int finalScore;
    private final boolean won;

    public EndScreen(Main game, int finalScore, boolean won) {
        super(game);
        this.finalScore = finalScore;
        this.won = won;
    }

    @Override
    public void render(float delta) {
        beginFrame();

        // R matches the "back to title" key used in battle and on the pause screen.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new StartScreen(game));
            return;
        }

        ConfigManager config = ConfigManager.getInstance();
        float width = worldWidth();

        String titleKey = won ? "end.win" : "end.lose";
        float titleY = config.getInt(titleKey + ".posY");

        game.batch.begin();

        text.setSize(game.font, config.getInt(titleKey + ".size"));
        text.drawCentred(game.batch, game.font, config.getString(titleKey + ".text"),
            width, titleY, won ? Color.LIME : Color.SCARLET);

        text.setSize(game.font, TextRenderer.BODY_SIZE * 1.5f);
        text.drawCentred(game.batch, game.font, "FINAL SCORE: " + finalScore,
            width, titleY - 80f, Color.YELLOW);

        text.setSize(game.font, TextRenderer.BODY_SIZE);
        text.drawCentredRows(game.batch, game.font,
            config.getString("end.instructionsList.text"),
            width,
            config.getInt("end.instructionsList.startPosY"),
            config.getInt("end.instructionsList.rowGap"),
            Color.CYAN);

        game.batch.end();
    }
}
