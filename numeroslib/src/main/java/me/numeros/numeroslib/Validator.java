package me.numeros.numeroslib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * Created by Abraham on 14/08/2015.
 */
public class Validator {
    public Object[] data;
    public String error;

    public static void promptError(String msg, Activity activity, final boolean finish) {
        prompt(NumerosLibApp.getContext().getString(R.string.error), msg, activity, finish);
    }

    public static void prompt(String title, String msg, final Activity activity, final boolean finish) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);

        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton(NumerosLibApp.getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (finish)
                    activity.finish();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public boolean validate(Object... args) {
        String theError = "";
        Object[] theData = new Object[args.length / 2];
        boolean theResult = true;

        for (int con1 = 0; con1 < args.length; con1 += 2) {
            if (args[con1] instanceof EditText) {
                EditText editText = (EditText) args[con1];
                String text = editText.getText().toString();

                if (text.equals("")) {
                    theError += ", " + args[con1 + 1];
                    theResult = false;
                } else
                    theData[(con1 + 1) / 2] = text;
            }
        }

        if (theResult)
            data = theData;
        else {
            error = NumerosLibApp.getContext().getString(R.string.campos_requeridos) + theError.substring(1);
        }

        return theResult;
    }

    public void promptError(Activity activity) {
        promptError(activity, false);
    }

    public void promptError(Activity activity, final boolean finish) {
        promptError(error, activity, false);
    }
}
