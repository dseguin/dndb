package ca.printf.dndb.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import ca.printf.dndb.R;
import ca.printf.dndb.list.BookmarkListviewAdapter;
import ca.printf.dndb.logic.BookmarkListController;

public class BookmarkListPage extends Fragment {
    private BookmarkListviewAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new BookmarkListviewAdapter(getActivity());
    }

    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.bookmark_list_page, vg, false);
        ListView bookmarkList = v.findViewById(R.id.bookmarklist_listview);
        bookmarkList.setAdapter(adapter);
        bookmarkList.setOnItemClickListener(selectBookmark);
        ImageButton btn = v.findViewById(R.id.bookmarklist_add_list);
        btn.setOnClickListener(addBookmarkList);
        return v;
    }

    private View.OnClickListener addBookmarkList = new View.OnClickListener() {
        public void onClick(View v) {newBookmarkDialog();}
    };

    private AdapterView.OnItemClickListener selectBookmark = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, new BookmarkPage(BookmarkListController.get(position)))
                    .commit();
        }
    };

    private void newBookmarkDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
        dlg.setTitle(R.string.label_bookmarklist_dialogadd_title);
        final EditText textfield = new EditText(getContext());
        textfield.setGravity(Gravity.CENTER_HORIZONTAL);
        textfield.setHint(R.string.placeholder_bookmarklist_dialogadd_name);
        textfield.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        dlg.setView(textfield);
        dlg.setNegativeButton(R.string.general_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
        });
        dlg.setPositiveButton(R.string.general_button_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BookmarkListController.add(textfield.getText().toString(), getActivity());
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dlg.create().show();
    }
}
