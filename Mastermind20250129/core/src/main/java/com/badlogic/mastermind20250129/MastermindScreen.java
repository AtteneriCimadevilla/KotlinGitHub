package com.badlogic.mastermind20250129;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** First screen of the application. Displayed after the application is created. */
public class MastermindScreen implements Screen {

    private final Mastermind MASTERMIND;
    private final Logic MASTERMIND_LOGIC;
    private final Array<Integer> attempt;

    private BitmapFont bitmapFont;
    private OrthographicCamera orthographicCamera;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;

    private float cell = 40;
    private float feedbackButton = 15;
    private float boardX;
    private float boardY;
    private float margin = 20;

    private static final Color[] COLOURS = new Color[] {
        new Color(153,0,153,1),         // Purple
        new Color(255, 153, 51, 1),     // Orange
        new Color(51, 102, 204, 1),     // Blue
        new Color(255, 255, 51, 1),     // Yellow
        new Color(204, 0, 51, 1),       // Red
        new Color(153, 102, 51, 1),     // Brown
        new Color(	255, 0, 255, 1)     // Magenta
    };

    public MastermindScreen() {
        MASTERMIND = new Mastermind();
        MASTERMIND_LOGIC = new Logic();
        attempt = new Array<>();
        init();
    }

    private void init() {
        bitmapFont = new BitmapFont();
        bitmapFont.getData().setScale(2);
        orthographicCamera = new OrthographicCamera();
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(400, 800, orthographicCamera);

        orthographicCamera.position.set(viewport.getScreenWidth() / 2f, viewport.getScreenHeight() / 2f, 0);
        orthographicCamera.update();

        updateCell();
        calculatePosition();
        setupInput();
    }

    private void updateCell() {
        float minimumSize = Math.min(viewport.getScreenWidth(), viewport.getScreenHeight()) / 15f;
        cell = Math.max(minimumSize, 40); // Ensure cell is at least 40 pixels.
        feedbackButton = cell / 2.5f;             // Adjust feedback based on cell size.
    }

