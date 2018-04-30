package com.example.javier.tmbeacon;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by Javier on 29/09/2017.
 */

public final class DialogBuilder {

    private DialogBuilder() {
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("Oks", null);
        return alertDialog.create();
    }

}
