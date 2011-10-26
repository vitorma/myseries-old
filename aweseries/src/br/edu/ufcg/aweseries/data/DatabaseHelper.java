package br.edu.ufcg.aweseries.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.EpisodeBuilder;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesBuilder;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aweseries_db";

    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_SERIES =
        "CREATE TABLE Series (" +
        "id TEXT PRIMARY KEY, " +
        "name TEXT NOT NULL, " +
        "status TEXT, " +
        "airsDay TEXT, " +
        "airsTime TEXT, " +
        "firstAired TEXT, " +
        "runtime TEXT, " +
        "network TEXT, " +
        "overview TEXT, " +
        "genres TEXT, " +
        "actors TEXT, " +
        "poster BLOB);";

    private static final String CREATE_TABLE_EPISODES =
        "CREATE TABLE Episode (" +
        "id TEXT PRIMARY KEY, " +
        "seriesId TEXT NOT NULL, " +
        "number INTEGER NOT NULL, " +
        "seasonNumber INTEGER NOT NULL, " +
        "name TEXT, " +
        "firstAired TEXT, " +
        "overview TEXT, " +
        "director TEXT, " +
        "writer TEXT, " +
        "guestStars TEXT, " +
        "poster TEXT, " +
        "viewed TEXT, " +
        "FOREIGN KEY(seriesId) REFERENCES Series(id) ON DELETE CASCADE ON UPDATE CASCADE);";

    private static final String DROP_TABLE_SERIES =
        "DROP TABLE IF EXISTS Series";

    private static final String DROP_TABLE_EPISODE =
        "DROP TABLE IF EXISTS Episode";

    private static final String SELECT_ALL_EPISODES_OF_A_SERIES =
        "SELECT * FROM Episode WHERE seriesId=?";

    private static final String SELECT_A_SERIES_BY_ID =
        "SELECT * FROM Series WHERE id=?";

    private static final String SELECT_ALL_SERIES =
        "SELECT * FROM Series";

    private static final String SELECT_AN_EPISODE_BY_ID =
        "SELECT * FROM Episode WHERE id=?";

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

    public void insert(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("Series", null, this.contentValuesBy(series));
        for (Episode e : series.getSeasons().getAllEpisodes()) {
            db.insert("Episode", null, this.contentValuesBy(e));
        }
        db.close();
    }

    public void update(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.update("Series", this.contentValuesBy(series), "id=?", new String[] {series.getId()});
        db.close();
    }

    public void updateFull(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.update("Series", this.contentValuesBy(series), "id=?", new String[] {series.getId()});
        for (Episode e : series.getSeasons().getAllEpisodes()) {
            db.update("Episode", this.contentValuesBy(e), "id=?", new String[] {e.getId()});
        }
        db.close();
    }

    public void update(Episode episode) {
        if (episode == null) {
            throw new IllegalArgumentException("episode should not be null");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.update("Episode", this.contentValuesBy(episode), "id=?", new String[] {episode.getId()});
        db.close();
    }

    public void updateAll(List<Episode> episodes) {
        if (episodes == null) {
            throw new IllegalArgumentException("episodes should not be null");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        for (Episode e : episodes) {
            db.update("Episode", this.contentValuesBy(e), "id=?", new String[] {e.getId()});
        }
        db.close();
    }

    public void delete(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Series", "id=?", new String[] {series.getId()});
        db.close();
    }

    public void deleteAllSeries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Series", null, null);
        db.close();
    }

    public int countSeries() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor c = db.rawQuery(SELECT_ALL_SERIES, null);
        final int count = c.getCount();
        c.close();
        db.close();
        return count;
    }

    public boolean hasSeries() {
        return this.countSeries() > 0;
    }

    public Series getSeries(String seriesId) {
        if (seriesId == null) {
            throw new IllegalArgumentException("series id should not be null");
        }

        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor c = db.rawQuery(SELECT_A_SERIES_BY_ID, new String[] {seriesId});
        if (!c.moveToFirst()) {
            Log.d("DatabaseHelper", "There is no series with id " + seriesId);
            c.close();
            return null;
        }
        final Series s = this.seriesByCurrentPositionOf(c);
        s.getSeasons().addAllEpisodes(this.getAllEpisodesOf(s, db));
        c.close();
        db.close();
        return s;
    }

    public Episode getEpisode(String episodeId) {
        if (episodeId == null) {
            throw new IllegalArgumentException("episode id should not be null");
        }

        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor c = db.rawQuery(SELECT_AN_EPISODE_BY_ID, new String[] {episodeId});
        if (!c.moveToFirst()) {
            Log.d("DatabaseHelper", "There is no episode with id " + episodeId);
            c.close();
            return null;
        }
        final Episode e = this.episodeByCurrentPositionOf(c);
        c.close();
        db.close();
        return e;
    }

    public List<Series> getAllSeries() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor c = db.rawQuery(SELECT_ALL_SERIES, null);
        final List<Series> allSeries = new ArrayList<Series>();
        while (c.moveToNext()) {
            final Series s = this.seriesByCurrentPositionOf(c);
            s.getSeasons().addAllEpisodes(this.getAllEpisodesOf(s, db));
            allSeries.add(s);
        }
        c.close();
        db.close();
        return allSeries;
    }

    private List<Episode> getAllEpisodesOf(Series s, SQLiteDatabase db) {
        final Cursor c = db.rawQuery(SELECT_ALL_EPISODES_OF_A_SERIES, new String[] {s.getId()});
        final List<Episode> allEpisodes = new ArrayList<Episode>();
        while(c.moveToNext()) {
            allEpisodes.add(this.episodeByCurrentPositionOf(c));
        }
        c.close();
        return allEpisodes;
    }

    private ContentValues contentValuesBy(Series s) {
        final ContentValues cv = new ContentValues();
        cv.put("id", s.getId());
        cv.put("name", s.getName());
        cv.put("status", s.getStatus());
        cv.put("airsDay", s.getAirsDay());
        cv.put("airsTime", s.getAirsTime());
        cv.put("firstAired", s.getFirstAired());
        cv.put("runtime", s.getRuntime());
        cv.put("network", s.getNetwork());
        cv.put("overview", s.getOverview());
        cv.put("genres", s.getGenres());
        cv.put("actors", s.getActors());
        cv.put("poster", (s.getPoster() != null) ? s.getPoster().toByteArray(): null);
        return cv;
    }

    private ContentValues contentValuesBy(Episode e) {
        final ContentValues cv = new ContentValues();
        cv.put("id", e.getId());
        cv.put("seriesId", e.getSeriesId());
        cv.put("number", e.getNumber());
        cv.put("seasonNumber", e.getSeasonNumber());
        cv.put("name", e.getName());
        cv.put("firstAired", e.getFirstAired());
        cv.put("overview", e.getOverview());
        cv.put("director", e.getDirector());
        cv.put("writer", e.getWriter());
        cv.put("guestStars", e.getGuestStars());
        cv.put("poster", e.getPoster());
        cv.put("viewed", String.valueOf(e.wasSeen()));
        return cv;
    }

    private Episode episodeByCurrentPositionOf(Cursor c) {
        return new EpisodeBuilder()
            .withId(c.getString(c.getColumnIndex("id")))
            .withSeriesId(c.getString(c.getColumnIndex("seriesId")))
            .withNumber(c.getInt(c.getColumnIndex("number")))
            .withSeasonNumber(c.getInt(c.getColumnIndex("seasonNumber")))
            .withName(c.getString(c.getColumnIndex("name")))
            .withFirstAired(c.getString(c.getColumnIndex("firstAired")))
            .withOverview(c.getString(c.getColumnIndex("overview")))
            .withDirector(c.getString(c.getColumnIndex("director")))
            .withWriter(c.getString(c.getColumnIndex("writer")))
            .withGuestStars(c.getString(c.getColumnIndex("guestStars")))
            .withPoster(c.getString(c.getColumnIndex("poster")))
            .withViewed(Boolean.valueOf(c.getString(c.getColumnIndex("viewed"))))
            .build();
    }

    private Series seriesByCurrentPositionOf(Cursor c) {
        return new SeriesBuilder()
            .withId(c.getString(c.getColumnIndex("id")))
            .withName(c.getString(c.getColumnIndex("name")))
            .withStatus(c.getString(c.getColumnIndex("status")))
            .withAirsDay(c.getString(c.getColumnIndex("airsDay")))
            .withAirsTime(c.getString(c.getColumnIndex("airsTime")))
            .withFirstAired(c.getString(c.getColumnIndex("firstAired")))
            .withRuntime(c.getString(c.getColumnIndex("runtime")))
            .withNetwork(c.getString(c.getColumnIndex("network")))
            .withOverview(c.getString(c.getColumnIndex("overview")))
            .withGenres(c.getString(c.getColumnIndex("genres")))
            .withActors(c.getString(c.getColumnIndex("actors")))
            .withPoster(c.getBlob(c.getColumnIndex("poster")))
            .build();
    }
}
