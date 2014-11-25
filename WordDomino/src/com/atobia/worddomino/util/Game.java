package com.atobia.worddomino.util;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

public class Game {
    // Constants
    protected static final double DEFAULT_TIME_TO_WAIT = 5000;
    protected static final int DEFAULT_NUM_OF_STRIKES = 3;

    // Private Variables
    private int currentRunningScore;
    private double timeLimit;
    private int longestStreak;
    private int currentStreak;
    private int numOfStrikesLeft;
    private Set<String> failedWords;
    private Set<String> successWords;
    private int diffLevel;
    private Context context;

    /**
     * @param c
     */
    public Game(Context c) {
        // Initialize
        this.timeLimit = DEFAULT_TIME_TO_WAIT;
        this.currentRunningScore = 0;
        this.longestStreak = 0;
        this.currentStreak = 0;
        this.numOfStrikesLeft = DEFAULT_NUM_OF_STRIKES;
        this.failedWords = new HashSet<String>();
        this.successWords = new HashSet<String>();

        this.context = c;
    }

    /**
     * Returns total number of points
     * 
     * @param int points
     * @return integer
     */
    public int updateScore(double milliSecondsToAnswer) {
        if (milliSecondsToAnswer < 0) {
            return -1;
        }

        // Only update if they haven't passed the time limit
        if (milliSecondsToAnswer < this.timeLimit) {
            this.currentRunningScore += milliSecondsToAnswer;
            this.currentStreak++;
        } else {
            // Incorrect answer: We don't have a word that they failed on, they
            // didn't answer in the allotted time
            FailedAnswer();
        }

        return this.currentRunningScore;
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

        return this.numOfStrikesLeft;
    }

    public void GameOver() {
        StatsManager sm = new StatsManager(this.context);
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
        }
    }

    public double decreaseTimeLimit() {
        this.timeLimit *= .75;
        return this.timeLimit;
    }
}
