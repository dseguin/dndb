package ca.printf.dndb.view;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.core.util.Supplier;
import androidx.fragment.app.Fragment;
import java.io.FileNotFoundException;
import ca.printf.dndb.R;
import ca.printf.dndb.io.DndbSQLManager;
import ca.printf.dndb.logic.BookmarkListController;
import ca.printf.dndb.logic.SpellListController;

public class SettingsPage extends Fragment {
    private static final int FILEPICKER_RESULT = 0xF17E;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.settings_page, vg, false);
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
                    SpellListController.initSpells(getActivity());
                    BookmarkListController.initBookmarks(getActivity());
                    return null;
                }
            };
            MiscDialogs.confirmationDialog(getString(R.string.label_settings_reset_db_confim_msg), resetAction, getContext());
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
            DndbSQLManager dbman = new DndbSQLManager(getActivity());
            SQLiteDatabase db = dbman.getWritableDatabase();
            dbman.execZipPackage(db, getActivity().getContentResolver().openInputStream(data.getData()));
            db.close();
        } catch (FileNotFoundException | NullPointerException e) {
            Log.e("onActivityResult", "Error loading file from file picker", e);
        } catch (Exception e) {
            Log.e("onActivityResult", "Error processing source package", e);
            ErrorPage.errorScreen(getActivity().getSupportFragmentManager(),
                    "Error processing source package", e);
        }
    }

    private void resetDB() {
        DndbSQLManager dbman = new DndbSQLManager(getActivity());
        SQLiteDatabase db = dbman.getWritableDatabase();
        Log.d("resetDB", "Clearing database with onCreate()");
        dbman.onCreate(db);
        db.close();
        dbman.close();
    }
}
