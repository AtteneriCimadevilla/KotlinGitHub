package com.example.ivanmastermind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private final MastermindGame game;
    private final GameLogic gameLogic;
    private final Array<Integer> currentGuess;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    private float cellSize = 40;
    private float feedbackSize = 15;
    private float boardX;
    private float boardY;
    private float margin = 20;

    private static final Color[] COLORS = new Color[] {
        new Color(1, 0, 0, 1),      // Red
        new Color(0, 0, 1, 1),      // Blue
        new Color(0, 1, 0, 1),      // Green
        new Color(1, 1, 0, 1),      // Yellow
        new Color(1, 0, 1, 1),      // Magenta
        new Color(1, 0.5f, 0, 1)    // Orange
    };

    public GameScreen(MastermindGame game) {
        this.game = game;
        this.gameLogic = new GameLogic();
        this.currentGuess = new Array<>();
        init();
    }

    private void init() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();

        updateCellSize();
        calculateBoardPosition();
        setupInput();
    }

    private void updateCellSize() {
        // Ajusta el tamaño de las celdas según el tamaño de la ventana
        float baseSize = Math.min(viewport.getWorldWidth(), viewport.getWorldHeight()) / 15;
        cellSize = Math.max(baseSize, 40); // Tamaño mínimo de 40
        feedbackSize = cellSize / 2.5f;   // Ajustar feedback en proporción al tamaño de la celda
    }

    private void calculateBoardPosition() {
        boardX = (viewport.getWorldWidth() - (gameLogic.getCodeLength() * cellSize)) / 2;
        boardY = (viewport.getWorldHeight() - (gameLogic.getMaxAttempts() * cellSize)) / 2;
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
        if (gameLogic.isGameOver()) return;

        // Detectar si el toque fue sobre el selector de colores
        float pickerY = boardY - margin - cellSize;
        if (y >= pickerY && y <= pickerY + cellSize) {
            float pickerX = (viewport.getWorldWidth() - (COLORS.length * cellSize)) / 2;
            float relativeX = x - pickerX;
            int colorIndex = (int)(relativeX / cellSize);

            if (colorIndex >= 0 && colorIndex < COLORS.length && currentGuess.size < gameLogic.getCodeLength()) {
                // Agregar el color a la adivinanza
                currentGuess.add(colorIndex);
                if (currentGuess.size == gameLogic.getCodeLength()) {
                    makeGuess();
                }
            }
        } else {
            // Detectar si el toque fue sobre un color ya colocado en la adivinanza
            float guessY = boardY + gameLogic.getCurrentAttempt() * cellSize;
            for (int i = 0; i < currentGuess.size; i++) {
                float colorX = boardX + i * cellSize;
                // Ajustamos el tamaño del área de toque
                if (x >= colorX && x <= colorX + cellSize &&
                    y >= guessY && y <= guessY + cellSize) {
                    // Eliminar el color tocado de la adivinanza
                    currentGuess.removeIndex(i);
                    break;  // Salir del bucle después de eliminar el color
                }
            }
        }
    }


    private void makeGuess() {
        int[] guess = new int[currentGuess.size];
        for (int i = 0; i < currentGuess.size; i++) {
            guess[i] = currentGuess.get(i);
        }
        gameLogic.submitGuess(guess);
        currentGuess.clear();
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw game board
        drawBoard();
        drawGuesses();
        drawColorPicker();

        // Draw text
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        drawText();
        game.getBatch().end();
    }

    private void drawBoard() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        shapeRenderer.rect(boardX - margin, boardY - margin,
            gameLogic.getCodeLength() * cellSize + margin * 2,
            gameLogic.getMaxAttempts() * cellSize + margin * 2);

        // Grid
        shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1);
        for (int row = 0; row < gameLogic.getMaxAttempts(); row++) {
            for (int col = 0; col < gameLogic.getCodeLength(); col++) {
                shapeRenderer.rect(
                    boardX + col * cellSize + 1,
                    boardY + row * cellSize + 1,
                    cellSize - 2,
                    cellSize - 2
                );
            }
        }

        shapeRenderer.end();
    }

    private void drawGuesses() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Previous guesses
        Array<int[]> guesses = gameLogic.getGuesses();
        Array<FeedbackResult> feedbacks = gameLogic.getFeedbackHistory();

        for (int row = 0; row < guesses.size; row++) {
            int[] guess = guesses.get(row);
            // Draw pegs
            for (int col = 0; col < guess.length; col++) {
                shapeRenderer.setColor(COLORS[guess[col]]);
                shapeRenderer.circle(
                    boardX + col * cellSize + cellSize / 2,
                    boardY + row * cellSize + cellSize / 2,
                    cellSize / 3
                );
            }

            // Draw feedback
            drawFeedback(feedbacks.get(row), row);
        }

        // Current guess
        for (int i = 0; i < currentGuess.size; i++) {
            shapeRenderer.setColor(COLORS[currentGuess.get(i)]);
            shapeRenderer.circle(
                boardX + i * cellSize + cellSize / 2,
                boardY + gameLogic.getCurrentAttempt() * cellSize + cellSize / 2,
                cellSize / 3
            );
        }

        shapeRenderer.end();
    }

    private void drawFeedback(FeedbackResult feedback, int row) {
        float x = boardX + gameLogic.getCodeLength() * cellSize + margin;
        float y = boardY + row * cellSize + cellSize / 2;

        // Exact matches (now green)
        shapeRenderer.setColor(Color.GREEN);
        for (int i = 0; i < feedback.getExactMatches(); i++) {
            shapeRenderer.circle(
                x + (i % 2) * feedbackSize,
                y + (i / 2) * feedbackSize,
                feedbackSize / 3
            );
        }

        // Color matches (now red)
        shapeRenderer.setColor(Color.RED);
        for (int i = 0; i < feedback.getColorMatches(); i++) {
            shapeRenderer.circle(
                x + ((i + feedback.getExactMatches()) % 2) * feedbackSize,
                y - ((i + feedback.getExactMatches()) / 2) * feedbackSize,
                feedbackSize / 3
            );
        }
    }

    private void drawColorPicker() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Color picker background
        float pickerWidth = COLORS.length * cellSize + margin * 2; // Ancho del bloque del selector
        float pickerX = (viewport.getWorldWidth() - pickerWidth) / 2; // Centrar horizontalmente
        float pickerY = boardY - margin - cellSize; // Debajo del tablero

        // Fondo del selector
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        shapeRenderer.rect(pickerX, pickerY - margin, pickerWidth, cellSize + margin * 2);

        // Dibujar los círculos de colores centrados
        for (int i = 0; i < COLORS.length; i++) {
            shapeRenderer.setColor(COLORS[i]);

            // Centrar cada círculo en su celda
            float circleX = pickerX + margin + (i * cellSize) + (cellSize / 2);
            float circleY = pickerY + (cellSize / 2);

            shapeRenderer.circle(circleX, circleY, cellSize / 3);
        }

        shapeRenderer.end();
    }


    private void drawText() {
        font.getData().setScale(2);
        String title = "MASTERMIND";
        float titleWidth = font.draw(game.getBatch(), title, 0, 0).width;
        font.draw(game.getBatch(), title,
            (viewport.getWorldWidth() - titleWidth) / 2,
            boardY + gameLogic.getMaxAttempts() * cellSize + margin * 3);

        if (gameLogic.isGameOver()) {
            String message = gameLogic.isGameWon() ? "You Won!" : "Game Over!";
            float messageWidth = font.draw(game.getBatch(), message, 0, 0).width;
            font.draw(game.getBatch(), message,
                (viewport.getWorldWidth() - messageWidth) / 2,
                boardY - margin * 3);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        updateCellSize(); // Recalcular el tamaño de las celdas
        calculateBoardPosition();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
