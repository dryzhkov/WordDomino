package com.atobia.worddomino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }

    public void StartGame_Clicked(View view) {
        Intent intent = new Intent(this, StartGame.class);
        startActivity(intent);

    }

    public void NewGame_Clicked(View view) {
        Toast.makeText(getApplicationContext(), "New Game Clicked!",
                Toast.LENGTH_LONG).show();

    }

    public void LoadGame_Clicked(View view) {
        Toast.makeText(getApplicationContext(), "Load Game Clicked!",
                Toast.LENGTH_LONG).show();

    }

    public void Settings_Clicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void Stats_Clicked(View view) {
        startActivity(new Intent(this, StatsActivity.class));
    }
}
