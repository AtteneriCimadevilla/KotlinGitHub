package com.badlogic.mastermind20250129;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Mastermind extends Game {

    public static final int WIDTH = 400;
    public static final int HEIGHT = 800;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;

    @Override
    public void create() {

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assetManager = new AssetManager();

        setScreen(new MastermindScreen());

    }

    public SpriteBatch getSpriteBatch() { return spriteBatch; }
    public ShapeRenderer getShapeRenderer() { return shapeRenderer; }
    public AssetManager getAssetManager() { return assetManager; }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        assetManager.dispose();
        getScreen().dispose();
    }

}