    private void calculatePosition() {
        boardX = (viewport.getWorldWidth() - (MASTERMIND_LOGIC.getPasswordLength() * cell)) / 2f;
        boardY = (viewport.getWorldHeight() - (MASTERMIND_LOGIC.getMaxTries() * cell)) / 2f;
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 touch = new Vector3(screenX, screenY, 0);
                viewport.unproject(touch);
                handleTouch(touch.x, touch.y);
                return true;
            }
        });
    }

    private void handleTouch(float x, float y) {
        if (MASTERMIND_LOGIC.playerLoses()) return;

        // Check if the touch is within a colour cell
        float pickY = boardY - margin - cell;
        if (y >= pickY && y <= pickY + cell) {
            float pickX = (viewport.getWorldWidth() - (COLOURS.length * cell)) / 2f;
            float relativeX = x - pickX;
            int index = (int) (relativeX / cell);

            if (index >= 0
                    && index < COLOURS.length
                    && attempt.size < MASTERMIND_LOGIC.getPasswordLength()) {
                attempt.add(index);
                if (attempt.size == MASTERMIND_LOGIC.getPasswordLength()) {
                    generateAttemptArray();
                }
            }
        } else {
            // Check if the touch is within an already filled cell
            float tryY = boardY + MASTERMIND_LOGIC.getCurrentTry() * cell;
            for (int i = 0; i < attempt.size; i++) {
                float colourX = boardX + i * cell;
                // Check if the touch is within the current attempt
                if (x >= colourX && x <= colourX + cell
                        && y >= tryY && y <= tryY + cell) {
                    // Remove the colour from the current attempt
                    attempt.removeIndex(i);
                    break; // Exit the loop
                }
            }
        }
    }

    private void generateAttemptArray() {
        int[] attemptArray = new int[MASTERMIND_LOGIC.getPasswordLength()];
        for (int i = 0; i < attempt.size; i++) {
            attemptArray[i] = attempt.get(i);
        }
        MASTERMIND_LOGIC.getFeedback(attemptArray);
        attempt.clear();
    }

    @Override
    public void render(float delta) {
        // Clear screen:
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthographicCamera.update();
        shapeRenderer.setProjectionMatrix(orthographicCamera.combined);

        // Draw the board:
        drawBoard();
        drawTries();
        drawOptions();

        // Draw text:
        MASTERMIND.getSpriteBatch().setProjectionMatrix(orthographicCamera.combined);
        MASTERMIND.getSpriteBatch().begin();
        drawText();
        MASTERMIND.getSpriteBatch().end();
    }

    private void drawBoard() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the background:
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(boardX - margin, boardY - margin,
            MASTERMIND_LOGIC.getPasswordLength() * cell + margin * 2,
            MASTERMIND_LOGIC.getMaxTries() * cell + margin * 2);

        // Draw the grid:
        shapeRenderer.setColor(Color.GOLDENROD);
        for (int row = 0; row < MASTERMIND_LOGIC.getMaxTries(); row++) {
            for (int col = 0; col < MASTERMIND_LOGIC.getPasswordLength(); col++) {
                shapeRenderer.rect(
                    boardX + col * cell + 1,
                    boardY + row * cell + 1,
                    cell - 2,
                    cell - 2
                );
            }
        }

        shapeRenderer.end();
    }

    private void drawTries() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw previous tries:
        Array<int[]> tries = MASTERMIND_LOGIC.getTries();
        Array<Feedback> feedbackLog = MASTERMIND_LOGIC.getFeedbackLog();

        for (int row = 0; row < tries.size; row++) {
            int[] attempt = tries.get(row);
            for (int col = 0; col < attempt.length; col++) {
                shapeRenderer.setColor(COLOURS[attempt[col]]);
                shapeRenderer.circle(
                    boardX + col * cell + cell / 2f,
                    boardY + row * cell + cell / 2f,
                    cell / 3f
                );
            }

            // Draw feedback:
            drawFeedback(feedbackLog.get(row), row);
        }

        // Draw current attempt:
        for (int i = 0; i < attempt.size; i++) {
            shapeRenderer.setColor(COLOURS[attempt.get(i)]);
            shapeRenderer.circle(
                boardX + i * cell + cell / 2f,
                boardY + MASTERMIND_LOGIC.getCurrentTry() * cell + cell / 2f,
                cell / 3f
            );
        }

        shapeRenderer.end();
    }

    private void drawFeedback(Feedback feedback, int row) {
        float x = boardX + MASTERMIND_LOGIC.getPasswordLength() * cell + margin;
        float y = boardY + row * cell + cell / 2f;

        // Exact matches in black:
        shapeRenderer.setColor(Color.BLACK);
        for (int i = 0; i < feedback.getPositionCorrect(); i++) {
            shapeRenderer.circle(
                x + (i % 2) * feedbackButton,
                y + (i / 2) * feedbackButton,
                feedbackButton / 3f
            );
        }

        // Incorrect matches in white:
        shapeRenderer.setColor(Color.WHITE);
        for (int i = 0; i < feedback.getPositionIncorrect(); i++) {
            shapeRenderer.circle(
                x + ((i + feedback.getPositionCorrect()) % 2) * feedbackButton,
                y - ((i + feedback.getPositionCorrect()) / 2) * feedbackButton,
                feedbackButton / 3f
            );
        }
    }

    private void drawOptions() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the options' background:
        float optionsWidth = MASTERMIND_LOGIC.getOptions() * cell + margin * 2; // Width of the options block.
        float optionsPositionX = (viewport.getWorldWidth() - optionsWidth) / 2f; // X position of the options block, in the middle of the screen.
        float optionsPositionY = boardY - margin - cell / 2f; // Y position of the options block, below the board.

        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(optionsPositionX, optionsPositionY - margin, optionsWidth, cell + margin * 2);

        // Draw the options:
        for (int i = 0; i < COLOURS.length; i++) {
            shapeRenderer.setColor(COLOURS[i]);

            // Center each circle within its cell:
            float circleX = optionsPositionX + margin + (i * cell) + (cell / 2f);
            float circleY = optionsPositionY + cell / 2f;

            // Draw the circle:
            shapeRenderer.circle(circleX, circleY, cell / 3f);
        }

        shapeRenderer.end();
    }

    private void drawText() {
        bitmapFont.getData().setScale(2);
        String startMessage = "MASTERMIND";
        float titleWidth = bitmapFont.draw(MASTERMIND.getSpriteBatch(), startMessage, 0, 0).width;
        bitmapFont.draw(
            MASTERMIND.getSpriteBatch(),
            startMessage,
            (viewport.getWorldWidth() - titleWidth) / 2f,
            boardY + MASTERMIND_LOGIC.getMaxTries() * cell + margin * 3
        );

        if(MASTERMIND_LOGIC.getPlayerWins()) {
            String endMessage =
                MASTERMIND_LOGIC.getPlayerWins()
                    ? "GAME OVER!\nYOU WIN!"
                    : "GAME OVER!\nYOU LOST!\nBetter luck next time!";
            float endMessageWidth = bitmapFont.draw(MASTERMIND.getSpriteBatch(), endMessage, 0, 0).width;
            bitmapFont.draw(
                MASTERMIND.getSpriteBatch(),
                endMessage,
                (viewport.getWorldWidth() - endMessageWidth) / 2f,
                boardY - margin * 3
            );
        }
    }

    @Override
    public void show() {
        // Initialize screen here.
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        updateCell(); // Update cell size.
        calculatePosition();
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
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        bitmapFont.dispose();
    }
}
