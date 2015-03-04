package com.atobia.worddomino.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.atobia.worddomino.StartGameActivity;

public class GameOverDialog extends DialogFragment {
    public Boolean wonGame;
    private StartGameActivity sga;

    private final String ALERT_TITLE = "Game Over";
    private final String ALERT_MESSAGE_WON = "Congratulations -- You won the game!";
    private final String ALERT_MESSAGE_LOST = "Try again next time!";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String message = wonGame ? ALERT_MESSAGE_WON : ALERT_MESSAGE_LOST;

        builder.setMessage(message)
                .setTitle(ALERT_TITLE)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This is the only function is DialogFragment that will give me control of the activity
        this.sga = (StartGameActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // finish the activity
        this.sga.finish();
    }
}
