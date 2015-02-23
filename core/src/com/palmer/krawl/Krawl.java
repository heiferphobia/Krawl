package com.palmer.krawl;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.palmer.krawl.Screens.GameScreen;

public class Krawl extends Game {

    private GameScreen gameScreen;
    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        this.setScreen(gameScreen);

    }

    public Krawl() {
        super();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
    }

    @Override
    public Screen getScreen() {
        return super.getScreen();
    }
}
