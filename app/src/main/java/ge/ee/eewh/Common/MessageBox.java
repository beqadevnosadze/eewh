package ge.ee.eewh.Common;

import android.app.AlertDialog;
import android.content.Context;



/**
 * Created by beka-work on 10.07.2017.
 */

public class MessageBox {
    public static void Show(Context context, String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("ინფორმაცია");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }



}
