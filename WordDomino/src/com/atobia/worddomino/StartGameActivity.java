package com.atobia.worddomino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        sg.run();
    }

    @Override
    public void onStop(){super.onStop();}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.sg.ListenerResult(requestCode, resultCode, data);
    }

}
