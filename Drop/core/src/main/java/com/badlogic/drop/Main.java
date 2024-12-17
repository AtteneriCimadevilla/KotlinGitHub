package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Texture toxicDropTexture;
    Sound dropSound;
    Music music;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite bucketSprite; // Declare a new Sprite variable

    Vector2 touchPos;

    Array<Sprite> dropSprites;
    Array<Sprite> toxicDropSprites;

    float dropTimer;

    Rectangle bucketRectangle;
    Rectangle dropRectangle;
    Rectangle toxicDropRectangle;

    private int score, lives;
    private String showScore = "Score: ";
    private String showLives = "Lives: ";
    BitmapFont bitmapFontName;
    BitmapFont bitmapFontGameOver;

    float gameOverTimer;
    float gameOverDelay = 30f;

    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");
        toxicDropTexture = new Texture("toxic-drop.png");

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(800, 500);

        bucketSprite = new Sprite(bucketTexture); // Initialize the sprite based on the texture
        bucketSprite.setSize(100, 100); // Define the size of the sprite

        touchPos = new Vector2();

        dropSprites = new Array<>();
        toxicDropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        toxicDropRectangle = new Rectangle();

        music.setLooping(true);
        music.setVolume(.5f);
        music.play();

        score = 0;
        lives = 5;
        bitmapFontName = new BitmapFont();
        bitmapFontName.getData().setScale(2);
        bitmapFontGameOver = new BitmapFont();
        bitmapFontGameOver.getData().setScale(5);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        if(lives > 0) {
            input();
            logic();
        } else {
            if(gameOverTimer > gameOverDelay) {
                Gdx.app.exit();
            } else {
                gameOverTimer += Gdx.graphics.getDeltaTime();
            }
        }
        draw();
    }

    private void input() {
        float speed = 400f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            bucketSprite.translateY(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            bucketSprite.translateY(-speed * delta);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY()); // Get where the touch happened on screen
            viewport.unproject(touchPos); // Convert the units to the world units of the viewport
            bucketSprite.setCenterX(touchPos.x); // Change the horizontally centered position of the bucket
            bucketSprite.setCenterY(touchPos.y);
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));
        bucketSprite.setY(MathUtils.clamp(bucketSprite.getY(), 0, worldHeight - bucketHeight));

        float delta = Gdx.graphics.getDeltaTime();
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-200f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i);
            else if (bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i);
                dropSound.play(); // Play the sound

                // CHANGE SCORE:
                score++;
                // CHANGE LIVES:
                if (score % 10 == 0) lives++;
            }
        }

        for (int i = toxicDropSprites.size - 1; i >= 0; i--) {
            Sprite toxicDropSprite = toxicDropSprites.get(i);
            float toxicDropWidth = toxicDropSprite.getWidth();
            float toxicDropHeight = toxicDropSprite.getHeight();

            toxicDropSprite.translateY(-200f * delta);
            toxicDropRectangle.set(toxicDropSprite.getX(), toxicDropSprite.getY(), toxicDropWidth, toxicDropHeight);

            if (toxicDropSprite.getY() < -toxicDropHeight) toxicDropSprites.removeIndex(i);
            else if (bucketRectangle.overlaps(toxicDropRectangle)) {
                toxicDropSprites.removeIndex(i);
                dropSound.play(); // Play the sound

                // CHANGE LIVES:
                lives--;
            }
        }

        dropTimer += delta;
        if (dropTimer > 1f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        if (lives <= 0) {

            bitmapFontGameOver.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            bitmapFontGameOver.draw(spriteBatch, "GAME OVER", 200, 200);

        } else {
            bucketSprite.draw(spriteBatch);

            // draw each sprite
            for (Sprite dropSprite : dropSprites) {
                dropSprite.draw(spriteBatch);
            }

            for (Sprite toxicDropSprite : toxicDropSprites) {
                toxicDropSprite.draw(spriteBatch);
            }
        }

        // SCORE:
        bitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        bitmapFontName.draw(spriteBatch, showScore + score, 50, 450);
        bitmapFontName.draw(spriteBatch, showLives + lives, 50, 425);

        spriteBatch.end();
    }

    private void createDroplet() {
        float dropWidth = 100;
        float dropHeight = 100;
        float toxicDropWidth = 100;
        float toxicDropHeight = 100;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth)); // Randomize the drop's x position
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);

        Sprite toxicDropSprite = new Sprite(toxicDropTexture);
        toxicDropSprite.setSize(toxicDropWidth, toxicDropHeight);
        toxicDropSprite.setX(MathUtils.random(0f, worldWidth - toxicDropWidth)); // Randomize the drop's x position
        toxicDropSprite.setY(worldHeight);
        toxicDropSprites.add(toxicDropSprite);

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
        // Destroy application's resources here.
    }
}
