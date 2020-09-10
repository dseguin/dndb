package ca.printf.dndb.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.Comparator;
import ca.printf.dndb.R;
import ca.printf.dndb.list.SpellListProvider;
import ca.printf.dndb.logic.SpellFilterLogic;
import ca.printf.dndb.logic.SpellListController;
import ca.printf.dndb.entity.Spell;
import ca.printf.dndb.list.SpellFilterAttributeSpinner;
import ca.printf.dndb.list.SpellListviewAdapter;

public class SpellListPage extends Fragment {
    private SpellListviewAdapter adapter;
    private AlertDialog filterPopup;

    private SpellListProvider spellProvider = new SpellListProvider() {
        public int size() {return SpellListController.size();}
        public Spell get(int index) {return SpellListController.get(index);}
        public void sort(Comparator<Spell> c) {
            SpellListController.sort(c);
            adapter.notifyDataSetChanged();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater li, ViewGroup v, Bundle b) {
        adapter = new SpellListviewAdapter(getActivity().getWindow(), spellProvider);
        View root = li.inflate(R.layout.spell_list_page, v, false);
        ListView list = root.findViewById(R.id.spells_listview);
        list.setAdapter(adapter);
        list.setOnItemClickListener(spellSelection);
        root.findViewById(R.id.spells_btn_filter_spells).setOnClickListener(filterButton);
        root.findViewById(R.id.spells_btn_sort_spells).setOnClickListener(sortButton);
        return root;
    }

    private AdapterView.OnItemClickListener spellSelection = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            SpellDetailsPage.goToSpellDetails(getActivity().getSupportFragmentManager(), SpellListController.get(pos));
        }
    };

    private View.OnClickListener filterButton = new View.OnClickListener() {
        public void onClick(View btn) {
            filterPopup = createFilterDialog().create();
            filterPopup.show();
        }
    };

    private View.OnClickListener sortButton = new View.OnClickListener() {
        public void onClick(View v) {
            MiscDialogs.spellSortDialog(spellProvider, getActivity());
        }
    };

    private AlertDialog.Builder createFilterDialog() {
        AlertDialog.Builder filterDialog = new AlertDialog.Builder(getContext());
        View filterLayout = getLayoutInflater().inflate(R.layout.spell_filter_dialog, null, false);

        Spinner level = filterLayout.findViewById(R.id.spellfilter_level_spinner);
        SpellFilterAttributeSpinner lvlSpinner =
                new SpellFilterAttributeSpinner(
                        getActivity(), SpellFilterLogic.DEFAULT_OPTION_LEVEL, Spell.QUERY_LEVEL_OPTIONS);
        lvlSpinner.replaceItem("0", "Cantrip");
        level.setAdapter(lvlSpinner);

        Spinner school = filterLayout.findViewById(R.id.spellfilter_school_spinner);
        school.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_SCHOOL, Spell.QUERY_SCHOOL_OPTIONS));

        Spinner duration = filterLayout.findViewById(R.id.spellfilter_duration_spinner);
        duration.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_DURATION, Spell.QUERY_DURATION_OPTIONS));

        Spinner casttime = filterLayout.findViewById(R.id.spellfilter_casttime_spinner);
        casttime.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_CASTTIME, Spell.QUERY_CASTTIME_OPTIONS));

        Spinner target = filterLayout.findViewById(R.id.spellfilter_target_spinner);
        target.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_TARGET, Spell.QUERY_TARGET_OPTIONS));

        Spinner save = filterLayout.findViewById(R.id.spellfilter_ability_spinner);
        save.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_ABILITY, Spell.QUERY_ABILITY_OPTIONS));

        Spinner atktype = filterLayout.findViewById(R.id.spellfilter_atktype_spinner);
        atktype.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_ATKTYPE, Spell.QUERY_ATK_TYPE_OPTIONS));

        Spinner dmgtype = filterLayout.findViewById(R.id.spellfilter_dmgtype_spinner);
        dmgtype.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_DMGTYPE, Spell.QUERY_DMG_TYPE_OPTIONS));

        Spinner condition = filterLayout.findViewById(R.id.spellfilter_condition_spinner);
        condition.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_CONDITION, Spell.QUERY_CONDITION_OPTIONS));

        Spinner source = filterLayout.findViewById(R.id.spellfilter_source_spinner);
        source.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_SOURCE, Spell.QUERY_SOURCE_OPTIONS));

        Spinner spellclass = filterLayout.findViewById(R.id.spellfilter_class_spinner);
        spellclass.setAdapter(new SpellFilterAttributeSpinner(
                getActivity(), SpellFilterLogic.DEFAULT_OPTION_CLASS, Spell.QUERY_CLASS_OPTIONS));

        filterDialog.setView(filterLayout);
        filterDialog.setPositiveButton(R.string.general_button_search, applySearch);
        filterDialog.setNegativeButton(R.string.general_button_cancel, cancelSearch);
        return filterDialog;
    }

    private DialogInterface.OnClickListener cancelSearch = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
    };

    private DialogInterface.OnClickListener applySearch = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            SpellFilterLogic filter = new SpellFilterLogic();
            filter.setSearchphrase(((EditText)filterPopup.findViewById(R.id.spellfilter_spellname)).getText().toString());
            filter.setCheckdesc(((CheckBox)filterPopup.findViewById(R.id.spellfilter_description_checkbox)).isChecked());
            filter.setCheckmats(((CheckBox)filterPopup.findViewById(R.id.spellfilter_materialtxt_checkbox)).isChecked());
            filter.setIsconcentration(((CheckBox)filterPopup.findViewById(R.id.spellfilter_concentration_checkbox)).isChecked());
            filter.setIsritual(((CheckBox)filterPopup.findViewById(R.id.spellfilter_ritual_checkbox)).isChecked());
            String tmpnum = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_level_spinner)).getSelectedItem();
            int level = -1;
            if(tmpnum != null && !tmpnum.trim().isEmpty() && !tmpnum.contains(getString(R.string.label_spell_filter_level)))
                level = tmpnum.equals("Cantrip") ? 0 : Integer.parseInt(tmpnum);
            filter.setLevel(level);
            filter.setSchool((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_school_spinner)).getSelectedItem());
            filter.setDuration((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_duration_spinner)).getSelectedItem());
            filter.setCasttime((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_casttime_spinner)).getSelectedItem());
            filter.setTarget((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_target_spinner)).getSelectedItem());
            filter.setSave((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_ability_spinner)).getSelectedItem());
            filter.setAtktype((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_atktype_spinner)).getSelectedItem());
            filter.setDmgtype((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_dmgtype_spinner)).getSelectedItem());
            filter.setCondition((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_condition_spinner)).getSelectedItem());
            filter.setSource((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_source_spinner)).getSelectedItem());
            filter.setSpellclass((String)((Spinner)filterPopup.findViewById(R.id.spellfilter_class_spinner)).getSelectedItem());
            filter.setIsverbal(((CheckBox)filterPopup.findViewById(R.id.spellfilter_verbal_checkbox)).isChecked());
            filter.setIssomatic(((CheckBox)filterPopup.findViewById(R.id.spellfilter_somatic_checkbox)).isChecked());
            filter.setIsmaterial(((CheckBox)filterPopup.findViewById(R.id.spellfilter_material_checkbox)).isChecked());
            filter.setExcludecomps(((CheckBox)filterPopup.findViewById(R.id.spellfilter_exclude_component)).isChecked());
            tmpnum = ((EditText)filterPopup.findViewById(R.id.spellfilter_material_mincost)).getText().toString();
            filter.setMincost(tmpnum != null && !tmpnum.trim().isEmpty() ? Integer.parseInt(tmpnum) : -1);
            tmpnum = ((EditText)filterPopup.findViewById(R.id.spellfilter_material_maxcost)).getText().toString();
            filter.setMaxcost(tmpnum != null && !tmpnum.trim().isEmpty() ? Integer.parseInt(tmpnum) : -1);
            filter.setRange(((EditText)filterPopup.findViewById(R.id.spellfilter_range)).getText().toString());

            SpellListController.reloadFromDB(getActivity());
            filter.execFilter();
            adapter.notifyDataSetChanged();
        }
    };
}
