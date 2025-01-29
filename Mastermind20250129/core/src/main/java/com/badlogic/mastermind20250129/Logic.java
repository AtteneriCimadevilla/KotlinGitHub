package com.badlogic.mastermind20250129;

import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Logic {

    private static final int OPTIONS = 8;
    private static final int PASSWORD_LENGTH = 4;
    private static final int MAX_TRIES = 10;

    private int[] password;
    private int currentTry;
    private boolean playerWins;
    private Array<int[]> tries;
    private Array<Feedback> feedbackLog;

    public Logic() {
        password = new int[PASSWORD_LENGTH];
        currentTry = 0;
        playerWins = false;
        tries = new Array<int[]>();
        feedbackLog = new Array<Feedback>();

        // Generate password
        Random random = new Random();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password[i] = random.nextInt(OPTIONS);
        }

        tries.clear();
        feedbackLog.clear();
    }

    public Feedback getFeedback(int[] attempt) {
        int positionCorrect = 0;
        int positionIncorrect = 0;

        boolean[] usedAttempt = new boolean[PASSWORD_LENGTH];
        boolean[] usedPassword = new boolean[PASSWORD_LENGTH];

        // Check correct position
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            if (attempt[i] == password[i]) {
                positionCorrect++;
                usedAttempt[i] = true;
                usedPassword[i] = true;
            }
        }

        // Check right digits in wrong position
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            if (!usedAttempt[i]) {
                for (int j = 0; j < PASSWORD_LENGTH; j++) {
                    if (!usedPassword[j] && attempt[i] == password[j]) {
                        positionIncorrect++;
                        usedPassword[j] = true;
                        break;
                    }
                }
            }
        }

        return new Feedback(positionCorrect, positionIncorrect);
    }

    public boolean getPlayerWins() { return playerWins; }
    public boolean playerLoses() { return currentTry >= MAX_TRIES; }
    public int getCurrentTry() { return currentTry; }
    public Array<int[]> getTries() { return tries; }
    public Array<Feedback> getFeedbackLog() { return feedbackLog; }
    public int getMaxTries() { return MAX_TRIES; }
    public int getPasswordLength() { return PASSWORD_LENGTH; }
    public int getOptions() { return OPTIONS; }

    public int[] getPassword() { return password; }
}

class Feedback {
    private final int positionCorrect;  // Number of correct digits in the correct position.
    private final int positionIncorrect; // Number of correct digits in the incorrect position.

    public Feedback(int positionCorrect, int positionIncorrect) {
        this.positionCorrect = positionCorrect;
        this.positionIncorrect = positionIncorrect;
    }

    public int getPositionCorrect() { return positionCorrect; }
    public int getPositionIncorrect() { return positionIncorrect; }
}
