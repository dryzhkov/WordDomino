package com.example.words.util;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.words.util.Configuration.DifficultyLevel;
import com.google.gson.Gson;

public class Game {
    // Constants
    protected static final int DEFAULT_TIME_TO_WAIT = 5000;
    protected static final int DEFAULT_NUM_OF_STRIKES = 3;

    // Private Variables
    private int currentRunningScore;
    private double timeLimit;
    private int longestStreak;
    private int currentStreak;
    private int numOfStrikesLeft;
    private Set<String> FailedWords;
    private Set<String> SuccessWords;
    private int diffLevel;
    private Context context;

    /**
     * @param c
     * @param difficulty
     */
    public Game(Context c, int difficulty) {
        // Initialize
        this.timeLimit = DEFAULT_TIME_TO_WAIT;
        this.currentRunningScore = 0;
        this.longestStreak = 0;
        this.currentStreak = 0;
        this.numOfStrikesLeft = DEFAULT_NUM_OF_STRIKES;
        this.FailedWords = new HashSet<String>();
        this.SuccessWords = new HashSet<String>();

        this.context = c;
        this.diffLevel = difficulty;
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
            return FailedWords.add(word);
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

    public boolean GameOver() {
        StatsManager sm = new StatsManager(this.context);
        if (sm.IsInitiated()) {
            // Increment total number of games
            sm.IncrementTotalGames(this.diffLevel);

            // High Score Update
            sm.UpdateHighestScore(this.currentRunningScore, this.diffLevel);

            // Update the Streak with the longest one
            sm.UpdateStreak(this.longestStreak, this.diffLevel);

            // Update the successful letters
            for (String word : SuccessWords) {
                sm.UpdateLetterProgress(word, true);
            }

            // Update the Failed letters
            for (String word : FailedWords) {
                sm.UpdateLetterProgress(word, false);
            }

            sm.SaveStats();
        }

        return true;
    }

    public double decreaseTimeLimit() {
        this.timeLimit *= .75;
        return this.timeLimit;
    }
}
