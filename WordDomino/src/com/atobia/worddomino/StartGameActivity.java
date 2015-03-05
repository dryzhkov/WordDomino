package com.atobia.worddomino;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.Util;
import com.atobia.worddomino.util.VolumeDialog;

public class StartGameActivity extends Activity implements DialogInterface.OnClickListener {
    private StartGame sg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_game);
        setVolumeControlStream(Configuration.GAME_AUDIO_STREAM);

        // Get a handle on the input text box on the StartGameActivity page
        EditText playOverText = (EditText)this.findViewById(R.id.play_over_text);

        // Delay the game if the volume is too low
        Boolean delayGame = false;

        // Determine if they are going to play over bluetooth
        if (Configuration.PlayOverBluetooth) {
            //check current volume
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int curVol = audioManager.getStreamVolume(Configuration.GAME_AUDIO_STREAM);

            if(curVol < Configuration.GAME_MIN_VOLUME){
                VolumeDialog snd = new VolumeDialog();
                snd.onAttach(this);
                snd.show(getFragmentManager(), "volume_notice");
                // Wait to start the game until they press ok
                delayGame = true;
            }

            // Hide the text box
            playOverText.setVisibility(View.INVISIBLE);
        } else {
            playOverText.setVisibility(View.VISIBLE);
        }

        if (!delayGame) {
            // Run the game, there are no issues
            this.runGame();
        }
    }

    public void runGame() {
        this.sg = new StartGame(this);
        this.sg.setBackgroundColor(Color.WHITE);
        LinearLayout layout = (LinearLayout)findViewById(R.id.rootLayout);
        layout.addView(this.sg);
    }


    // Needed for the alert dialog
    public void onClick (DialogInterface dialog, int which) {
        // The user is aware of the issue, it's time to play!
        this.runGame();

        dialog.dismiss();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.sg.startGameLoop.shouldRun = false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.sg.ListenerResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Util.SaveGame(sg.game);
    }
}
