package com.example.words;

import com.example.words.util.Configuration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

public class SettingsActivity extends Activity {
	private final String PREFERENCES_KEY = "WORDS_APP_PREF_FILE";
	private final String GAME_DIFF_KEY = "GAME_DIFFICULTY";
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
		SharedPreferences settings = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		Configuration.GameDifficulty = settings.getInt(GAME_DIFF_KEY, Configuration.DifficultyLevel.EASY);
		
		if(Configuration.GameDifficulty == Configuration.DifficultyLevel.HARD){
			radioButtonGroup.check(R.id.rb_gamediff_hard);
		}else if(Configuration.GameDifficulty == Configuration.DifficultyLevel.MEDIUM){
			radioButtonGroup.check(R.id.rb_gamediff_medium);
		}else{ //default: easy
			radioButtonGroup.check(R.id.rb_gamediff_easy);
		}
	}
	
	private void SaveGameDifficulty(){
		int selectedOption = radioButtonGroup.getCheckedRadioButtonId();
		if(selectedOption == 2){
			Configuration.GameDifficulty = Configuration.DifficultyLevel.HARD;
		}else if(selectedOption == 1){
			Configuration.GameDifficulty = Configuration.DifficultyLevel.MEDIUM;
		}else{ //default: easy
			Configuration.GameDifficulty = Configuration.DifficultyLevel.EASY;
		}
		SharedPreferences settings = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(GAME_DIFF_KEY, Configuration.GameDifficulty);
		editor.commit();
	}
}
