package ca.printf.dndb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class CommonIO {
    private static final String MANIFEST_FILENAME = "Manifest.xml";
    private static final String MANIFEST_FILE_TAG = "AssetFile";

    public static String sanitizeString(String str) {
        StringTokenizer tok = new StringTokenizer(str, "'");
        String ret = "";
        while(tok.hasMoreTokens())
            ret += (tok.nextToken() + "''");
        if(ret.isEmpty())
            return ret;
        return ret.substring(0, ret.length() - 2);
    }

    public static void execSQLFromFile(Context c, int res_id, SQLiteDatabase db) throws IOException {
        InputStream is = c.getResources().openRawResource(res_id);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        execSQLFromStream(reader, db);
        reader.close();
        is.close();
    }

    private static void execSQLFromStream(BufferedReader reader, SQLiteDatabase db) throws IOException {
        while(reader.ready()) {
            String stmt = getSQLStatement(reader);
            if(!stmt.trim().isEmpty()) {
                Log.d("execSQLFromFile", stmt);
                db.execSQL(stmt);
            }
        }
    }

    private static void execSQLFromString(String str, SQLiteDatabase db) throws IOException {
        StringTokenizer tok = new StringTokenizer(str, "\n", false);
        String stmt = "";
        while(tok.hasMoreTokens()) {
            stmt += tok.nextToken();
            stmt += "\n";
            if(!stmt.trim().isEmpty() && stmt.charAt(stmt.length() - 2) == ';') {
                Log.d("execSQLFromString", stmt);
                db.execSQL(stmt.trim());
                stmt = "";
            }
        }
    }

    private static String getSQLStatement(BufferedReader reader) throws IOException {
        String stmt = "";
        char prev = '\0';
        while(reader.ready()) {
            char next = (char)reader.read();
            if(stmt.isEmpty() && next == '-') {
                reader.readLine();
                continue;
            }
            stmt += next;
            if(next == '\n' && prev == ';')
                break;
            prev = next;
        }
        return stmt;
    }

    private static String getZipPackageManifest(ZipFile zf) throws IOException {
        ZipEntry man = zf.getEntry(MANIFEST_FILENAME);
        if(man == null)
            return null;
        return getZipFileContent(zf, man);
    }

    private static ArrayList<String> getFileListFromManifest(String manifest) throws IOException {
        ArrayList<String> files = new ArrayList<>();
        try {
            XmlPullParser xml = XmlPullParserFactory.newInstance().newPullParser();
            xml.setInput(new StringReader(manifest));
            while(xml.next() != XmlPullParser.END_DOCUMENT) {
                if(xml.getEventType() == XmlPullParser.START_TAG && xml.getName().equals(MANIFEST_FILE_TAG)){
                    if(xml.next() == XmlPullParser.TEXT) {
                        files.add(xml.getText());
                        Log.d("getFileListFromManifest", "Discovered file: " + files.get(files.size() - 1));
                    }
                }
            }
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        }
        return files;
    }

    private static String getZipFileContent(ZipFile zf, ZipEntry ze) throws IOException {
        ZipInputStream zis = new ZipInputStream(zf.getInputStream(ze));
        String ret = _readZipStreamEntry(ze, zis);
        zis.close();
        return ret;
    }

    private static String _readZipStreamEntry(ZipEntry ze, ZipInputStream zis) throws IOException {
        int size = (int)ze.getSize();
        byte[] buf = new byte[size];
        int bytes_read;
        for(bytes_read = 0; bytes_read < size;) {
            int bytes = zis.read(buf, bytes_read, size - bytes_read);
            if(bytes == -1)
                break;
            bytes_read += bytes;
        }
        Log.d("getZipFileContent", bytes_read + " bytes read from " + ze.getName());
        return new String(buf);
    }

    private static ArrayList<String> getZipFilesContents(ZipFile zf, ArrayList<String> filenames) throws IOException {
        ArrayList<String> contents = new ArrayList<>();
        for(String file : filenames) {
            ZipEntry entry = zf.getEntry(file);
            if(entry == null) {
                Log.w("getZipFilesContents", file + " not found in " + zf.getName());
                continue;
            }
            String s = getZipFileContent(zf, entry);
            if(s.isEmpty())
                Log.w("getZipFilesContents", "No data read from " + file);
            else
                contents.add(s);
        }
        return contents;
    }

    public static void execSQLFromZipFile(ZipFile zf, SQLiteDatabase db) throws IOException {
        String man = getZipPackageManifest(zf);
        if(man == null || man.isEmpty())
            throw new IOException("Could not read " + MANIFEST_FILENAME + " from zip file " + zf.getName());
        ArrayList<String> files = getFileListFromManifest(man);
        Log.d("execSQLFromZipFile", "Discovered " + files.size() + " files");
        if(files.isEmpty())
            return;
        files = getZipFilesContents(zf, files);
        for(String s : files)
            execSQLFromStream(new BufferedReader(new StringReader(s)), db);
    }

    public static void execSQLFromZipStream(InputStream zipfile, SQLiteDatabase db) throws IOException {
        Map<String, String> files = new HashMap<>();
        ZipInputStream zis = new ZipInputStream(zipfile);
        while(true) {
            ZipEntry ze = zis.getNextEntry();
            if (ze == null)
                break;
            String filename = ze.getName();
            String contents = _readZipStreamEntry(ze, zis);
            files.put(filename, contents);
            zis.closeEntry();
        }
        String man = files.get(MANIFEST_FILENAME);
        if(man == null || man.isEmpty()) {
            Log.e("execSQLFromZipStream", MANIFEST_FILENAME + " not found in ZipInputStream");
            return;
        }
        ArrayList<String> file_list = getFileListFromManifest(man);
        for(String s : file_list) {
            String contents = files.get(s);
            if(contents == null || contents.isEmpty()) {
                Log.w("execSQLFromZipStream", "Could not find contents of " + s + " in ZipInputStream");
            } else {
                Log.d("execSQLFromZipStream", "Processing " + s + " (" + contents.length() + " chars)");
                execSQLFromString(contents, db);
            }
        }
    }
}
