package org.intelehealth.ezazi.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.intelehealth.ezazi.R;
import org.intelehealth.ezazi.app.IntelehealthApplication;

public class DialogUtils {

    public void showOkDialog(Context context, String title, String message, String ok) {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(context);

        //AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogStyle).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = alertDialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription("positive_ok");
        positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        //alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        IntelehealthApplication.setAlertDialogCustomTheme(context, dialog);
    }

    public void showerrorDialog(Context context, String title, String message, String ok) {
        //AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogStyle).create();
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = alertDialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription("positive_ok");
        positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        //alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        IntelehealthApplication.setAlertDialogCustomTheme(context, dialog);
    }

    // Timeline stage 1 end confirmation dialog
    public void stage1ConfirmationDialog(Context context, String title, String message, String ok, String cancel) {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alertDialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription("positive_ok");
        positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setContentDescription("negative_ok");
        negativeButton.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));

        IntelehealthApplication.setAlertDialogCustomTheme(context, dialog);
    }



}
