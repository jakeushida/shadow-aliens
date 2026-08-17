package io.github.jakeushida.shadowaliens.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.jakeushida.shadowaliens.Main;
import io.github.jakeushida.shadowaliens.managers.ConfigManager;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.rendering.TextRenderer;

public class PauseScreen extends BaseScreen {
    /** Opacity of the black wash drawn over the frozen battle. */
    private static final float DIM_ALPHA = 0.72f;

    private final BattleScreen battleScreen;

    public PauseScreen(Main game, BattleScreen battleScreen) {
        super(game);
        this.battleScreen = battleScreen;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(battleScreen);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new StartScreen(game));
            return;
        }

        // Draw the battle frozen underneath, then wash it out. The old code
        // passed an alpha to glClearColor, which the default framebuffer ignores,
        // so the intended overlay was really just an opaque black screen.
        battleScreen.renderWorld();

        ConfigManager config = ConfigManager.getInstance();
        float width = worldWidth();

        game.batch.begin();

        game.batch.setColor(0f, 0f, 0f, DIM_ALPHA);
        game.batch.draw(game.blankTexture, 0f, 0f, width, worldHeight());
        game.batch.setColor(Color.WHITE);

        text.setSize(game.font, config.getInt("pausedTitle.size"));
        text.drawCentred(game.batch, game.font, config.getString("pausedTitle.text"),
            width, config.getInt("pausedTitle.posY"), Color.YELLOW);

        text.setSize(game.font, TextRenderer.BODY_SIZE);
        text.drawCentredRows(game.batch, game.font,
            config.getString("controlsList.text"),
            width,
            config.getInt("controlsList.startPosY"),
            config.getInt("controlsList.rowGap"),
            Color.WHITE);

        String[] timescalePos = config.getString("timescale.pos").split(",");
        text.draw(game.batch, game.font,
            config.getString("timescale.text") + " "
                + String.format("%.1f", GameSession.getInstance().getTimeScale()),
            Float.parseFloat(timescalePos[0].trim()),
            Float.parseFloat(timescalePos[1].trim()),
            Color.LIGHT_GRAY);

        text.drawCentred(game.batch, game.font, "PRESS ESC TO RESUME", width, 60f, Color.CYAN);

        game.batch.end();
    }
}
