package com.atobia.worddomino;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.StatsManager;

import java.util.Map;
import java.util.HashMap;


public class StatsActivity extends Activity {
    private int[] gameDifficultyId;
    private String[] gameDifficultyName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        gameDifficultyId = new int[] {
            Configuration.DifficultyLevel.EASY,
            Configuration.DifficultyLevel.MEDIUM,
            Configuration.DifficultyLevel.HARD};

        gameDifficultyName = new String[] {
            "Easy",
            "Medium",
            "Hard"};

        populateStats();
    }

    private void populateStats(){
        StatsManager statsManager = new StatsManager(this);
        if(statsManager.IsInitiated()){
            TableLayout totalGames = (TableLayout)findViewById(R.id.tblTotalGames);
            TableLayout highestScore = (TableLayout)findViewById(R.id.tblHighestScores);
            TableLayout longestStreaks = (TableLayout)findViewById(R.id.tblLongestStreaks);
            TextView bestLetterView = (TextView)findViewById(R.id.tvBestLetter);
            TableRow newRow = null;
            TextView newView = null;
            for(int i = 0; i < gameDifficultyId.length; i++){
                //create new row for total games
                newRow = new TableRow(this);
                newRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
                //column 1: difficulty name
                newView = new TextView(this);
                newView.setText(gameDifficultyName[i]);
                newRow.addView(newView);
                //column 2: number of games played
                newView = new TextView(this);
                newView.setText(String.valueOf(statsManager.GetTotalGames(gameDifficultyId[i])));
                newRow.addView(newView);
                //add row to the total games table
                totalGames.addView(newRow, i);

                //create new row for highest scores
                newRow = new TableRow(this);
                newRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
                //column 1: difficulty name
                newView = new TextView(this);
                newView.setText(gameDifficultyName[i]);
                newRow.addView(newView);
                //column 2: score
                newView = new TextView(this);
                newView.setText(String.valueOf(statsManager.GetHighestScore(gameDifficultyId[i])));
                newRow.addView(newView);
                //add row to the highest score table
                highestScore.addView(newRow, i);

                //create new row for longest streaks
                newRow = new TableRow(this);
                newRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
                //column 1: difficulty name
                newView = new TextView(this);
                newView.setText(gameDifficultyName[i]);
                newRow.addView(newView);
                //column 2: streak value
                newView = new TextView(this);
                newView.setText(String.valueOf(statsManager.GetLongestStreak(gameDifficultyId[i])));
                newRow.addView(newView);
                //add row to the longest streak table
                longestStreaks.addView(newRow, i);
            }

            StatsManager.Letter bestLetter = statsManager.GetBestLetter();
            if(bestLetter != null){
                bestLetterView.setText(
                        String.format("Your best letter is '%s' with the current success ration of percent %.2f percent.",
                                bestLetter.GetCharacter(),
                                bestLetter.GetSuccessRatio() * 100));
            }
            //TODO: if null put place holder text or hide the whole thing
        }
    }


    /*private void TestUsage(){
        Configuration.LoadSettings(this);
        StatsManager myStatsManager = new StatsManager(this);
        if(myStatsManager.IsInitiated()) {
            myStatsManager.IncrementTotalGames(Configuration.GameDifficulty);
            myStatsManager.UpdateHighestScore(400, Configuration.GameDifficulty);
            myStatsManager.UpdateStreak(25, Configuration.GameDifficulty);


            myStatsManager.UpdateLetterProgress("seattle", true);
            myStatsManager.UpdateLetterProgress("seattle", false);
            myStatsManager.UpdateLetterProgress("seattle", false);
            myStatsManager.UpdateLetterProgress("seattle", false);
            myStatsManager.UpdateLetterProgress("seattle", false);
            myStatsManager.UpdateLetterProgress("amega", true);
            myStatsManager.UpdateLetterProgress("amega", true);
            myStatsManager.UpdateLetterProgress("amega", false);
            myStatsManager.UpdateLetterProgress("amega", false);
            myStatsManager.UpdateLetterProgress("xmen", true);
            myStatsManager.UpdateLetterProgress("amega", false);
            myStatsManager.SaveStats();
            StatsManager.Letter myLetter = myStatsManager.GetBestLetter();
        }

    }*/
}
