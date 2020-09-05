package ca.printf.dndb.logic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
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
import java.util.Comparator;
import java.util.List;
import ca.printf.dndb.R;
import ca.printf.dndb.entity.Spell;
import ca.printf.dndb.io.DndbSQLManager;
import ca.printf.dndb.view.ErrorPage;

public class SpellListController {
    private static final String SPELL_CACHE = "spell_cache";
    private static List<Spell> spells = new ArrayList<>();

    SpellListController() {}
    public static void setSpellList(List<Spell> spellList) {spells = spellList;}
    public static int size() {return spells.size();}
    public static void add(Spell spell) {spells.add(spell);}
    public static Spell get(int index) {return spells.get(index);}
    public static void sort(Comparator<Spell> c) {Collections.sort(spells, c);}

    public static void initSpells(FragmentActivity activity) {
        try {
            initDBDefaultSpells(activity);
        } catch (Exception e) {
            ErrorPage.errorScreen(activity.getSupportFragmentManager(),
                    "initDBDefaultSpells(): Error creating spells database", e);
        }
        try {
            if(loadSpellCache(activity)) {
                Log.d("loadSpellCache", "Spell cache successfully loaded");
                return;
            }
        } catch (Exception e) {
            Log.e("loadSpellCache", "Error while reading spell cache", e);
        }
        reloadFromDB(activity);
        try {
            if(saveSpellCache(activity))
                Log.d("saveSpellCache", "Spell cache was written successfully");
        } catch (Exception e) {
            Log.e("saveSpellCache", "Error while writing to spell cache", e);
        }
    }

    public static void reloadFromDB(FragmentActivity activity) {
        spells.clear();
        try {
            readSpellsFromDB(activity);
        } catch (Exception e) {
            ErrorPage.errorScreen(activity.getSupportFragmentManager(),
                    "readSpellsFromDB: Error reading spells from database", e);
        }
    }

    private static File getSpellCache(Context ctx) throws IOException {
        File dir = ctx.getCacheDir();
        if(dir == null)
            throw new IOException("getCacheDir(): Could not get file handler to cache directory");
        File f = new File(dir.getPath() + File.separator + SPELL_CACHE);
        if(f.createNewFile())
            Log.d("getSpellCache", "Cache file \"" + SPELL_CACHE + "\" does not exist. Creating...");
        return f;
    }

    private static boolean saveSpellCache(Context ctx) throws IOException {
        File f = getSpellCache(ctx);
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

    private static boolean loadSpellCache(FragmentActivity activity) throws IOException {
        ArrayList<Spell> tmp = new ArrayList<>();
        File f = getSpellCache(activity);
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
        if(tmp.size() != getDBSpellCount(activity))
            return false;
        spells = tmp;
        return true;
    }

    private static int getDBSpellCount(FragmentActivity activity) {
        int ret = 0;
        DndbSQLManager dbman = new DndbSQLManager(activity);
        SQLiteDatabase db = dbman.getReadableDatabase();
        Cursor res = db.rawQuery(Spell.QUERY_SPELL_COUNT, null);
        if(res.getCount() != 0) {
            res.moveToFirst();
            ret = res.getInt(0);
        }
        res.close();
        db.close();
        dbman.close();
        return ret;
    }

    private static void initDBDefaultSpells(FragmentActivity a) {
        DndbSQLManager dbman = new DndbSQLManager(a);
        SQLiteDatabase db = dbman.getReadableDatabase();
        if(dbman.dbHasSpells(db)) {
            db.close();
            return;
        }
        db.close();
        insertDefaultSpells(dbman, a);
        dbman.close();
    }

    private static void insertDefaultSpells(DndbSQLManager dbman, FragmentActivity a) {
        insertSpellsFromResource(R.raw.srd, dbman, a);
        insertSpellsFromResource(R.raw.ee, dbman, a);
    }

    private static void insertSpellsFromResource(int rawResourceId, DndbSQLManager dbman, FragmentActivity a) {
        SQLiteDatabase db = dbman.getWritableDatabase();
        InputStream zip = a.getResources().openRawResource(rawResourceId);
        try {
            dbman.execZipPackage(db, zip);
        } catch (IOException e) {
            db.close();
            Log.e("initSpells", "Error processing stream " + a.getResources().getResourceName(rawResourceId), e);
            ErrorPage.errorScreen(a.getSupportFragmentManager(),
                    "initSpells: Error processing stream " + a.getResources().getResourceName(rawResourceId), e);
        }
        db.close();
    }

    private static void readSpellsFromDB(FragmentActivity a) {
        DndbSQLManager dbman = new DndbSQLManager(a);
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
            SpellListController.add(s);
        }
        row.close();
        for(Spell s : spells)
            setSpellMultivalues(s, db);
        db.close();
        dbman.close();
    }

    private static void setSpellMultivalues(Spell s, SQLiteDatabase db) {
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

    private static String stripTableFromCol(String col) {
        return col.substring(col.lastIndexOf('.') + 1);
    }
}
