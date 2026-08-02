package io.github.jakeushida.shadowaliens.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;

public class TextElement extends GameEntity {
    private String text;
    private final BitmapFont font;
    private Color color;

    public TextElement(float x, float y, String text, BitmapFont font, Color color) {
        super(x, y, RenderLayer.UI);
        this.text = text;
        this.font = font;
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void update(float delta) {
        // Static UI text does not require updates in the base implementation.
    }

    @Override
    public void draw(SpriteBatch batch) {
        Color previous = new Color(font.getColor());
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.setColor(previous);
    }
}
