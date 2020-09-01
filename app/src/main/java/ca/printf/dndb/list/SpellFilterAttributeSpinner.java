package ca.printf.dndb.list;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class SpellFilterAttributeSpinner implements SpinnerAdapter {
    private ArrayList<String> attributes;
    private Context ctx;

    public SpellFilterAttributeSpinner(ArrayList<String> attributes, Context ctx) {
        this.attributes = attributes;
        this.ctx = ctx;
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
            old = new TextView(ctx);
        ((TextView)old).setText(txt);
        return (TextView)old;
    }
}
