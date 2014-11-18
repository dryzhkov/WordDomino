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
import java.util.Map;
import com.google.gson.Gson;
/**
 * Created by dima on 11/9/2014.
 */
public class StatsManager {
    private static final String FILE_NAME = "statistics.xml";
    private static final String DIR_NAME = "data";
    private PlayerStats playerStats;
    private Context context;

    public StatsManager(Context c){
        this.context = c;
        //load player stats
        this.LoadStats();
    }

    public boolean IsInitiated(){
        return (this.playerStats != null);
    }

    public void SaveStats() {
        String data = new Gson().toJson(this.playerStats);
        try {
            File target = EnsureFileExists();
            FileOutputStream outputStream = new FileOutputStream(target);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("StatsManager::SaveStats::Ex", e.toString());
        }
    }

    public void UpdateStreak(int value, int diffLevel){
        if(value < 0){
            return;
        }
        GameRecord record = this.playerStats.gameRecords.get(diffLevel);
        int curHighestStreak = record.GetStreak();
        if(curHighestStreak < value){
            record.SetStreak(value);
        }
    }

    public int GetLongestStreak(int diffLevel){
        return this.playerStats.gameRecords.get(diffLevel).GetStreak();
    }

    public void UpdateHighestScore(int value, int diffLevel){
        if(value < 0){
            return;
        }
        GameRecord record = this.playerStats.gameRecords.get(diffLevel);
        int curHighestScore = record.GetHighestScore();
        if(curHighestScore < value){
            record.SetHighestScore(value);
        }
    }

    public int GetHighestScore(int diffLevel){
        return this.playerStats.gameRecords.get(diffLevel).GetHighestScore();
    }

    public void IncrementTotalGames(int diffLevel){
        GameRecord record = this.playerStats.gameRecords.get(diffLevel);
        record.SetTotalGames(record.GetTotalGames() + 1);
    }

    public int GetTotalGames(int diffLevel){
        return this.playerStats.gameRecords.get(diffLevel).GetTotalGames();
    }

    public void UpdateLetterProgress(String word, boolean playerSuccess){
        if(word == null || word.length() == 0){
            return;
        }
        char firstChar = word.toLowerCase().charAt(0);
        Letter letter = this.playerStats.letterProgress[Util.AtoI(firstChar)];
        //increment total count by 1
        letter.SetTotalCount(letter.GetTotalCount() + 1);
        if(playerSuccess){
            //player answer was correct, increment success count by 1
            letter.SetSuccessCount(letter.GetSuccessCount() + 1);
        }
    }

    public Letter GetBestLetter(){
        Letter[] temp = this.playerStats.letterProgress;
        //sort letter array by total count
        Arrays.sort(temp);
        //select best ratio from top 20%
        int lastIndex = temp.length - temp.length / 5;
        int bestIndex = temp.length - 1;
        double bestRatio = 0;
        for(int i = temp.length - 1; i >= lastIndex; i--){
            Letter curLetter = temp[i];
            double curRatio = curLetter.GetSuccessRatio();
            if(curRatio > bestRatio){
                bestRatio = curRatio;
                bestIndex = i;
            }
        }
        return bestRatio > 0 ? temp[bestIndex] : null;
    }

    private void LoadStats(){
        try{
            StringBuilder data = new StringBuilder();
            File target = EnsureFileExists();
            InputStream inputStream = new FileInputStream(target);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while(line != null){
                if(!line.isEmpty()){
                    data.append(line);
                }
                line = reader.readLine();
            }

            if(data.length() > 0) {
                this.playerStats =  new Gson().fromJson(data.toString(), PlayerStats.class);
            }else{ //empty file
                this.playerStats =  new PlayerStats();
            }
        }catch(Exception ioex){
            Log.e("StatsManager::SaveStats::Ex", ioex.toString());
            this.playerStats =  null;
        }
    }

    private File EnsureFileExists() throws IOException {
        File targetDir = this.context.getDir(DIR_NAME, Context.MODE_PRIVATE);
        File targetFile = new File(targetDir, FILE_NAME);
        //targetFile.delete();
        if(!targetFile.exists()) {
            targetFile.createNewFile();
        }
        return targetFile;
    }

    protected class PlayerStats{
        private Map<Integer, GameRecord> gameRecords;
        private Letter[] letterProgress;
        private static final int LETTER_COUNT = 26;
        public PlayerStats(){
            //initialize game records
            this.gameRecords = new HashMap<Integer, GameRecord>();
            this.gameRecords.put(Configuration.DifficultyLevel.EASY, new GameRecord());
            this.gameRecords.put(Configuration.DifficultyLevel.MEDIUM, new GameRecord());
            this.gameRecords.put(Configuration.DifficultyLevel.HARD, new GameRecord());
            //initialize letter progress
            this.letterProgress = new Letter[LETTER_COUNT];
            for(int i = 0; i < LETTER_COUNT; i++){
                this.letterProgress[i] = new Letter(Util.ItoA(i));
            }
        }
    }

    protected class GameRecord {
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

    public class Letter implements Comparable<Letter> {
        private char character;
        private int successCount;
        private int totalCount;

        public Letter(char c){
            this.character = c;
        }

        public char GetCharacter(){
            return this.character;
        }

        public int GetSuccessCount(){
            return this.successCount;
        }

        public void SetSuccessCount(int i){
            this.successCount = i;
        }

        public int GetTotalCount(){
            return this.totalCount;
        }

        public void SetTotalCount(int i){
            this.totalCount = i;
        }

        public double GetSuccessRatio(){
            if(this.totalCount == 0){
                return 0d;
            }
            //round to 2 decimals
            return (double)Math.round((double)this.successCount / this.totalCount * 100) / 100;
        }

        @Override
        public int compareTo(Letter other){
            return this.totalCount - other.totalCount;
        }
    }
}
