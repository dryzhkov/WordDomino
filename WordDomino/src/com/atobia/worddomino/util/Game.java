package com.atobia.worddomino.util;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

public class Game {
    // Private Variables
    private int currentRunningScore;
    private int longestStreak;
    private int currentStreak;
    private int multiplier;
    private int lastMultiplierIncrease;
    private int numOfStrikesLeft;
    private Set<String> failedWords;
    private Set<String> successWords;

    // Public Variables
    public WordDictionary wd;
    public double timeLimit;
    public boolean isGameOver = false;
    public EnumGameState CurrentState;

    /* Getters */
    public int getCurrentRunningScore() {
        return currentRunningScore;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int getNumOfStrikesLeft() {
        return numOfStrikesLeft;
    }

    public Game() {
        // Initialize
        this.timeLimit = Configuration.DEFAULT_TIME_TO_WAIT;
        this.currentRunningScore = 0;
        this.longestStreak = 0;
        this.currentStreak = 0;
        this.lastMultiplierIncrease = 0;
        this.multiplier = 1;
        this.numOfStrikesLeft = Configuration.DEFAULT_NUM_OF_STRIKES;
        this.failedWords = new HashSet<String>();
        this.successWords = new HashSet<String>();
    }

    /**
     * Returns total number of points
     */
    public void updateScore(double milliSecondsToAnswer) {
        this.setGameMultiplier(false);

        double timeLeft = this.timeLimit - (milliSecondsToAnswer/100000.0);

        this.currentRunningScore += timeLeft * this.multiplier / 10.0;
        this.currentStreak++;
    }

    public boolean AddFailedWord(String word) {
        // Add to FailedWords Set
        if (word != "") {
            return failedWords.add(word);
        }
        return false;
    }

    /**
     * decrements the numOfStrikesLeft and calls GameOver if necessary
     *
     * @return numOfStrikesLeft
     */
    public int FailedAnswer() {
        // Check if we should update the longest streak
        if (this.currentStreak > this.longestStreak) {
            this.longestStreak = this.currentStreak;
        }

        // Reset the current streak
        this.currentStreak = 0;

        // Strikes
        this.numOfStrikesLeft--;

        this.setGameMultiplier(true);

        return this.numOfStrikesLeft;
    }

    public void GameOver(Context context) {
        StatsManager sm = new StatsManager(context);
        if (sm.IsInitiated()) {
            // Increment total number of games
            sm.IncrementTotalGames(Configuration.GameDifficulty);

            // High Score Update
            sm.UpdateHighestScore(this.currentRunningScore, Configuration.GameDifficulty);

            // Update the Streak with the longest one
            sm.UpdateStreak(this.longestStreak, Configuration.GameDifficulty);

            // Update the successful letters
            for (String word : successWords) {
                sm.UpdateLetterProgress(word, true);
            }

            // Update the Failed letters
            for (String word : failedWords) {
                sm.UpdateLetterProgress(word, false);
            }

            sm.SaveStats();

            this.isGameOver = true;
        }
    }

    public void decreaseTimeLimit() {
        this.timeLimit *= .75;
    }

    public void increaseTimeLimit() {
        this.timeLimit *= 1.3;
    }

    public int setGameMultiplier(boolean struckOut) {
        if (struckOut) {
            // They had a strike, reset the multiplier and make them wait another 3 turns before an increase
            this.multiplier = 1;
            lastMultiplierIncrease = 0;

            // Give them more time to think because they are a dumb
            this.increaseTimeLimit();
        } else {
            // Only increase the multiplier every 3 correct answers
            if (lastMultiplierIncrease == 3) {
                // They're doing well, decrease the time they have
                this.decreaseTimeLimit();
                increaseMultiplier();
                lastMultiplierIncrease = 0;
            } else {
                lastMultiplierIncrease++;
            }
        }
        return this.multiplier;
    }

    private void increaseMultiplier() {
        switch (this.multiplier) {
            case 1:
                this.multiplier = 2;
                break;
            case 2:
                this.multiplier = 3;
                break;
            case 3:
                this.multiplier = 6;
                break;
            case 6:
            default:
                this.multiplier = 12;
                break;
        }
    }
}