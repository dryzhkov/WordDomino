package com.example.words;

import com.example.words.util.Configuration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

public class SettingsActivity extends Activity {
    private RadioGroup radioButtonGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        radioButtonGroup = (RadioGroup)findViewById(R.id.radioGroupGameDiff);
        LoadGameDifficulty();
    }

    @Override
    protected void onStop(){
        super.onStop();
        SaveGameDifficulty();
    }

    public void btnSave_Clicked(View view)
    {
        SaveGameDifficulty();
        finish();
    }

    private void LoadGameDifficulty(){
        Configuration.LoadGameDifficulty(this);

        if(Configuration.GameDifficulty == Configuration.DifficultyLevel.HARD){
            radioButtonGroup.check(R.id.rb_gamediff_hard);
        }else if(Configuration.GameDifficulty == Configuration.DifficultyLevel.MEDIUM){
            radioButtonGroup.check(R.id.rb_gamediff_medium);
        }else{ //default: easy
            radioButtonGroup.check(R.id.rb_gamediff_easy);
        }
    }

    private void SaveGameDifficulty(){
        int selectedOptionId = radioButtonGroup.getCheckedRadioButtonId();
        View selectedButton = radioButtonGroup.findViewById(selectedOptionId);
        int selectedIndex = radioButtonGroup.indexOfChild(selectedButton);
        if(selectedIndex == 2){
            Configuration.GameDifficulty = Configuration.DifficultyLevel.HARD;
        }else if(selectedIndex == 1){
            Configuration.GameDifficulty = Configuration.DifficultyLevel.MEDIUM;
        }else{ //default: easy
            Configuration.GameDifficulty = Configuration.DifficultyLevel.EASY;
        }
        Configuration.SaveGameDifficulty(this, Configuration.GameDifficulty);
    }
}
