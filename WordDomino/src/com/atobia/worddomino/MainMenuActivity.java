package com.atobia.worddomino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.SafetyNoticeDialog;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        //hide/show resume button
        Button btnResume = (Button)findViewById(R.id.btnResumeGame);
        btnResume.setEnabled(Configuration.SavedGameExists);

        Configuration.LoadSettings(this);
        if(Configuration.ShowSafetyScreen){
            SafetyNoticeDialog snd = new SafetyNoticeDialog();
            snd.show(getFragmentManager(), "safety_notice");
        }
    }

    public void StartGame_Clicked(View view) {
        Intent intent = new Intent(this, StartGame.class);
        intent.putExtra("com.atobia.worddomino.isNewGame", true);
        startActivity(intent);
    }

    public void onBackPressed(){
        MainMenuActivity.this.finish();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        Configuration.SaveSettings(this);
        super.onStop();
    }

    public void NewGame_Clicked(View view) {
        Intent intent = new Intent(this, StartGame.class);
        startActivity(intent);
    }

    public void LoadGame_Clicked(View view) {
        Intent intent = new Intent(this, StartGame.class);
        intent.putExtra("com.atobia.worddomino.isNewGame", false);
        startActivity(intent);
    }

    public void Settings_Clicked(View view) {
        //Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent);
        GameView gv = new GameView(this);
        setContentView(gv);
    }

    public void Stats_Clicked(View view) {
        startActivity(new Intent(this, StatsActivity.class));
    }
}
