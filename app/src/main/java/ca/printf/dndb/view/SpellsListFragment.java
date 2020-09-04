package ca.printf.dndb.view;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import ca.printf.dndb.R;
import ca.printf.dndb.data.DndbSQLManager;
import ca.printf.dndb.entity.Spell;
import ca.printf.dndb.list.SpellFilterAttributeSpinner;
import ca.printf.dndb.list.SpellListManager;
import ca.printf.dndb.list.SpellSortComparator;
import ca.printf.dndb.list.SpellSortSpinner;

public class SpellsListFragment extends Fragment {
    private static final String SPELL_CACHE = "spell_cache";
    private ArrayList<Spell> spells;
    private DndbSQLManager dbman;
    private SpellListManager adapter;
    private AlertDialog filterPopup;
    private AlertDialog sortPopup;

    public SpellsListFragment() {
        spells = new ArrayList<>();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbman = new DndbSQLManager(getContext(), getActivity());
        initSpells();
        try {
            if(loadSpellCache()) {
                Log.d("loadSpellCache", "Spell cache successfully loaded");
                return;
            }
        } catch (Exception e) {
            Log.e("loadSpellCache", "Error while reading spell cache", e);
        }
        try {
            readSpellsFromDB();
        } catch (Exception e) {
            ErrorFragment.errorScreen(getActivity().getSupportFragmentManager(),
                    "readSpellsFromDB: Error reading spells from database", e);
        }
        try {
            if(saveSpellCache())
                Log.d("saveSpellCache", "Spell cache was written successfully");
        } catch (Exception e) {
            Log.e("saveSpellCache", "Error while writing to spell cache", e);
        }
    }

    public View onCreateView(LayoutInflater li, ViewGroup v, Bundle b) {
        adapter = new SpellListManager(spells, this.getActivity().getWindow());
        View root = li.inflate(R.layout.fragment_spells_list, v, false);
        ListView list = root.findViewById(R.id.spells_listview);
        list.setAdapter(adapter);
        list.setOnItemClickListener(spellSelection);
        root.findViewById(R.id.spells_btn_filter_spells).setOnClickListener(filterButton);
        root.findViewById(R.id.spells_btn_sort_spells).setOnClickListener(sortButton);
        return root;
    }

