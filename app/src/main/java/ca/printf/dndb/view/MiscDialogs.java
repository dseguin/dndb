package ca.printf.dndb.view;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Supplier;
import androidx.fragment.app.FragmentActivity;
import ca.printf.dndb.R;
import ca.printf.dndb.list.SpellListProvider;
import ca.printf.dndb.list.SpellSortComparator;
import ca.printf.dndb.list.SpellSortSpinner;

public class MiscDialogs {
    private static AlertDialog sortPopup;

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

    public static void spellSortDialog(final SpellListProvider provider, FragmentActivity activity) {
        AlertDialog.Builder sortDialog = new AlertDialog.Builder(activity);
        View sortLayout = activity.getLayoutInflater().inflate(R.layout.spell_sort_dialog, null, false);
        Spinner sort = sortLayout.findViewById(R.id.spellsort_spinner);
        sort.setAdapter(new SpellSortSpinner(activity));
        sortDialog.setView(sortLayout);
        sortDialog.setNegativeButton(R.string.general_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
        });
        sortDialog.setPositiveButton(R.string.general_button_sort, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                boolean reverseOrder = ((CheckBox)sortPopup.findViewById(R.id.spellsort_descending_checkbox)).isChecked();
                String sortitem = (String)((Spinner)sortPopup.findViewById(R.id.spellsort_spinner)).getSelectedItem();
                provider.sort(new SpellSortComparator(sortitem, reverseOrder));
            }
        });
        sortPopup = sortDialog.create();
        sortPopup.show();
    }
}
