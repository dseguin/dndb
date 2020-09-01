package ca.printf.dndb.list;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import ca.printf.dndb.R;
import ca.printf.dndb.entity.Spell;

public class SpellListManager extends BaseAdapter {
    private ArrayList<Spell> spells;
    private Window parentActivity;

    public SpellListManager(ArrayList<Spell> spells, Window thisActivity) {
        this.spells = spells;
        this.parentActivity = thisActivity;
    }

    public void setSpells(ArrayList<Spell> spells) {
        this.spells = spells;
    }

    public int getCount() {return spells.size();}

    public Object getItem(int pos) {return spells.get(pos);}

    public long getItemId(int pos) {return ((Spell)getItem(pos)).getId();}

    public View getView(int pos, View old, ViewGroup parent) {
        Spell s = (Spell)getItem(pos);
        View v = (old != null) ? old : parentActivity
                        .getLayoutInflater()
                        .inflate(R.layout.spells_listview_item, parent, false);
        ((TextView)v.findViewById(R.id.spells_listview_item_spellname)).setText(s.getName());
        String lvl = s.getLevel() > 0 ? Integer.toString(s.getLevel()) : "Cantrip";
        ((TextView)v.findViewById(R.id.spells_listview_item_level)).setText(lvl);
        ((TextView)v.findViewById(R.id.spells_listview_item_school)).setText(s.getSchool());
        ((TextView)v.findViewById(R.id.spells_listview_item_casttime)).setText(s.getCastTime());
        String comps = (s.isVerbal() ? "V" : "");
        comps += (s.isSomatic() ? (comps.isEmpty() ? "S" : "/S") : "");
        comps += (s.isMaterial() ? (comps.isEmpty() ? "M" : "/M") : "");
        ((TextView)v.findViewById(R.id.spells_listview_item_component)).setText(comps);
        ((ImageView)v.findViewById(R.id.spells_listview_item_concentration)).setVisibility(s.isConcentration() ? View.VISIBLE : View.GONE);
        ((ImageView)v.findViewById(R.id.spells_listview_item_ritual)).setVisibility(s.isRitual() ? View.VISIBLE : View.GONE);
        return v;
    }
}