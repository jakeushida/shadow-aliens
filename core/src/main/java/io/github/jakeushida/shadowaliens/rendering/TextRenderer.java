package io.github.jakeushida.shadowaliens.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

/**
 * Draws UI text at a size given in pixels rather than as a raw font scale.
 *
 * <p>Screens used to scale the shared font with {@code size / 24f}, but
 * upheaval.fnt is generated at {@code size=72}. A "48px" title therefore came out
 * 1114px wide inside an 800px window and the instruction lists came out around
 * 2100px wide, so most text was clipped off screen. Sizes here are converted
 * against the real page size, and anything still too wide is wrapped instead of
 * running past the edge.
 */
public final class TextRenderer {
    /** Pixel size upheaval.fnt was generated at (the {@code size=72} field in its header). */
    public static final float NATIVE_SIZE = 72f;

    /** Default body copy size, matching {@code text.size} in global.properties. */
    public static final float BODY_SIZE = 24f;

    /** Scales {@code font} so that a line renders at roughly {@code pixelSize} tall. */
    public void setSize(BitmapFont font, float pixelSize) {
        font.getData().setScale(pixelSize / NATIVE_SIZE);
    }

    /** Draws one left aligned line and returns the height it occupied. */
    public float draw(SpriteBatch batch, BitmapFont font, String text, float x, float y, Color color) {
        font.setColor(color);
        return font.draw(batch, text, x, y).height;
    }

    /** Draws {@code text} centred across {@code worldWidth}, wrapping if it does not fit. */
    public float drawCentred(SpriteBatch batch, BitmapFont font, String text, float worldWidth, float y, Color color) {
        font.setColor(color);
        return font.draw(batch, text, 0f, y, worldWidth, Align.center, true).height;
    }

    /**
     * Draws a comma separated list from the .properties files as one centred row
     * per item, stepping down by {@code rowGap} each time.
     *
     * <p>The {@code *.rowGap} keys were already in the config but nothing read
     * them, so lists such as {@code controlsList.text} were drawn as a single
     * line more than twice the width of the window.
     */
    public void drawCentredRows(SpriteBatch batch, BitmapFont font, String list, float worldWidth,
                                float topY, float rowGap, Color color) {
        float y = topY;
        for (String row : list.split(",")) {
            String trimmed = row.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            float height = drawCentred(batch, font, trimmed, worldWidth, y, color);
            // A wrapped row is taller than one line, so never let rows collide.
            y -= Math.max(rowGap, height + 4f);
        }
    }
}