    private AdapterView.OnItemClickListener spellSelection = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            SpellDetailsFragment.goToSpellDetails(getActivity().getSupportFragmentManager(), spells.get(pos));
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
            sortPopup = createSortDialog().create();
            sortPopup.show();
        }
    };

    private AlertDialog.Builder createSortDialog() {
        AlertDialog.Builder sortDialog = new AlertDialog.Builder(getContext());
        View sortLayout = getLayoutInflater().inflate(R.layout.spell_sort_dialog, null, false);
        Spinner sort = sortLayout.findViewById(R.id.spellsort_spinner);
        sort.setAdapter(new SpellSortSpinner(createSortByList(), getContext()));
        sortDialog.setView(sortLayout);
        sortDialog.setNegativeButton(R.string.general_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
        });
        sortDialog.setPositiveButton(R.string.general_button_sort, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                boolean reverseOrder = ((CheckBox)sortPopup.findViewById(R.id.spellsort_descending_checkbox)).isChecked();
                String sortitem = (String)((Spinner)sortPopup.findViewById(R.id.spellsort_spinner)).getSelectedItem();
                sortSpellsBy(sortitem, reverseOrder);
            }
        });
        return sortDialog;
    }

    private ArrayList<String> createSortByList() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(SpellSortComparator.SORT_NAME);
        ret.add(SpellSortComparator.SORT_LEVEL);
        ret.add(SpellSortComparator.SORT_SCHOOL);
        ret.add(SpellSortComparator.SORT_DURATION);
        ret.add(SpellSortComparator.SORT_CASTTIME);
        ret.add(SpellSortComparator.SORT_RANGE);
        ret.add(SpellSortComparator.SORT_MATCOST);
        return ret;
    }

    private void sortSpellsBy(String constraint, boolean reverseOrder) {
        Collections.sort(spells, new SpellSortComparator(constraint, reverseOrder));
        adapter.setSpells(spells);
        adapter.notifyDataSetChanged();
    }

    private AlertDialog.Builder createFilterDialog() {
        AlertDialog.Builder filterDialog = new AlertDialog.Builder(getContext());
        View filterLayout = getLayoutInflater().inflate(R.layout.spell_filter_dialog, null, false);

        Spinner level = filterLayout.findViewById(R.id.spellfilter_level_spinner);
        ArrayList<String> levelOpts = spinnerAttributeOptions(R.string.label_spell_filter_level, Spell.QUERY_LEVEL_OPTIONS);
        levelOpts.set(levelOpts.indexOf("0"), "Cantrip");
        level.setAdapter(new SpellFilterAttributeSpinner(levelOpts, getContext()));

        Spinner school = filterLayout.findViewById(R.id.spellfilter_school_spinner);
        ArrayList<String> schoolOpts = spinnerAttributeOptions(R.string.label_spell_filter_school, Spell.QUERY_SCHOOL_OPTIONS);
        school.setAdapter(new SpellFilterAttributeSpinner(schoolOpts, getContext()));

        Spinner duration = filterLayout.findViewById(R.id.spellfilter_duration_spinner);
        ArrayList<String> durationOpts = spinnerAttributeOptions(R.string.label_spell_filter_duration, Spell.QUERY_DURATION_OPTIONS);
        duration.setAdapter(new SpellFilterAttributeSpinner(durationOpts, getContext()));

        Spinner casttime = filterLayout.findViewById(R.id.spellfilter_casttime_spinner);
        ArrayList<String> casttimeOpts = spinnerAttributeOptions(R.string.label_spell_filter_casttime, Spell.QUERY_CASTTIME_OPTIONS);
        casttime.setAdapter(new SpellFilterAttributeSpinner(casttimeOpts, getContext()));

        Spinner target = filterLayout.findViewById(R.id.spellfilter_target_spinner);
        ArrayList<String> targetOpts = spinnerAttributeOptions(R.string.label_spell_filter_target, Spell.QUERY_TARGET_OPTIONS);
        target.setAdapter(new SpellFilterAttributeSpinner(targetOpts, getContext()));

        Spinner save = filterLayout.findViewById(R.id.spellfilter_ability_spinner);
        ArrayList<String> saveOpts = spinnerAttributeOptions(R.string.label_spell_filter_ability, Spell.QUERY_ABILITY_OPTIONS);
        save.setAdapter(new SpellFilterAttributeSpinner(saveOpts, getContext()));

        Spinner atktype = filterLayout.findViewById(R.id.spellfilter_atktype_spinner);
        ArrayList<String> atktypeOpts = spinnerAttributeOptions(R.string.label_spell_filter_atktype, Spell.QUERY_ATK_TYPE_OPTIONS);
        atktype.setAdapter(new SpellFilterAttributeSpinner(atktypeOpts, getContext()));

        Spinner dmgtype = filterLayout.findViewById(R.id.spellfilter_dmgtype_spinner);
        ArrayList<String> dmgtypeOpts = spinnerAttributeOptions(R.string.label_spell_filter_dmgtype, Spell.QUERY_DMG_TYPE_OPTIONS);
        dmgtype.setAdapter(new SpellFilterAttributeSpinner(dmgtypeOpts, getContext()));

        Spinner condition = filterLayout.findViewById(R.id.spellfilter_condition_spinner);
        ArrayList<String> conditionOpts = spinnerAttributeOptions(R.string.label_spell_filter_condition, Spell.QUERY_CONDITION_OPTIONS);
        condition.setAdapter(new SpellFilterAttributeSpinner(conditionOpts, getContext()));

        Spinner source = filterLayout.findViewById(R.id.spellfilter_source_spinner);
        ArrayList<String> sourceOpts = spinnerAttributeOptions(R.string.label_spell_filter_source, Spell.QUERY_SOURCE_OPTIONS);
        source.setAdapter(new SpellFilterAttributeSpinner(sourceOpts, getContext()));

        Spinner spellclass = filterLayout.findViewById(R.id.spellfilter_class_spinner);
        ArrayList<String> classOpts = spinnerAttributeOptions(R.string.label_spell_filter_class, Spell.QUERY_CLASS_OPTIONS);
        spellclass.setAdapter(new SpellFilterAttributeSpinner(classOpts, getContext()));

        filterDialog.setView(filterLayout);
        filterDialog.setPositiveButton(R.string.general_button_search, applySearch);
        filterDialog.setNegativeButton(R.string.general_button_cancel, cancelSearch);
        return filterDialog;
    }

    private ArrayList<String> spinnerAttributeOptions(int strId, String query) {
        return spinnerAttributeOptions(getString(strId), query);
    }

    private ArrayList<String> spinnerAttributeOptions(String defaultEntry, String query) {
        ArrayList<String> ret = new ArrayList<>();
        ret.add("-- " + defaultEntry + " --");
        SQLiteDatabase db = dbman.getReadableDatabase();
        Cursor row = db.rawQuery(query, null);
        while(row.moveToNext())
            ret.add(row.getString(0));
        row.close();
        db.close();
        return ret;
    }

    private DialogInterface.OnClickListener cancelSearch = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
    };

    private DialogInterface.OnClickListener applySearch = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            String name = ((EditText)filterPopup.findViewById(R.id.spellfilter_spellname)).getText().toString();
            name = name.toLowerCase();
            boolean check_desc = ((CheckBox)filterPopup.findViewById(R.id.spellfilter_description_checkbox)).isChecked();
            boolean check_mats = ((CheckBox)filterPopup.findViewById(R.id.spellfilter_materialtxt_checkbox)).isChecked();
            boolean concentration = ((CheckBox)filterPopup.findViewById(R.id.spellfilter_concentration_checkbox)).isChecked();
            boolean ritual = ((CheckBox)filterPopup.findViewById(R.id.spellfilter_ritual_checkbox)).isChecked();
            String tmpnum = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_level_spinner)).getSelectedItem();
            int level = -1;
            if(tmpnum != null && !tmpnum.trim().isEmpty() && !tmpnum.contains(getString(R.string.label_spell_filter_level)))
                level = tmpnum.equals("Cantrip") ? 0 : Integer.parseInt(tmpnum);
            String school = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_school_spinner)).getSelectedItem();
            String duration = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_duration_spinner)).getSelectedItem();
            String casttime = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_casttime_spinner)).getSelectedItem();
            String target = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_target_spinner)).getSelectedItem();
            String save = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_ability_spinner)).getSelectedItem();
            String atktype = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_atktype_spinner)).getSelectedItem();
            String dmgtype = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_dmgtype_spinner)).getSelectedItem();
            String condition = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_condition_spinner)).getSelectedItem();
            String source = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_source_spinner)).getSelectedItem();
            String spellclass = (String)((Spinner)filterPopup.findViewById(R.id.spellfilter_class_spinner)).getSelectedItem();
            boolean verbal = ((CheckBox)filterPopup.findViewById(R.id.spellfilter_verbal_checkbox)).isChecked();
            boolean somatic = ((CheckBox)filterPopup.findViewById(R.id.spellfilter_somatic_checkbox)).isChecked();
            boolean material = ((CheckBox)filterPopup.findViewById(R.id.spellfilter_material_checkbox)).isChecked();
            boolean exclude_comps = ((CheckBox)filterPopup.findViewById(R.id.spellfilter_exclude_component)).isChecked();
            tmpnum = ((EditText)filterPopup.findViewById(R.id.spellfilter_material_mincost)).getText().toString();
            int mincost = -1;
            if(tmpnum != null && !tmpnum.trim().isEmpty())
                mincost = Integer.parseInt(tmpnum);
            tmpnum = ((EditText)filterPopup.findViewById(R.id.spellfilter_material_maxcost)).getText().toString();
            int maxcost = -1;
            if(tmpnum != null && !tmpnum.trim().isEmpty())
                maxcost= Integer.parseInt(tmpnum);
            String range = ((EditText)filterPopup.findViewById(R.id.spellfilter_range)).getText().toString();
            range = range.toLowerCase();
            spells.clear();
            readSpellsFromDB();
            ArrayList<Spell> spellsFiltered = new ArrayList<>();
            for(Spell s : spells) {
                if(!name.isEmpty()) {
                    if(!s.getName().toLowerCase().contains(name)
                            && !(check_desc && s.getDesc().toLowerCase().contains(name))
                            && !(check_mats && s.getMaterials() != null && s.getMaterials().toLowerCase().contains(name)))
                        continue;
                }
                if(concentration && !s.isConcentration())
                    continue;
                if(ritual && !s.isRitual())
                    continue;
                if(level > -1 && s.getLevel() != level)
                    continue;
                if(!school.contains(getString(R.string.label_spell_filter_school)) && !school.equals(s.getSchool()))
                    continue;
                if(!duration.contains(getString(R.string.label_spell_filter_duration)) && !duration.equals(s.getDuration()))
                    continue;
                if(!casttime.contains(getString(R.string.label_spell_filter_casttime)) && !casttime.equals(s.getCastTime()))
                    continue;
                if(!target.contains(getString(R.string.label_spell_filter_target))) {
                    boolean found = false;
                    for(String t : s.getTargets()) {
                        if(found = target.equals(t))
                            break;
                    }
                    if(!found)
                        continue;
                }
                if(!save.contains(getString(R.string.label_spell_filter_ability))) {
                    boolean found = false;
                    for(String t : s.getAbilitySaves()) {
                        if(found = save.equals(t))
                            break;
                    }
                    if(!found)
                        continue;
                }
                if(!atktype.contains(getString(R.string.label_spell_filter_atktype))) {
                    boolean found = false;
                    for(String t : s.getAtkTypes()) {
                        if(found = atktype.equals(t))
                            break;
                    }
                    if(!found)
                        continue;
                }
                if(!dmgtype.contains(getString(R.string.label_spell_filter_dmgtype))) {
                    boolean found = false;
                    for(String t : s.getDmgTypes()) {
                        if(found = dmgtype.equals(t))
                            break;
                    }
                    if(!found)
                        continue;
                }
                if(!condition.contains(getString(R.string.label_spell_filter_condition))) {
                    boolean found = false;
                    for(String t : s.getConditions()) {
                        if(found = condition.equals(t))
                            break;
                    }
                    if(!found)
                        continue;
                }
                if(!source.contains(getString(R.string.label_spell_filter_source))) {
                    boolean found = false;
                    for(String t : s.getSources().keySet()) {
                        if(found = source.equals(t))
                            break;
                    }
                    if(!found)
                        continue;
                }
                if(!spellclass.contains(getString(R.string.label_spell_filter_class))) {
                    boolean found = false;
                    for(String t : s.getClasses()) {
                        if(found = spellclass.equals(t))
                            break;
                    }
                    if(!found)
                        continue;
                }
                if(verbal && ((!s.isVerbal() && !exclude_comps) || (s.isVerbal() && exclude_comps)))
                    continue;
                if(somatic && ((!s.isSomatic() && !exclude_comps) || (s.isSomatic() && exclude_comps)))
                    continue;
                if(material && ((!s.isMaterial() && !exclude_comps) || (s.isMaterial() && exclude_comps)))
                    continue;
                if(mincost > -1 && (!s.isMaterial() || s.getMaterialsCost() < mincost))
                    continue;
                if(maxcost > -1 && (!s.isMaterial() || s.getMaterialsCost() > maxcost))
                    continue;
                if(!range.isEmpty() && !s.getRange().toLowerCase().contains(range))
                    continue;
                spellsFiltered.add(s);
            }
            spells = spellsFiltered;
            adapter.setSpells(spells);
            adapter.notifyDataSetChanged();
        }
    };

    private void initSpells() {
        SQLiteDatabase db = dbman.getReadableDatabase();
        if(dbman.dbHasSpells(db)) {
            db.close();
            return;
        }
        db.close();
        insertDefaultSpells();
    }

    private void insertDefaultSpells() {
        insertSpellsFromResource(R.raw.srd);
        insertSpellsFromResource(R.raw.ee);
    }

    private void insertSpellsFromResource(int rawResourceId) {
        SQLiteDatabase db = dbman.getWritableDatabase();
        InputStream zip = getResources().openRawResource(rawResourceId);
        try {
            dbman.execZipPackage(db, zip);
        } catch (IOException e) {
            db.close();
            Log.e("initSpells", "Error processing stream " + getResources().getResourceName(rawResourceId), e);
            ErrorFragment.errorScreen(getActivity().getSupportFragmentManager(),
                    "initSpells: Error processing stream " + getResources().getResourceName(rawResourceId), e);
        }
        db.close();
    }

    private void readSpellsFromDB() {
        SQLiteDatabase db = dbman.getReadableDatabase();
        Cursor row = db.rawQuery(Spell.querySpell(), null);
        Log.d("readSpellsFromDB", row.getCount() + " spells loaded");
        while(row.moveToNext()) {
            Spell s = new Spell(row.getLong(row.getColumnIndex(stripTableFromCol(Spell.COL_ID))));
            s.setName(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_NAME))));
            s.setLevel(row.getInt(row.getColumnIndex(stripTableFromCol(Spell.COL_LEVEL))));
            s.setSchool(row.getString(row.getColumnIndex("school_" + stripTableFromCol(Spell.COL_SCHOOL))));
            s.setCastTime(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_CAST_TIME))));
            s.setConcentration(row.getInt(row.getColumnIndex(stripTableFromCol(Spell.COL_CONCENTRATION))) != 0);
            s.setRitual(row.getInt(row.getColumnIndex(stripTableFromCol(Spell.COL_RITUAL))) != 0);
            s.setDesc(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_DESC))));
            s.setHigherDesc(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_HIGHER_DESC))));
            s.setDuration(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_DURATION))));
            s.setMaterials(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_MATERIALS))));
            s.setMaterialsCost(row.getInt(row.getColumnIndex(stripTableFromCol(Spell.COL_MATERIALS_COST))));
            s.setRange(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_RANGE))));
            s.setReactionDesc(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_REACTION_DESC))));
            spells.add(s);
        }
        row.close();
        for(Spell s : spells)
            setSpellMultivalues(s, db);
        db.close();
    }

    private void setSpellMultivalues(Spell s, SQLiteDatabase db) {
        Cursor row = db.rawQuery(Spell.queryComponent(s.getName()), null);
        while(row.moveToNext()) {
            String comp = row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_COMPONENT_SYMBOL)));
            if(comp == null || comp.isEmpty())
                continue;
            if ("V".equals(comp)) {
                s.setVerbal(true);
            } else if ("S".equals(comp)) {
                s.setSomatic(true);
            } else if ("M".equals(comp)) {
                s.setMaterial(true);
            }
        }
        row.close();
        row = db.rawQuery(Spell.queryTarget(s.getName()), null);
        while(row.moveToNext())
            s.getTargets().add(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_TARGET))));
        row.close();
        row = db.rawQuery(Spell.queryAbility(s.getName()), null);
        while(row.moveToNext())
            s.getAbilitySaves().add(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_ABILITY_SHORTNAME))));
        row.close();
        row = db.rawQuery(Spell.queryAttackType(s.getName()), null);
        while(row.moveToNext())
            s.getAtkTypes().add(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_ATK_TYPE))));
        row.close();
        row = db.rawQuery(Spell.queryDamageType(s.getName()), null);
        while(row.moveToNext())
            s.getDmgTypes().add(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_DMG_TYPE))));
        row.close();
        row = db.rawQuery(Spell.queryCondition(s.getName()), null);
        while(row.moveToNext())
            s.getConditions().add(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_CONDITION))));
        row.close();
        row = db.rawQuery(Spell.querySource(s.getName()), null);
        while(row.moveToNext()) {
            s.getSources().put(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_SOURCE_SHORTNAME))),
                    row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_SOURCE_FULLNAME))));
        }
        row.close();
        row = db.rawQuery(Spell.queryClass(s.getName()), null);
        while(row.moveToNext())
            s.getClasses().add(row.getString(row.getColumnIndex(stripTableFromCol(Spell.COL_CLASS))));
        row.close();
    }

    private String stripTableFromCol(String col) {
        return col.substring(col.lastIndexOf('.') + 1);
    }

    private File getSpellCache() throws IOException {
        File dir = getActivity().getCacheDir();
        if(dir == null)
            throw new IOException("getCacheDir(): Could not get file handler to cache directory");
        File f = new File(dir.getPath() + File.separator + SPELL_CACHE);
        if(f.createNewFile())
            Log.d("getSpellCache", "Cache file \"" + SPELL_CACHE + "\" does not exist. Creating...");
        return f;
    }

    private boolean saveSpellCache() throws IOException {
        File f = getSpellCache();
        if(f.delete() && f.createNewFile()) {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            for(Spell s : spells)
                oos.writeObject(s);
            oos.close();
        } else {
            return false;
        }
        return true;
    }

    private boolean loadSpellCache() throws IOException {
        ArrayList<Spell> tmp = new ArrayList<>();
        File f = getSpellCache();
        if(f.length() == 0)
            return false;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
        while(ois.available() != -1) {
            try {
                tmp.add((Spell)ois.readObject());
            } catch (ClassNotFoundException e) {
                ois.close();
                return false;
            } catch (EOFException e) {
                break;
            }
        }
        ois.close();
        if(tmp.size() != getDBSpellCount())
            return false;
        spells = tmp;
        return true;
    }

    private int getDBSpellCount() {
        int ret = 0;
        SQLiteDatabase db = dbman.getReadableDatabase();
        Cursor res = db.rawQuery(Spell.QUERY_SPELL_COUNT, null);
        if(res.getCount() != 0) {
            res.moveToFirst();
            ret = res.getInt(0);
        }
        res.close();
        db.close();
        return ret;
    }
}
