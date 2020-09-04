package ca.printf.dndb.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Supplier;
import androidx.fragment.app.Fragment;
import java.io.FileNotFoundException;
import java.io.IOException;
import ca.printf.dndb.R;
import ca.printf.dndb.data.DndbSQLManager;

public class SettingsFragment extends Fragment {
    private static final int FILEPICKER_RESULT = 0xF17E;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.fragment_settings, vg, false);
        Button importBtn = v.findViewById(R.id.settings_import_source);
        importBtn.setOnClickListener(importBtnListener);
        Button resetBtn = v.findViewById(R.id.settings_reset_db);
        resetBtn.setOnClickListener(resetBtnListener);
        return v;
    }

    private View.OnClickListener importBtnListener = new View.OnClickListener() {
        public void onClick(View v) {showFilePicker();}
    };

    private View.OnClickListener resetBtnListener = new View.OnClickListener() {
        public void onClick(View v) {
            Supplier<Void> resetAction = new Supplier<Void>() {
                public Void get() {
                    resetDB();
                    return null;
                }
            };
            confirmationDialog(getString(R.string.label_settings_reset_db_confim_msg), resetAction);
        }
    };

    // https://riptutorial.com/android/example/14425/showing-a-file-chooser-and-reading-the-result
    private void showFilePicker() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("application/zip");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(i, FILEPICKER_RESULT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != FILEPICKER_RESULT || resultCode != Activity.RESULT_OK)
            return;
        try {
            DndbSQLManager dbman = new DndbSQLManager(getContext(), getActivity());
            SQLiteDatabase db = dbman.getWritableDatabase();
            dbman.execZipPackage(db, getActivity().getContentResolver().openInputStream(data.getData()));
            db.close();
        } catch (FileNotFoundException | NullPointerException e) {
            Log.e("onActivityResult", "Error loading file from file picker", e);
        } catch (Exception e) {
            Log.e("onActivityResult", "Error processing source package", e);
            ErrorFragment.errorScreen(getActivity().getSupportFragmentManager(),
                    "Error processing source package", e);
        }
    }

    private void resetDB() {
        DndbSQLManager dbman = new DndbSQLManager(getContext(), getActivity());
        SQLiteDatabase db = dbman.getWritableDatabase();
        Log.d("resetDB", "Clearing database with onCreate()");
        dbman.onCreate(db);
        db.close();
        dbman.close();
    }

    private void confirmationDialog(String msg, final Supplier<Void> confirmAction) {
        AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
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
}
