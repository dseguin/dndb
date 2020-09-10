package ca.printf.dndb.logic;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.fragment.app.FragmentActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ca.printf.dndb.entity.Bookmark;
import ca.printf.dndb.entity.Spell;
import ca.printf.dndb.io.CommonIO;
import ca.printf.dndb.io.DndbSQLManager;
import ca.printf.dndb.view.ErrorPage;

public class BookmarkListController {
    private static List<Bookmark> bookmarks = new ArrayList<>();

    BookmarkListController() {}
    public static void setBookmarkList(List<Bookmark> bookmarkList) {bookmarks = bookmarkList;}
    public static int size() {return bookmarks.size();}
    public static boolean isEmpty() {return bookmarks.isEmpty();}
    public static Bookmark get(int index) {return bookmarks.get(index);}
    public static void sort(Comparator<Bookmark> c) {Collections.sort(bookmarks, c);}
    public static Bookmark add(String bookmarkname, FragmentActivity activity) {
        try {
            Bookmark bookmark = new Bookmark(insertDB(CommonIO.sanitizeString(bookmarkname), activity));
            bookmark.setName(bookmarkname);
            bookmarks.add(bookmark);
            return bookmark;
        } catch (Exception e) {
            ErrorPage.errorScreen(activity.getSupportFragmentManager(),
                    "insertDB: Error adding bookmark \"" + bookmarkname + "\" to database", e);
        }
        return null;
    }

    public static void add(Bookmark bookmark, FragmentActivity activity) {
        try {
            modifyDB(Bookmark.insert_new_bookmark(CommonIO.sanitizeString(bookmark.getName())), activity);
            bookmarks.add(bookmark);
        } catch (Exception e) {
            ErrorPage.errorScreen(activity.getSupportFragmentManager(),
                    "modifyDB: Error adding bookmark \"" + bookmark.getName() + "\" to database", e);
        }
    }

    public static void addSpell(Bookmark bookmark, Spell spell, FragmentActivity activity) {
        try {
            modifyDB(Bookmark.insert_bookmark_spell(bookmark.getId(), CommonIO.sanitizeString(spell.getName())), activity);
            if(!bookmark.getSpellList().contains(spell))
                bookmark.getSpellList().add(spell);
        } catch (Exception e) {
            ErrorPage.errorScreen(activity.getSupportFragmentManager(),
                    "modifyDB: Error adding spell \"" + spell.getName() + "\" to bookmark \"" + bookmark.getName() + "\"", e);
        }
    }

    public static void addSpell(long bookmarkid, Spell spell, FragmentActivity activity) {
        addSpell(get(bookmarks.indexOf(new Bookmark(bookmarkid))), spell, activity);
    }

    public static void remove(int index, FragmentActivity activity) {
        try {
            modifyDB(Bookmark.delete_bookmark(bookmarks.get(index).getId()), activity);
            bookmarks.remove(index);
        } catch (Exception e) {
            ErrorPage.errorScreen(activity.getSupportFragmentManager(),
                    "modifyDB: Error removing bookmark 'id=" + index + "' from database", e);
        }
    }

    public static boolean remove(Bookmark bookmark, FragmentActivity activity) {
        boolean ret = false;
        if(bookmark == null)
            return ret;
        for(int i = 0; i < size(); i++) {
            if(get(i).getId() == bookmark.getId()) {
                remove(i, activity);
                ret = true;
            }
        }
        return ret;
    }

    private static long insertDB(String bookmarkname, FragmentActivity a) {
        DndbSQLManager dbman = new DndbSQLManager(a);
        SQLiteDatabase db = dbman.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DndbSQLManager.stripTableFromCol(Bookmark.COL_BOOKMARK_NAME), bookmarkname);
        long id = db.insert(DndbSQLManager.TABLE_BOOKMARK, null, cv);
        db.close();
        dbman.close();
        return id;
    }

    private static void modifyDB(String sqlQuery, FragmentActivity a) throws IOException {
        DndbSQLManager dbman = new DndbSQLManager(a);
        SQLiteDatabase db = dbman.getWritableDatabase();
        CommonIO.execSQLFromString(sqlQuery, db);
        db.close();
        dbman.close();
    }

    public static void initBookmarks(FragmentActivity activity) {
        bookmarks.clear();
        DndbSQLManager dbman = new DndbSQLManager(activity);
        SQLiteDatabase db = dbman.getWritableDatabase();
        Cursor row = db.rawQuery(Bookmark.QUERY_BOOKMARKS, null);
        while(row.moveToNext()) {
            Bookmark b = new Bookmark(row.getLong(row.getColumnIndex(DndbSQLManager.stripTableFromCol(Bookmark.COL_ID))));
            b.setName(row.getString(row.getColumnIndex(DndbSQLManager.stripTableFromCol(Bookmark.COL_BOOKMARK_NAME))));
            bookmarks.add(b);
        }
        row.close();
        for(Bookmark b : bookmarks) {
            row = db.rawQuery(Bookmark.query_bookmark_spells(b.getId()), null);
            while(row.moveToNext()) {
                Spell s = new Spell();
                s.setName(row.getString(row.getColumnIndex(DndbSQLManager.stripTableFromCol(Spell.COL_NAME))));
                s = SpellListController.get(s);
                if(s != null)
                    b.getSpellList().add(s);
            }
            row.close();
        }
        db.close();
        dbman.close();
    }
}
