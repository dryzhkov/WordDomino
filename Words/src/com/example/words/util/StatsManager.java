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
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
/**
 * Created by dima on 11/9/2014.
 */
public class StatsManager {
    private final String fileName = "statistics.xml";
    private final String dirName = "data";
    private PlayerStats playerStats;
    private Context context;
    /*
        TODO:
        Best / Worst letter: * Take top 20% of the whole game letters by appearance count and
        then from there select the best ratio from within that letter. Ex: if a shows up 100 time, B 99, C 98 ....
        and x was 1. Then x gets ruled out and of the ones that are left, we select the best one of those letters as it is compared to it's own kind. B had 50 successful out of 98 B letter words.
     */
    public StatsManager(Context c){
        this.context = c;
        //load player stats
        this.LoadStats();
    }

    public boolean IsValid(){
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

    public void IncrementTotalGames(int diffLevel){
        GameRecord record = this.playerStats.gameRecords.get(diffLevel);
        record.SetTotalGames(record.GetTotalGames() + 1);
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
        File targetDir = this.context.getDir(this.dirName, Context.MODE_PRIVATE);
        File targetFile = new File(targetDir, fileName);
        //targetFile.delete();
        if(!targetFile.exists()) {
            targetFile.createNewFile();
        }
        return targetFile;
    }

    protected class PlayerStats{
        private Map<Integer, GameRecord> gameRecords;
        private Letter[] letterProgress;
        private static final int letterCount = 26;
        public PlayerStats(){
            //initialize game records
            this.gameRecords = new HashMap<Integer, GameRecord>();
            this.gameRecords.put(Configuration.DifficultyLevel.EASY, new GameRecord());
            this.gameRecords.put(Configuration.DifficultyLevel.MEDIUM, new GameRecord());
            this.gameRecords.put(Configuration.DifficultyLevel.HARD, new GameRecord());
            //initialize letter progress
            this.letterProgress = new Letter[letterCount];
            for(int i = 0; i < letterCount; i++){
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

    protected class Letter {
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

        public double GetSuccessRation(){
            if(this.totalCount == 0){
                return 0d;
            }
            //round to 2 decimals
            return Math.round(this.successCount / this.totalCount * 100) / 100;
        }
    }
}
