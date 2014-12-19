package com.atobia.worddomino;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.Game;
import com.atobia.worddomino.util.MailSender;
import com.atobia.worddomino.util.SafetyNoticeDialog;
import com.atobia.worddomino.util.Util;

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
        Intent intent = new Intent(this, StartGameActivity.class);
        intent.putExtra("com.atobia.worddomino.isNewGame", true);
        startActivity(intent);
    }

    public void onBackPressed(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder
                .setTitle("Promise you'll come back!")
                .setMessage("Are you sure you want to quit?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                MainMenuActivity.this.finish();
                            }
                        }
                )
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialogBuilder.create().show();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onResume();
        Configuration.SaveSettings(this);
        super.onStop();
    }

    public void NewGame_Clicked(View view) {
        super.onResume();
        startActivity(new Intent(this, StartGameActivity.class));
    }

    public void ResumeGame_Clicked(View view) {
        Toast.makeText(getApplicationContext(), "Resume Game Clicked!",
                Toast.LENGTH_LONG).show();
        Game loadedGame = Util.LoadGame(this);
        if(loadedGame != null){
            //resume game code here
        }
    }

    public void Settings_Clicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void SendFeedBack_Clicked(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"atobiaapps@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from WordDomino.");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        try {
            startActivity(Intent.createChooser(intent, "We appreciate your feedback and suggestions."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainMenuActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
    public void Stats_Clicked(View view) {
        startActivity(new Intent(this, StatsActivity.class));
    }
}
