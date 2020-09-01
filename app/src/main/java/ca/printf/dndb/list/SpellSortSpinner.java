package ca.printf.dndb.list;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class SpellSortSpinner implements SpinnerAdapter {
    private ArrayList<String> sortlist;
    private Context ctx;

    public SpellSortSpinner(ArrayList<String> sortlist, Context ctx) {
        this.sortlist = sortlist;
        this.ctx = ctx;
    }

    public void registerDataSetObserver(DataSetObserver observer) {}
    public void unregisterDataSetObserver(DataSetObserver observer) {}
    public int getCount() {return sortlist.size();}
    public Object getItem(int position) {return sortlist.get(position);}
    public long getItemId(int position) {return -1;}
    public boolean hasStableIds() {return true;}
    public int getItemViewType(int position) {return 0;}
    public int getViewTypeCount() {return 1;}
    public boolean isEmpty() {return sortlist.isEmpty();}

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(ctx);
        tv.setText(sortlist.get(position));
        return tv;
    }
}
