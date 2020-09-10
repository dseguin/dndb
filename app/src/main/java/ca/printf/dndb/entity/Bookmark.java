package ca.printf.dndb.entity;

import java.util.ArrayList;
import java.util.List;
import ca.printf.dndb.io.DndbSQLManager;

public class Bookmark {
    public static final String COL_ID = DndbSQLManager.TABLE_BOOKMARK + ".rowid";
    public static final String COL_BOOKMARK_NAME = DndbSQLManager.TABLE_BOOKMARK + ".name";
    public static final String COL_BOOKMARK_SPELL_ID = DndbSQLManager.TABLE_BOOKMARK_SPELL + ".rowid";
    public static final String CREATE_BOOKMARK_TABLE =
            "DROP TABLE IF EXISTS " + DndbSQLManager.TABLE_BOOKMARK + ";\n" +
            "CREATE TABLE " + DndbSQLManager.TABLE_BOOKMARK + " (\n "
                    + DndbSQLManager.stripTableFromCol(COL_BOOKMARK_NAME) + " TEXT\n" +
            ");";
    public static final String CREATE_BOOKMARK_SPELL_TABLE =
            "DROP TABLE IF EXISTS " + DndbSQLManager.TABLE_BOOKMARK_SPELL + ";\n" +
            "CREATE TABLE " + DndbSQLManager.TABLE_BOOKMARK_SPELL + " (\n "
                    + DndbSQLManager.TABLE_BOOKMARK + "_id INTEGER NOT NULL,\n "
                    + DndbSQLManager.TABLE_SPELL + "_id INTEGER NOT NULL,\n "
                    + "UNIQUE(" + DndbSQLManager.TABLE_BOOKMARK + "_id," + DndbSQLManager.TABLE_SPELL + "_id)\n" +
            ");";
    public static final String JOIN_BOOKMARK_SPELL = "INNER JOIN " + DndbSQLManager.TABLE_BOOKMARK_SPELL +
            " ON " + COL_ID + " = " + DndbSQLManager.TABLE_BOOKMARK_SPELL + "." +
            DndbSQLManager.TABLE_BOOKMARK + "_id INNER JOIN " + DndbSQLManager.TABLE_SPELL + " ON " +
            Spell.COL_ID + " = " + DndbSQLManager.TABLE_BOOKMARK_SPELL + "." + DndbSQLManager.TABLE_SPELL + "_id";
    public static final String[] QUERY_BOOKMARKS_COLS = {DndbSQLManager.stripTableFromCol(COL_ID), DndbSQLManager.stripTableFromCol(COL_BOOKMARK_NAME)};
    public static final String[] QUERY_BOOKMARK_SPELLS_COLS = {COL_BOOKMARK_SPELL_ID, COL_BOOKMARK_NAME, Spell.COL_NAME};
    public static final String QUERY_BOOKMARKS = "SELECT " + COLATE_COLS(QUERY_BOOKMARKS_COLS) +
            " FROM " + DndbSQLManager.TABLE_BOOKMARK + ";";
    private long id;
    private String name;
    private List<Spell> spells;

    public Bookmark() {this(-1);}
    public Bookmark(long id) {
        this.id = id;
        this.spells = new ArrayList<>();
    }

    public long getId() {return this.id;}
    public String getName() {return this.name;}
    public List<Spell> getSpellList() {return this.spells;}
    public void setId(long id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setSpellList(List<Spell> spellList) {this.spells = spellList;}

    public static String query_bookmark_spells(long bookmarkid) {
        return "SELECT " + COLATE_COLS(QUERY_BOOKMARK_SPELLS_COLS) + " FROM " +
                DndbSQLManager.TABLE_BOOKMARK + " " + JOIN_BOOKMARK_SPELL + " WHERE " +
                DndbSQLManager.TABLE_BOOKMARK_SPELL + "." + DndbSQLManager.TABLE_BOOKMARK +
                "_id = " + bookmarkid + ";";
    }

    public static String insert_bookmark_spell(String bookmarkname, String spellname) {
        String bookmark_id = "(SELECT " + DndbSQLManager.stripTableFromCol(COL_ID) + " FROM " +
                DndbSQLManager.TABLE_BOOKMARK + " WHERE " +
                DndbSQLManager.stripTableFromCol(COL_BOOKMARK_NAME) + " LIKE '" + bookmarkname + "')";
        String spell_id = "(SELECT " + DndbSQLManager.stripTableFromCol(Spell.COL_ID) + " FROM " +
                DndbSQLManager.TABLE_SPELL + " WHERE " +
                DndbSQLManager.stripTableFromCol(Spell.COL_NAME) + " LIKE '" + spellname + "')";
        return _insert_bookmark_spell(bookmark_id, spell_id);
    }

    public static String insert_bookmark_spell(long bookmarkid, String spellname) {
        String spell_id = "(SELECT " + DndbSQLManager.stripTableFromCol(Spell.COL_ID) + " FROM " +
                DndbSQLManager.TABLE_SPELL + " WHERE " +
                DndbSQLManager.stripTableFromCol(Spell.COL_NAME) + " LIKE '" + spellname + "')";
        return _insert_bookmark_spell(Long.toString(bookmarkid), spell_id);
    }

    private static String _insert_bookmark_spell(String bookmark_id, String spell_id) {
        return "INSERT OR IGNORE INTO " + DndbSQLManager.TABLE_BOOKMARK_SPELL +
                " (" + DndbSQLManager.TABLE_BOOKMARK + "_id," + DndbSQLManager.TABLE_SPELL + "_id) VALUES\n " +
                "(" + bookmark_id + "," + spell_id + ");";
    }

    public static String delete_bookmark_spell(long bookmarkid, String spellname) {
        return "DELETE FROM " + DndbSQLManager.TABLE_BOOKMARK_SPELL + " WHERE " +
                DndbSQLManager.TABLE_BOOKMARK + "_id = " + bookmarkid + " AND " +
                DndbSQLManager.TABLE_SPELL + "_id = (SELECT " +
                DndbSQLManager.stripTableFromCol(Spell.COL_ID) + " FROM " +
                DndbSQLManager.TABLE_SPELL + " WHERE " +
                DndbSQLManager.stripTableFromCol(Spell.COL_NAME) + " LIKE '" + spellname + "');";
    }

    public static String insert_new_bookmark(String bookmarkname) {
        return "INSERT OR IGNORE INTO " + DndbSQLManager.TABLE_BOOKMARK +
                " (" + DndbSQLManager.stripTableFromCol(COL_BOOKMARK_NAME) +
                ") VALUES\n ('" + bookmarkname + "');";
    }

    public static String delete_bookmark(long id) {
        return "DELETE FROM " + DndbSQLManager.TABLE_BOOKMARK + " WHERE " +
                DndbSQLManager.stripTableFromCol(COL_ID) + " = " + id + ";";
    }

    private static final String COLATE_COLS(final String[] COLS) {
        String ret = "";
        for(String s : COLS)
            ret += (s + ",");
        if(ret.isEmpty())
            return ret;
        return ret.substring(0, ret.length() - 1);
    }
}
