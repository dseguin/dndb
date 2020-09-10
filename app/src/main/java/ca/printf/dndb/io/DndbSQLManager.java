package ca.printf.dndb.io;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;
import ca.printf.dndb.R;
import ca.printf.dndb.entity.Bookmark;
import ca.printf.dndb.view.ErrorPage;

public class DndbSQLManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "dndb.sqlite";
    private static final int DB_VER = 2;
    public static final String TABLE_SPELL = "spell";
    public static final String TABLE_SCHOOL = "school";
    public static final String TABLE_SPELL_TARGET = "spell_target";
    public static final String TABLE_TARGET = "target";
    public static final String TABLE_SPELL_ABILITY = "spell_ability";
    public static final String TABLE_ABILITY = "ability";
    public static final String TABLE_SPELL_ATTACK_TYPE = "spell_attack_type";
    public static final String TABLE_ATTACK_TYPE = "attack_type";
    public static final String TABLE_SPELL_DAMAGE_TYPE = "spell_damage_type";
    public static final String TABLE_DAMAGE_TYPE = "damage_type";
    public static final String TABLE_SPELL_CONDITION = "spell_condition";
    public static final String TABLE_CONDITION = "condition";
    public static final String TABLE_SPELL_SOURCE = "spell_source";
    public static final String TABLE_SOURCE = "source";
    public static final String TABLE_SPELL_CLASS_LIST = "spell_class_list";
    public static final String TABLE_CLASS_LIST = "class_list";
    public static final String TABLE_SPELL_COMPONENT = "spell_component";
    public static final String TABLE_COMPONENT = "component";
    public static final String TABLE_BOOKMARK = "bookmark";
    public static final String TABLE_BOOKMARK_SPELL = "bookmark_spell";
    private FragmentActivity act;

    public DndbSQLManager(FragmentActivity a) {
        super(a, DB_NAME, null, DB_VER);
        this.act = a;
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            CommonIO.execSQLFromFile(act, R.raw.spells_ddl, db);
            CommonIO.execSQLFromFile(act, R.raw.spells_init_dml, db);
            createBookmarkTable(db);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error creating database ", e);
            ErrorPage.errorScreen(act.getSupportFragmentManager(), "Error creating database", e);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != 1)
            onCreate(db);
        try {
            createBookmarkTable(db);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error upgrading database ", e);
            ErrorPage.errorScreen(act.getSupportFragmentManager(), "Error upgrading database", e);
        }
    }

    private void createBookmarkTable(SQLiteDatabase db) throws IOException {
        db.beginTransaction();
        try {
            String sql = Bookmark.CREATE_BOOKMARK_TABLE + "\n\n" + Bookmark.CREATE_BOOKMARK_SPELL_TABLE;
            CommonIO.execSQLFromString(sql, db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public boolean dbHasSpells(SQLiteDatabase db) {
        Cursor res = db.rawQuery("SELECT rowid FROM " + TABLE_SPELL + ";", null);
        boolean ret = res.getCount() > 0;
        res.close();
        return ret;
    }

    public void execZipPackage(SQLiteDatabase db, File zipfile) throws IOException {
        ZipFile zf = new ZipFile(zipfile);
        CommonIO.execSQLFromZipFile(zf, db);
    }

    public void execZipPackage(SQLiteDatabase db, InputStream zipfile) throws IOException {
        CommonIO.execSQLFromZipStream(zipfile, db);
    }

    public static String stripTableFromCol(String col) {
        return col.substring(col.lastIndexOf('.') + 1);
    }
}
