package com.atobia.worddomino.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by dima on 11/30/2014.
 */
public class SafetyNoticeDialog extends DialogFragment {
    private final String ALERT_TITLE = "Attention";
    private final String ALERT_MESSAGE = "Do not drink and drive!";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(ALERT_MESSAGE)
                .setTitle(ALERT_TITLE)
                .setNegativeButton("Never show this again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Configuration.ShowSafetyScreen = false;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
