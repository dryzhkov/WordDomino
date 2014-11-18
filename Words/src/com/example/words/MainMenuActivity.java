package com.example.words;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.words.util.Configuration;
import com.example.words.util.StatsManager;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setup here
        /*
        //SAMPLE: how to use StatsManager class
        Configuration.LoadGameDifficulty(this);
        StatsManager myStatsManager = new StatsManager(this);
        if(myStatsManager.IsInitiated()) {
            myStatsManager.IncrementTotalGames(Configuration.GameDifficulty);
            myStatsManager.UpdateHighestScore(400, Configuration.GameDifficulty);
            myStatsManager.UpdateStreak(25, Configuration.GameDifficulty);
            StatsManager.Letter myLetter = myStatsManager.GetBestLetter();
            myStatsManager.SaveStats();
        }
        */


        setContentView(R.layout.main_menu);
    }

    public void StartGame_Clicked(View view)
    {
        Intent intent = new Intent(this, StartGame.class);
        startActivity(intent);

    }

    public void NewGame_Clicked(View view)
    {
        Toast.makeText(getApplicationContext(),
                  "New Game Clicked!",
                  Toast.LENGTH_LONG).show();

    }

    public void LoadGame_Clicked(View view)
    {
        Toast.makeText(getApplicationContext(),
                  "Load Game Clicked!",
                  Toast.LENGTH_LONG).show();

    }

    public void Settings_Clicked(View view)
    {
        Toast.makeText(getApplicationContext(),
                  "Settings Clicked!",
                  Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void HighScores_Clicked(View view)
    {
        Toast.makeText(getApplicationContext(),
                  "High Scores Clicked!",
                  Toast.LENGTH_LONG).show();

    }
}
