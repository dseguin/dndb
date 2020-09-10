package ca.printf.dndb.list;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import ca.printf.dndb.R;
import ca.printf.dndb.entity.Bookmark;
import ca.printf.dndb.logic.BookmarkListController;

public class BookmarkListSpinner implements SpinnerAdapter {
    private Context ctx;

    public BookmarkListSpinner(Context ctx) {this.ctx = ctx;}
    public void registerDataSetObserver(DataSetObserver observer) {}
    public void unregisterDataSetObserver(DataSetObserver observer) {}
    public int getCount() {return BookmarkListController.size();}
    public Object getItem(int position) {return BookmarkListController.get(position);}
    public long getItemId(int position) {return ((Bookmark)getItem(position)).getId();}
    public boolean isEmpty() {return BookmarkListController.isEmpty();}
    public boolean hasStableIds() {return true;}
    public int getItemViewType(int position) {return 0;}
    public int getViewTypeCount() {return 1;}
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(ctx);
        tv.setText(BookmarkListController.get(position).getName());
        tv.setPadding(100, 50, 100, 50);
        tv.setTextSize(20);
        tv.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        return tv;
    }
}
