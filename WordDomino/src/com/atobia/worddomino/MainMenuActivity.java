package com.atobia.worddomino;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.atobia.worddomino.util.AchievementManager;
import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.EnumGameState;
import com.atobia.worddomino.util.Game;
import com.atobia.worddomino.util.SafetyNoticeDialog;
import com.atobia.worddomino.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

public class MainMenuActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "MainMenu";
    private static final int RC_SIGN_IN = 9001;
    private static final String DEV_EMAIL = "atobiaapps@gmail.com";
    private static final String SNAP_SHOT_NAME = "Snapshot_0";
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private boolean mIsResolving = false;
    private Button btnResume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        setVolumeControlStream(Configuration.GAME_AUDIO_STREAM);
        Configuration.LoadSettings(this);

        btnResume = (Button)findViewById(R.id.btnResumeGame);

        // Create the Google API Client with access to Plus, Games, and Drive
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .build();

        Configuration.GMSClient = mGoogleApiClient;
        AchievementManager.init(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        //hide/show resume button
        btnResume.setEnabled(Configuration.SavedGameExists);

        showProgressDialog("Signing in.");
        Log.d(TAG, "onStart(): connecting");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();

        Configuration.SaveSettings(this);
        Log.d(TAG, "onStop(): disconnecting");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");
        dismissProgressDialog();

        if(Configuration.ShowSafetyScreen){
            SafetyNoticeDialog snd = new SafetyNoticeDialog();
            snd.show(getFragmentManager(), "safety_notice");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        if(!mIsResolving) {
            mIsResolving = resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dismissProgressDialog();
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "SIGN_IN:Success");
                // Sign-in was successful, connect the API Client
                if(!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else {
                Log.d(TAG, "SIGN_IN:Failure");
            }
        }
    }

    public void NewGame_Clicked(View view) {
        Intent intent = new Intent(this, StartGameActivity.class);
        intent.putExtra("com.atobia.worddomino.isNewGame", true);
        //TODO: I suggest we replace the intent.putExtra with just having a global variable for a loaded game.
        //TODO: null means new game
        Configuration.LoadedGame = null;
        startActivity(intent);
    }

    public void onBackPressed(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder
                .setTitle("Quit Game")
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

    public void ResumeGame_Clicked(View view) {
        //attempt to load saved game
        PendingResult<Snapshots.OpenSnapshotResult> pendingResult = Games.Snapshots.open(
                mGoogleApiClient, SNAP_SHOT_NAME, false);

        showProgressDialog("Loading Saved Game");
        ResultCallback<Snapshots.OpenSnapshotResult> callback =
                new ResultCallback<Snapshots.OpenSnapshotResult>() {
                    @Override
                    public void onResult(Snapshots.OpenSnapshotResult openSnapshotResult) {
                        if (openSnapshotResult.getStatus().isSuccess()) {
                            byte[] data = new byte[0];
                            try {
                                data = openSnapshotResult.getSnapshot().getSnapshotContents().readFully();
                                Configuration.LoadedGame = Util.BytesToGame(data);
                                if(Configuration.LoadedGame != null){
                                    //start loaded game
                                    Intent intent = new Intent(MainMenuActivity.this, StartGameActivity.class);
                                    startActivity(intent);
                                }else{
                                    //there was an issue with serializing Game object
                                    Log.d(TAG, "Error converting bytes to game.");
                                }
                            } catch (IOException e) {
                                Log.e(TAG, e.toString());
                            }
                        } else {
                            //display error
                            Log.d(TAG, "Could not load game snapshot");
                        }

                        dismissProgressDialog();
                    }
                };
        pendingResult.setResultCallback(callback);
    }

    public void Settings_Clicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void SendFeedBack_Clicked(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{DEV_EMAIL});
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

    private void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private boolean resolveConnectionFailure(Activity activity, GoogleApiClient client, ConnectionResult result, int requestCode) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(activity, requestCode);
                return true;
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                client.connect();
                return false;
            }
        } else {
            // not resolvable... so show an error message
            int errorCode = result.getErrorCode();
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                    activity, requestCode);
            if (dialog != null) {
                dialog.show();
            }
            return false;
        }
    }

    public void TestSave_Clicked(View view) {
        Game testGame = new Game();
        testGame.CurrentState = EnumGameState.NEW_GAME;
        Util.SaveGame(this, testGame);
        AchievementManager.Achievements.PRIME_ACCOMPLISHED = true;
        AchievementManager.pushAchievements();
    }
}
