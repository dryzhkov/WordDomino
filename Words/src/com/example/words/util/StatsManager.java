package com.example.words.util;

import com.example.words.util.Configuration.DifficultyLevel;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dima on 11/9/2014.
 */
public class StatsManager {
    private String fileName = "words_statistics.xml";
    private Context context;
    private Map<Integer, PlayerStats> letterProgress;
    /*
        TO-DO:
        Streak per game difficulty
        High score per game difficulty
        Number of games played per game difficulty
    */

    /*
        TO-DO:
        Best/ Worst letter: * Take top 20% of the whole game letters by appearance count and then from there select the best ratio from within that letter. Ex: if a shows up 100 time, B 99, C 98 ....
        and x was 1. Then x gets ruled out and of the ones that are left, we select the best one of those letters as it is compared to it's own kind. B had 50 successful out of 98 B letter words.
     */
    public StatsManager(Context c){
        this.context = c;
        this.letterProgress = new HashMap<Integer, PlayerStats>();

    }
    public void Save(){
        String data = "";
        try {
            FileOutputStream outputStream = this.context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Load(){
        /*
            TO-DO: Read / Write stats to a life, convert from xml to object and back
         */
        //http://developer.android.com/training/basics/network-ops/xml.html
        //http://developer.android.com/training/basics/data-storage/files.html

        PlayerStats gameStats = new PlayerStats();
        gameStats.SetHighestScore(0);
        gameStats.SetStreak(0);
        gameStats.SetTotalGames(0);
        this.letterProgress.put(DifficultyLevel.EASY, gameStats);
    }


    public class PlayerStats {
        private int longestStreak;
        private int highestScore;
        private int totalGamesPlayed;

        public int GetStreak(){
            return this.longestStreak;
        }

        public void SetStreak(int c){
            this.longestStreak = c;
        }

        public int GetHighestScore(){
            return this.highestScore;
        }

        public void SetHighestScore(int i){
            this.highestScore = i;
        }

        public int GetTotalGames(){
            return this.totalGamesPlayed;
        }

        public void SetTotalGames(int i){
            this.totalGamesPlayed = i;
        }
    }
}
