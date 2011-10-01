package br.edu.ufcg.aweseries.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_SERIES =
        "CREATE TABLE Series (" +
        "id TEXT PRIMARY KEY, " +
        "name TEXT NOT NULL, " +
        "status TEXT NOT NULL, " +
        "airsDay TEXT NOT NULL, " +
        "airsTime TEXT NOT NULL, " +
        "firstAired TEXT NOT NULL, " +
        "runtime TEXT NOT NULL, " +
        "network TEXT NOT NULL, " +
        "overview TEXT NOT NULL, " +
        "genres TEXT NOT NULL, " +
        "actors TEXT NOT NULL, " +
        "poster TEXT NOT NULL);";

    private static final String CREATE_TABLE_EPISODES =
        "CREATE TABLE Episode (" +
        "id TEXT PRIMARY KEY, " +
        "seriesId TEXT NOT NULL, " +
        "number INTEGER NOT NULL, " +
        "seasonNumber INTEGER NOT NULL, " +
        "name TEXT NOT NULL, " +
        "firstAired TEXT NOT NULL, " +
        "overview TEXT NOT NULL, " +
        "director TEXT NOT NULL, " +
        "writer TEXT NOT NULL, " +
        "guestStars TEXT NOT NULL, " +
        "poster TEXT NOT NULL, " +
        "viewed TEXT NOT NULL, " +
        "FOREIGN KEY(seriesId) REFERENCES Series(id) ON DELETE CASCADE);";

    private static final String DROP_TABLE_SERIES = "DROP TABLE IF EXISTS Series";

    private static final String DROP_TABLE_EPISODE = "DROP TABLE IF EXISTS Episode";

    public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SERIES);
        db.execSQL(CREATE_TABLE_EPISODES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_EPISODE);
        db.execSQL(DROP_TABLE_SERIES);
        this.onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
