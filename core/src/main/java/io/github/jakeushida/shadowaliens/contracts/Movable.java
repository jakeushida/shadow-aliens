package io.github.jakeushida.shadowaliens.contracts;

public interface Movable {
    float getSpeedX();

    float getSpeedY();

    void setSpeedX(float speedX);

    void setSpeedY(float speedY);

    void move(float delta);
}
