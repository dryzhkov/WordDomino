package com.example.words;

import com.example.words.util.Configuration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

public class SettingsActivity extends Activity {
    private RadioGroup rgDifficulty;
    private RadioGroup rgFirstPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rgDifficulty = (RadioGroup)findViewById(R.id.radioGroupGameDiff);
        rgFirstPlayer = (RadioGroup)findViewById(R.id.radioGroupFirstPlayer);
        LoadSettings();
    }

    @Override
    protected void onStop(){
        super.onStop();
        SaveSettings();
    }

    public void btnSave_Clicked(View view)
    {
        SaveSettings();
        finish();
    }

    private void LoadSettings(){
        Configuration.LoadSettings(this);

        //set game difficulty
        if(Configuration.GameDifficulty == Configuration.DifficultyLevel.HARD){
            rgDifficulty.check(R.id.rb_gamediff_hard);
        }else if(Configuration.GameDifficulty == Configuration.DifficultyLevel.MEDIUM){
            rgDifficulty.check(R.id.rb_gamediff_medium);
        }else{ //default: easy
            rgDifficulty.check(R.id.rb_gamediff_easy);
        }

        //set first player
        if(Configuration.FirstPlayer == Configuration.PlayerType.Player){
            rgFirstPlayer.check(R.id.rb_firstplayer_player);
        }else{ //default: AI
            rgFirstPlayer.check(R.id.rb_firstplayer_ai);
        }
    }

    private void SaveSettings(){
        //get game difficulty
        int selectedOptionId = rgDifficulty.getCheckedRadioButtonId();
        View selectedButton = rgDifficulty.findViewById(selectedOptionId);
        int selectedIndex = rgDifficulty.indexOfChild(selectedButton);
        if(selectedIndex == 2){
            Configuration.GameDifficulty = Configuration.DifficultyLevel.HARD;
        }else if(selectedIndex == 1){
            Configuration.GameDifficulty = Configuration.DifficultyLevel.MEDIUM;
        }else{ //default: easy
            Configuration.GameDifficulty = Configuration.DifficultyLevel.EASY;
        }

        selectedOptionId = rgFirstPlayer.getCheckedRadioButtonId();
        selectedButton = rgFirstPlayer.findViewById(selectedOptionId);
        selectedIndex = rgFirstPlayer.indexOfChild(selectedButton);

        if(selectedIndex == 1){
            Configuration.FirstPlayer = Configuration.PlayerType.Player;
        }else{ //default: AI
            Configuration.FirstPlayer = Configuration.PlayerType.AI;
        }

        Configuration.SaveSettings(this);
    }
}
