package com.atobia.worddomino;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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
        setVolumeControlStream(Configuration.GAME_AUDIO_STREAM);

        //check current volume
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int curVol = audioManager.getStreamVolume(Configuration.GAME_AUDIO_STREAM);

        if(curVol < Configuration.GAME_MIN_VOLUME){
            //TODO: we need to handle this case somehow, displaying toast message for now
            Toast.makeText(getApplicationContext(),
                    "Game Volume is too low. ",
                    Toast.LENGTH_SHORT).show();
        }

        this.sg = new StartGame(this);
        this.sg.setBackgroundColor(Color.WHITE);
        LinearLayout layout = (LinearLayout)findViewById(R.id.rootLayout);
        layout.addView(this.sg);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.sg.startGameLoop.shouldRun = false;
        /*
        Commenting out for now because of performance hit
        this.sg.util.SaveGame(this, this.sg.game);
        */
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.sg.ListenerResult(requestCode, resultCode, data);
    }
}
