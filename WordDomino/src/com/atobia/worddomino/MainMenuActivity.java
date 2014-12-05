package com.atobia.worddomino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.atobia.worddomino.util.Configuration;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        //hide/show resume button
        Button btnResume = (Button)findViewById(R.id.btnResumeGame);
        btnResume.setEnabled(Configuration.SavedGameExists);
    }

    public void StartGame_Clicked(View view) {
        Intent intent = new Intent(this, StartGame.class);
        intent.putExtra("com.atobia.worddomino.isNewGame", true);
        startActivity(intent);
    }

    public void NewGame_Clicked(View view) {
        Toast.makeText(getApplicationContext(), "New Game Clicked!",
                Toast.LENGTH_LONG).show();

    }

    public void LoadGame_Clicked(View view) {
        Intent intent = new Intent(this, StartGame.class);
        intent.putExtra("com.atobia.worddomino.isNewGame", false);
        startActivity(intent);
    }

    public void Settings_Clicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void Stats_Clicked(View view) {
        startActivity(new Intent(this, StatsActivity.class));
    }
}
