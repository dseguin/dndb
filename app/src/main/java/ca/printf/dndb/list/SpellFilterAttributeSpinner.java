package ca.printf.dndb.list;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import java.util.ArrayList;
import ca.printf.dndb.io.DndbSQLManager;

public class SpellFilterAttributeSpinner implements SpinnerAdapter {
    private ArrayList<String> attributes;
    private FragmentActivity a;

    public SpellFilterAttributeSpinner(FragmentActivity activity, int defaultOptionId, String optionsQuery) {
        this(activity, activity.getString(defaultOptionId), optionsQuery);
    }

    public SpellFilterAttributeSpinner(FragmentActivity activity, String defaultOption, String optionsQuery) {
        this.a = activity;
        this.attributes = spinnerAttributeOptions(defaultOption, optionsQuery);
    }

    public int getCount() {
        return attributes.size();
    }

    public Object getItem(int position) {
        return attributes.get(position);
    }

    public long getItemId(int position) {
        return -1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createListItemText(attributes.get(position), convertView);
    }

    public void replaceItem(String oldItem, String newItem) {
        attributes.set(attributes.indexOf(oldItem), newItem);
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public void registerDataSetObserver(DataSetObserver observer) {}

    public void unregisterDataSetObserver(DataSetObserver observer) {}

    private TextView createListItemText(String txt, View old) {
        if(!(old instanceof TextView))
            old = new TextView(a);
        ((TextView)old).setText(txt);
        return (TextView)old;
    }

    private ArrayList<String> spinnerAttributeOptions(String defaultEntry, String query) {
        DndbSQLManager dbman = new DndbSQLManager(a);
        ArrayList<String> ret = new ArrayList<>();
        ret.add("-- " + defaultEntry + " --");
        SQLiteDatabase db = dbman.getReadableDatabase();
        Cursor row = db.rawQuery(query, null);
        while(row.moveToNext())
            ret.add(row.getString(0));
        row.close();
        db.close();
        dbman.close();
        return ret;
    }
}
