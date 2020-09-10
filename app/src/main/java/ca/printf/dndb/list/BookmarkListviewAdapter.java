package ca.printf.dndb.list;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.core.util.Supplier;
import androidx.fragment.app.FragmentActivity;
import ca.printf.dndb.R;
import ca.printf.dndb.entity.Bookmark;
import ca.printf.dndb.logic.BookmarkListController;
import ca.printf.dndb.view.MiscDialogs;

public class BookmarkListviewAdapter extends BaseAdapter {
    private FragmentActivity parentActivity;
    public BookmarkListviewAdapter(FragmentActivity parentActivity) {this.parentActivity = parentActivity;}
    public int getCount() {return BookmarkListController.size();}
    public Object getItem(int position) {return BookmarkListController.get(position);}
    public long getItemId(int position) {return ((Bookmark)getItem(position)).getId();}

    public View getView(int pos, View v, ViewGroup parent) {
        final Bookmark b = (Bookmark)getItem(pos);
        if(v == null)
            v = parentActivity.getLayoutInflater().inflate(R.layout.bookmark_list_item, parent, false);
        ((TextView)v.findViewById(R.id.bookmarklistitem_bookmarkname)).setText(b.getName());
        v.findViewById(R.id.bookmarklistitem_delete_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Supplier<Void> deleteBookmark = new Supplier<Void>() {
                    public Void get() {
                        BookmarkListController.remove(b, parentActivity);
                        BookmarkListviewAdapter.this.notifyDataSetChanged();
                        return null;
                    }
                };
                MiscDialogs.confirmationDialog(R.string.label_bookmarklistitem_delete_msg, deleteBookmark, parentActivity);
            }
        });
        return v;
    }
}
