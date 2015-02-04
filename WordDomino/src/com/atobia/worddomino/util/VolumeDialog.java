package com.atobia.worddomino.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.atobia.worddomino.StartGameActivity;

public class VolumeDialog extends DialogFragment {
    private StartGameActivity sga;

    private final String ALERT_TITLE = "Attention";
    private final String ALERT_MESSAGE = "Game volume is too low";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(ALERT_MESSAGE)
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

        // Run that game!

        this.sga.runGame();
    }
}
