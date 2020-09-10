package ca.printf.dndb.view;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import ca.printf.dndb.R;
import ca.printf.dndb.entity.Spell;
import ca.printf.dndb.list.BookmarkListSpinner;
import ca.printf.dndb.logic.BookmarkListController;

public class SpellDetailsPage extends Fragment {
    private static final String PREV_SPELL = "previous_spell";
    private Spell spell;

    public SpellDetailsPage(Spell spell) {
        this.spell = spell;
    }

    public SpellDetailsPage() {}

    public void onCreate(Bundle b) {
        super.onCreate(b);
        if(b == null)
            return;
        spell = (Spell)b.getSerializable(PREV_SPELL);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(saveCurrentSpell(outState));
    }

    private Bundle saveCurrentSpell(Bundle b) {
        b.putSerializable(PREV_SPELL, this.spell);
        return b;
    }

    private View.OnClickListener bookmarkbtn = new View.OnClickListener() {
        public void onClick(View v) {
            showBookmarkDialog();
        }
    };

    private void showBookmarkDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
        dlg.setTitle(R.string.label_spell_addtobookmark_title);
        final boolean noBookmarks = BookmarkListController.isEmpty();
        final Spinner bookmarkSpinner = new Spinner(getContext());
        bookmarkSpinner.setPadding(100, 50, 100, 50);
        bookmarkSpinner.setAdapter(new BookmarkListSpinner(getContext()));
        if(!noBookmarks)
            dlg.setView(bookmarkSpinner);
        else
            dlg.setMessage(R.string.label_spell_addtobookmark_nobookmarkmsg);
        dlg.setNegativeButton(R.string.general_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
        });
        if(!noBookmarks) {
            dlg.setPositiveButton(R.string.general_button_confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    BookmarkListController.addSpell(bookmarkSpinner.getSelectedItemId(),
                            SpellDetailsPage.this.spell, getActivity());
                }
            });
        }
        dlg.create().show();
    }

    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.spell_details_page, vg, false);
        if(this.spell == null)
            return v;
        v.findViewById(R.id.spelldetail_btn_addtobookmark).setOnClickListener(bookmarkbtn);
        ((TextView)v.findViewById(R.id.spelldetail_spellname)).setText(spell.getName());
        ((TextView)v.findViewById(R.id.spelldetail_description)).setText(convertNewlines(spell.getDesc()));
        if(spell.getHigherDesc() != null && !spell.getHigherDesc().isEmpty()) {
            TextView hd = v.findViewById(R.id.spelldetail_highlevel);
            hd.setText(getString(R.string.general_spell_athigherlevels) + ". " + convertNewlines(spell.getHigherDesc()));
            hd.setVisibility(View.VISIBLE);
        }
        String lvl = spell.getLevel() > 0 ? Integer.toString(spell.getLevel()) : "Cantrip";
        ((TextView)v.findViewById(R.id.spelldetail_level)).setText(lvl);
        if(spell.isConcentration())
            ((ImageView)v.findViewById(R.id.spelldetail_concentration)).setVisibility(View.VISIBLE);
        if(spell.isRitual())
            ((ImageView)v.findViewById(R.id.spelldetail_ritual)).setVisibility(View.VISIBLE);
        ((TextView)v.findViewById(R.id.spelldetail_range)).setText(spell.getRange());
        ((TextView)v.findViewById(R.id.spelldetail_duration)).setText(spell.getDuration());
        ((TextView)v.findViewById(R.id.spelldetail_casttime)).setText(spell.getCastTime());
        if(spell.getReactionDesc() != null && !spell.getReactionDesc().isEmpty()) {
            TextView rd = v.findViewById(R.id.spelldetail_reactiondesc);
            rd.setText("(" + spell.getReactionDesc() + ")");
            rd.setVisibility(View.VISIBLE);
        }
        ((TextView)v.findViewById(R.id.spelldetail_school)).setText(spell.getSchool());
        String comps = (spell.isVerbal() ? "V" : "");
        comps += (spell.isSomatic() ? (comps.isEmpty() ? "S" : "/S") : "");
        comps += (spell.isMaterial() ? (comps.isEmpty() ? "M*" : "/M*") : "");
        ((TextView)v.findViewById(R.id.spelldetail_component)).setText(comps);
        if(spell.getMaterials() != null && !spell.getMaterials().isEmpty()) {
            TextView mat = v.findViewById(R.id.spelldetail_materials);
            mat.setText("* " + spell.getMaterials());
            mat.setVisibility(View.VISIBLE);
        }
        ((TextView)v.findViewById(R.id.spelldetail_targets)).setText(colateStringList(spell.getTargets()));
        displayStringList(spell.getAbilitySaves(), v, R.id.spelldetail_saves, R.id.spelldetail_saves_container);
        displayStringList(spell.getAtkTypes(), v, R.id.spelldetail_attacks, R.id.spelldetail_attacks_container);
        displayStringList(spell.getDmgTypes(), v, R.id.spelldetail_damages, R.id.spelldetail_damages_container);
        displayStringList(spell.getConditions(), v, R.id.spelldetail_conditions, R.id.spelldetail_conditions_container);
        ((TextView)v.findViewById(R.id.spelldetail_sources)).setText(colateStringList(spell.getSources().values()));
        ((TextView)v.findViewById(R.id.spelldetail_classes)).setText(colateStringList(spell.getClasses()));
        return v;
    }

    private String convertNewlines(String str) {
        return str.replaceAll("\n", "\n\n");
    }

    private void displayStringList(ArrayList<String> list, View parent, int textId, int containerId) {
        String strlist = colateStringList(list);
        if(strlist.trim().isEmpty())
            return;
        ((TextView)parent.findViewById(textId)).setText(strlist);
        parent.findViewById(containerId).setVisibility(View.VISIBLE);
    }

    private String colateStringList(Collection<String> list) {
        String ret = "";
        String delim = "";
        for(String s : list) {
            ret += (delim + s);
            delim = ", ";
        }
        return ret;
    }

    public static void goToSpellDetails(FragmentManager fragManager, Spell spell) {
        fragManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_frame, new SpellDetailsPage(spell))
                .commit();
    }
}