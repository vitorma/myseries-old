package br.edu.ufcg.aweseries.data;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aweseries_db";

    private static final int DATABASE_VERSION = 1;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    public void addSeries(Series series) {
        //TODO Implement
    }

    public void addEpisode(Episode episode) {
        //TODO Implement (does it need be public?)
    }

    public void removeSeries(Series series) {
        //TODO Implement
    }

    public void removeEpisode(Episode episode) {
        //TODO Implement (does it need be public?)
    }

    public void updateSeries(Series series) {
        //TODO Implement
    }

    public void updateEpisode(Episode episode) {
        //TODO Implement (does it need be public?)
    }

    public Series getSeries(String id) {
        //TODO Implement
        return null;
    }

    public Episode getEpisode(String id) {
        //TODO Implement (does it need be public?)
        return null;
    }

    public List<Series> getAllSeries() {
        //TODO Implement
        return null;
    }

    public List<Series> getAllEpisodes(String seriesId) {
        //TODO Implement (does it need be public?)
        return null;
    }

    //TODO Another methods that i don't know what
}
