package com.example.ivanmastermind;

import com.badlogic.gdx.utils.Array;
import java.util.Arrays;
import java.util.Random;

public class GameLogic {
    private static final int CODE_LENGTH = 4;
    private static final int NUM_COLORS = 6;
    private static final int MAX_ATTEMPTS = 10;

    private int[] secretCode;
    private boolean gameWon;
    private int currentAttempt;
    private Array<int[]> guesses;
    private Array<FeedbackResult> feedbackHistory;

    public GameLogic() {
        secretCode = new int[CODE_LENGTH];
        guesses = new Array<>();
        feedbackHistory = new Array<>();
        initializeGame();
    }

    public void initializeGame() {
        generateSecretCode();
        gameWon = false;
        currentAttempt = 0;
        guesses.clear();
        feedbackHistory.clear();
    }

    private void generateSecretCode() {
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            secretCode[i] = random.nextInt(NUM_COLORS);
        }
    }

    public FeedbackResult submitGuess(int[] guess) {
        if (guess.length != CODE_LENGTH) {
            throw new IllegalArgumentException("Guess must be " + CODE_LENGTH + " colors");
        }

        int[] guessCopy = Arrays.copyOf(guess, CODE_LENGTH);
        guesses.add(guessCopy);

        currentAttempt++;
        FeedbackResult feedback = checkGuess(guess);
        feedbackHistory.add(feedback);

        gameWon = (feedback.getExactMatches() == CODE_LENGTH);
        return feedback;
    }

    private FeedbackResult checkGuess(int[] guess) {
        int exactMatches = 0;
        int colorMatches = 0;

        boolean[] usedGuess = new boolean[CODE_LENGTH];
        boolean[] usedCode = new boolean[CODE_LENGTH];

        // Check exact matches first
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (guess[i] == secretCode[i]) {
                exactMatches++;
                usedGuess[i] = true;
                usedCode[i] = true;
            }
        }

        // Check color matches (right color, wrong position)
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (!usedGuess[i]) {
                for (int j = 0; j < CODE_LENGTH; j++) {
                    if (!usedCode[j] && guess[i] == secretCode[j]) {
                        colorMatches++;
                        usedCode[j] = true;
                        break;
                    }
                }
            }
        }

        return new FeedbackResult(exactMatches, colorMatches);
    }

    // Getters
    public boolean isGameWon() { return gameWon; }
    public boolean isGameOver() { return gameWon || currentAttempt >= MAX_ATTEMPTS; }
    public int getCurrentAttempt() { return currentAttempt; }
    public Array<int[]> getGuesses() { return guesses; }
    public Array<FeedbackResult> getFeedbackHistory() { return feedbackHistory; }
    public int getMaxAttempts() { return MAX_ATTEMPTS; }
    public int getCodeLength() { return CODE_LENGTH; }

    // For debugging or when game is over
    public int[] getSecretCode() { return secretCode; }
}

class FeedbackResult {
    private final int exactMatches;  // GREEN pegs
    private final int colorMatches;  // RED pegs

    public FeedbackResult(int exactMatches, int colorMatches) {
        this.exactMatches = exactMatches;
        this.colorMatches = colorMatches;
    }

    public int getExactMatches() { return exactMatches; }
    public int getColorMatches() { return colorMatches; }
}
