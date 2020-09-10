package ca.printf.dndb.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.Collections;
import java.util.Comparator;
import ca.printf.dndb.R;
import ca.printf.dndb.entity.Bookmark;
import ca.printf.dndb.entity.Spell;
import ca.printf.dndb.list.SpellListProvider;
import ca.printf.dndb.list.SpellListviewAdapter;

public class BookmarkPage extends Fragment {
    private static final String PREV_BOOKMARK = "prev_bookmark";
    private SpellListviewAdapter adapter;
    private Bookmark bookmark;

    private SpellListProvider bookmarkProvider = new SpellListProvider() {
        public int size() {return bookmark.getSpellList().size();}
        public Spell get(int index) {return bookmark.getSpellList().get(index);}
        public void sort(Comparator<Spell> c) {
            Collections.sort(bookmark.getSpellList(), c);
            adapter.notifyDataSetChanged();
        }
    };

    private View.OnClickListener sortbtn = new View.OnClickListener() {
        public void onClick(View v) {MiscDialogs.spellSortDialog(bookmarkProvider, getActivity());}
    };

    private AdapterView.OnItemClickListener spellSelection = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SpellDetailsPage.goToSpellDetails(getActivity().getSupportFragmentManager(), bookmarkProvider.get(position));
        }
    };

    public BookmarkPage() {}
    public BookmarkPage(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SpellListviewAdapter(getActivity().getWindow(), bookmarkProvider);
        if(savedInstanceState == null)
            return;
        bookmark = (Bookmark)savedInstanceState.getSerializable(PREV_BOOKMARK);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PREV_BOOKMARK, bookmark);
        super.onSaveInstanceState(outState);
    }

    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.spell_list_page, vg, false);
        ListView list = v.findViewById(R.id.spells_listview);
        list.setAdapter(adapter);
        list.setOnItemClickListener(spellSelection);
        v.findViewById(R.id.spells_btn_filter_spells).setVisibility(View.GONE);
        v.findViewById(R.id.spells_btn_sort_spells).setOnClickListener(sortbtn);
        return v;
    }
}
