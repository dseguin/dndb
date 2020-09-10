package ca.printf.dndb.view;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Supplier;
import ca.printf.dndb.R;

public class MiscDialogs {
    public static void confirmationDialog(String msg, final Supplier<Void> confirmAction, Context ctx) {
        AlertDialog.Builder confirm = new AlertDialog.Builder(ctx);
        confirm.setTitle(R.string.general_confirm_dialog_title);
        confirm.setMessage(msg);
        confirm.setNegativeButton(R.string.general_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
        });
        confirm.setPositiveButton(R.string.general_button_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {confirmAction.get();}
        });
        confirm.create().show();
    }

    public static void confirmationDialog(int msgId, final Supplier<Void> confirmAction, Context ctx) {
        confirmationDialog(ctx.getString(msgId), confirmAction, ctx);
    }
}
