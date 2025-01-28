package com.badlogic.mastermind;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    Pixmap pixmap;
    Texture texture;
    Array<Sprite> colorSprites = new Array<Sprite>();
    private Array<Color> colorOptions;
    private Array<Sprite> gridSprites;
    private Array<Sprite> colorOptionSprites;
//    Sprite[][] playerSprites = new Sprite[11][4];
    private SpriteBatch batch;
    private Texture circleTexture;
    private int gridRows = 11;
    private int gridCols = 4;
    private int circleSize = 50;
    private int padding = 10;




    @Override
    public void create() {
//        pixmap = new Pixmap(120, 120, Pixmap.Format.RGBA8888);
//        pixmap.setColor(Color.WHITE);
//        pixmap.fillCircle(60, 60, 50);
//        texture = new Texture(pixmap); //remember to dispose of later
//        pixmap.dispose();
        batch = new SpriteBatch();

        // Create the grid texture (grey circles)
        circleTexture = createCircleTexture(circleSize, Color.LIGHT_GRAY);

        // Create the grid sprites
        gridSprites = new Array<>();
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                Sprite sprite = new Sprite(circleTexture);
                sprite.setPosition(
                    col * (circleSize + padding) + padding,
                    Gdx.graphics.getHeight() - (row + 1) * (circleSize + padding) - padding
                );
                gridSprites.add(sprite);
            }
        }

        // Initialize color options
        colorOptions = new Array<>();
        colorOptions.add(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN);

        // Create sprites for color options
        colorOptionSprites = new Array<>();
        for (int i = 0; i < colorOptions.size; i++) {
            Sprite sprite = new Sprite(createCircleTexture(circleSize, colorOptions.get(i)));
            sprite.setPosition(
                i * (circleSize + padding) + padding,
                padding
            );
            colorOptionSprites.add(sprite);
        }
    }

    private Texture createCircleTexture(int size, Color color) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(size / 2, size / 2, size / 2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void resize(int width, int height) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw the grid
        for (Sprite sprite : gridSprites) {
            sprite.draw(batch);
        }

        // Draw the color options
        for (Sprite sprite : colorOptionSprites) {
            sprite.draw(batch);
        }

        batch.end();    }

    @Override
    public void render() {
        // Draw your application here.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        batch.dispose();
        circleTexture.dispose();
    }

//    void addColorSprite(Color color, int x, int y){
//        Sprite sprite = new Sprite(texture);
//        sprite.setColor(Color.RED);
//        sprite.setPosition(x, y);
//
//        colorSprites.add(sprite);
//    }
}
