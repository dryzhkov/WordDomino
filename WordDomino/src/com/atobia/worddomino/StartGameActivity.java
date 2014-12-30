package com.atobia.worddomino;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.SafetyNoticeDialog;

import java.util.ArrayList;

public class StartGameActivity extends Activity {
    private StartGame sg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_game);
    }

    @Override
    public void onStart(){
        super.onStart();
        this.sg = new StartGame(this);
        this.sg.setBackgroundColor(Color.WHITE);
        LinearLayout layout = (LinearLayout)findViewById(R.id.rootLayout);
        layout.addView(this.sg);
    }

    @Override
    public void onBackPressed(){
        this.sg.util.SaveGame(this, this.sg.game);
    }

    @Override
    public void onStop(){super.onStop();}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.sg.ListenerResult(requestCode, resultCode, data);
    }
}
