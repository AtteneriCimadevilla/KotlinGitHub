package com.badlogic.mastermind20250129;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Mastermind extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}