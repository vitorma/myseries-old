/*
 *   SeriesDatabase.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.domain.repository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Numbers;
import mobi.myseries.shared.Validate;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SeriesDatabase extends SQLiteOpenHelper implements SeriesRepository {
    private static final String DATABASE_NAME = "myseries_db";
    private static final int DATABASE_VERSION = 1;

    private static final String SERIES = "Series";
    private static final String SERIES_ID = "id";
    private static final String SERIES_NAME = "name";
    private static final String SERIES_STATUS = "status";
    private static final String SERIES_AIRDAY = "airday";
    private static final String SERIES_AIRTIME = "airtime";
    private static final String SERIES_AIRDATE = "airdate";
    private static final String SERIES_RUNTIME = "runtime";
    private static final String SERIES_NETWORK = "network";
    private static final String SERIES_OVERVIEW = "overview";
    private static final String SERIES_GENRES = "genres";
    private static final String SERIES_ACTORS = "actors";
    private static final String SERIES_POSTER = "poster";

    private static final String EPISODE = "Episode";
    private static final String EPISODE_ID = "id";
    private static final String EPISODE_SERIES = "series";
    private static final String EPISODE_NUMBER = "number";
    private static final String EPISODE_SEASON = "season";
    private static final String EPISODE_NAME = "name";
    private static final String EPISODE_AIRDATE = "airdate";
    private static final String EPISODE_OVERVIEW = "overview";
    private static final String EPISODE_DIRECTORS = "directors";
    private static final String EPISODE_WRITERS = "writers";
    private static final String EPISODE_GUESTSTARS = "gueststars";
    private static final String EPISODE_IMAGE = "image";
    private static final String EPISODE_SEENMARK = "seenmark";

    private static final String CREATE_TABLE_SERIES =
        "CREATE TABLE " + SERIES + " (" +
            SERIES_ID +       " INTEGER PRIMARY KEY, " +
            SERIES_NAME +     " TEXT NOT NULL, " +
            SERIES_STATUS +   " TEXT, " +
            SERIES_AIRDAY +   " TEXT, " +
            SERIES_AIRTIME +  " TEXT, " +
            SERIES_AIRDATE +  " INTEGER, " +
            SERIES_RUNTIME +  " TEXT, " +
            SERIES_NETWORK +  " TEXT, " +
            SERIES_OVERVIEW + " TEXT, " +
            SERIES_GENRES +   " TEXT, " +
            SERIES_ACTORS +   " TEXT, " +
            SERIES_POSTER +   " TEXT);";

    private static final String CREATE_TABLE_EPISODES =
        "CREATE TABLE " + EPISODE + " (" +
            EPISODE_ID +         " INTEGER PRIMARY KEY, " +
            EPISODE_SERIES +     " INTEGER NOT NULL, " +
            EPISODE_NUMBER +     " INTEGER NOT NULL, " +
            EPISODE_SEASON +     " INTEGER NOT NULL, " +
            EPISODE_NAME +       " TEXT, " +
            EPISODE_AIRDATE +    " INTEGER, " +
            EPISODE_OVERVIEW +   " TEXT, " +
            EPISODE_DIRECTORS +  " TEXT, " +
            EPISODE_WRITERS +    " TEXT, " +
            EPISODE_GUESTSTARS + " TEXT, " +
            EPISODE_IMAGE +      " TEXT, " +
            EPISODE_SEENMARK +   " TEXT, " +
            "FOREIGN KEY(" + EPISODE_SERIES + ") REFERENCES " + SERIES + "(" + SERIES_ID + ") ON DELETE CASCADE);";

    private static final String DROP_TABLE_SERIES =
        "DROP TABLE IF EXISTS " + SERIES;

    private static final String DROP_TABLE_EPISODE =
        "DROP TABLE IF EXISTS " + EPISODE;

    private static final String SELECT_ALL_EPISODES_OF_A_SERIES =
        "SELECT * FROM " + EPISODE + " WHERE " + EPISODE_SERIES + "=?";

    private static final String SELECT_A_SERIES_BY_ID =
        "SELECT * FROM " + SERIES + " WHERE " + SERIES_ID + "=?";

    private static final String SELECT_ALL_SERIES =
        "SELECT * FROM " + SERIES;

    public SeriesDatabase(Context context) {
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

    @Override
    public void insert(Series series) {
        Validate.isNonNull(series, "series");

        SQLiteDatabase db = this.getWritableDatabase();
        this.insert(series, db);
        db.close();
    }

    private void insert(Series series, SQLiteDatabase db) {
        db.insert(SERIES, null, this.contentValuesBy(series));

        for (Episode e : series.episodes()) {
            db.insert(EPISODE, null, this.contentValuesBy(e));
        }
    }

    @Override
    public void update(Series series) {
        Validate.isNonNull(series, "series");

        SQLiteDatabase db = this.getWritableDatabase();
        this.update(series, db);
        db.close();
    }

    private void update(Series series, SQLiteDatabase db) {
        db.delete(EPISODE, EPISODE_SERIES + "=?", new String[] {String.valueOf(series.id())});
        db.update(SERIES, this.contentValuesBy(series), SERIES_ID + "=?", new String[] {String.valueOf(series.id())});

        for (Episode e : series.episodes()) {
            db.insert(EPISODE, null, this.contentValuesBy(e));
        }
    }

    @Override
    public void updateAll(Collection<Series> seriesCollection) {
        Validate.allNonNull(seriesCollection, "seriesCollection");

        SQLiteDatabase db = this.getWritableDatabase();
        this.updateAll(seriesCollection, db);
        db.close();
    }

    private void updateAll(Collection<Series> seriesCollection, SQLiteDatabase db) {
        for (Series series : seriesCollection) {
            this.update(series, db);
        }
    }

    @Override
    public void delete(Series series) {
        Validate.isNonNull(series, "series");

        SQLiteDatabase db = this.getWritableDatabase();
        this.delete(series, db);
        db.close();
    }

    private void delete(Series series, SQLiteDatabase db) {
        db.delete(SERIES, SERIES_ID + "=?", new String[] {String.valueOf(series.id())});
    }

    @Override
    public void deleteAll(Collection<Series> seriesCollection) {
        Validate.allNonNull(seriesCollection, "seriesCollection");

        SQLiteDatabase db = this.getWritableDatabase();
        this.deleteAll(seriesCollection, db);
        db.close();
    }

    private void deleteAll(Collection<Series> seriesCollection, SQLiteDatabase db) {
        for (Series series : seriesCollection) {
            this.delete(series, db);
        }
    }

    @Override
    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SERIES, null, null);
        db.close();
    }

    @Override
    public boolean contains(Series series) {
        Validate.isNonNull(series, "series");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_A_SERIES_BY_ID, new String[] {String.valueOf(series.id())});

        boolean contains = c.getCount() > 0;

        c.close();
        db.close();

        return contains;
    }

    @Override
    public Series get(int seriesId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_A_SERIES_BY_ID, new String[] {String.valueOf(seriesId)});

        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Series series = this.seriesByCurrentPositionOf(c).includingAll(this.episodesWithSeriesId(seriesId, db));

        c.close();
        db.close();

        return series;
    }

    @Override
    public List<Series> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_ALL_SERIES, null);

        List<Series> result = new LinkedList<Series>();

        while (c.moveToNext()) {
            Series series = this.seriesByCurrentPositionOf(c);
            result.add(series.includingAll(this.episodesWithSeriesId(series.id(), db)));
        }

        c.close();
        db.close();

        return result;
    }

    private List<Episode> episodesWithSeriesId(int seriesId, SQLiteDatabase db) {
        Cursor c = db.rawQuery(SELECT_ALL_EPISODES_OF_A_SERIES, new String[] {String.valueOf(seriesId)});

        List<Episode> result = new LinkedList<Episode>();

        while(c.moveToNext()) {
            result.add(this.episodeByCurrentPositionOf(c));
        }

        c.close();

        return result;
    }

    private ContentValues contentValuesBy(Series s) {
        final ContentValues cv = new ContentValues();
        cv.put(SERIES_ID, s.id());
        cv.put(SERIES_NAME, s.name());
        cv.put(SERIES_STATUS, s.status());
        cv.put(SERIES_AIRDAY, s.airDay());
        cv.put(SERIES_AIRTIME, s.airTime());
        cv.put(SERIES_AIRDATE, Numbers.parseLong(s.airDate(), null));
        cv.put(SERIES_RUNTIME, s.runtime());
        cv.put(SERIES_NETWORK, s.network());
        cv.put(SERIES_OVERVIEW, s.overview());
        cv.put(SERIES_GENRES, s.genres());
        cv.put(SERIES_ACTORS, s.actors());
        cv.put(SERIES_POSTER, s.posterFileName());
        return cv;
    }

    private ContentValues contentValuesBy(Episode e) {
        final ContentValues cv = new ContentValues();
        cv.put(EPISODE_ID, e.id());
        cv.put(EPISODE_SERIES, e.seriesId());
        cv.put(EPISODE_NUMBER, e.number());
        cv.put(EPISODE_SEASON, e.seasonNumber());
        cv.put(EPISODE_NAME, e.name());
        cv.put(EPISODE_AIRDATE, Numbers.parseLong(e.airDate(), null));
        cv.put(EPISODE_OVERVIEW, e.overview());
        cv.put(EPISODE_DIRECTORS, e.directors());
        cv.put(EPISODE_WRITERS, e.writers());
        cv.put(EPISODE_GUESTSTARS, e.guestStars());
        cv.put(EPISODE_IMAGE, e.imageFileName());
        cv.put(EPISODE_SEENMARK, String.valueOf(e.wasSeen()));
        return cv;
    }

    private Series seriesByCurrentPositionOf(Cursor c) {
        return Series.builder()
            .withId(c.getInt(c.getColumnIndex(SERIES_ID)))
            .withName(c.getString(c.getColumnIndex(SERIES_NAME)))
            .withStatus(c.getString(c.getColumnIndex(SERIES_STATUS)))
            .withAirDay(c.getString(c.getColumnIndex(SERIES_AIRDAY)))
            .withAirTime(c.getString(c.getColumnIndex(SERIES_AIRTIME)))
            .withAirDate(Dates.parseDate(c.getLong(c.getColumnIndex(SERIES_AIRDATE)), null))
            .withRuntime(c.getString(c.getColumnIndex(SERIES_RUNTIME)))
            .withNetwork(c.getString(c.getColumnIndex(SERIES_NETWORK)))
            .withOverview(c.getString(c.getColumnIndex(SERIES_OVERVIEW)))
            .withGenres(c.getString(c.getColumnIndex(SERIES_GENRES)))
            .withActors(c.getString(c.getColumnIndex(SERIES_ACTORS)))
            .withPosterFileName(c.getString(c.getColumnIndex(SERIES_POSTER)))
            .build();
    }

    private Episode episodeByCurrentPositionOf(Cursor c) {
        return Episode.builder()
            .withId(c.getInt(c.getColumnIndex(EPISODE_ID)))
            .withSeriesId(c.getInt(c.getColumnIndex(EPISODE_SERIES)))
            .withNumber(c.getInt(c.getColumnIndex(EPISODE_NUMBER)))
            .withSeasonNumber(c.getInt(c.getColumnIndex(EPISODE_SEASON)))
            .withName(c.getString(c.getColumnIndex(EPISODE_NAME)))
            .withAirDate(Dates.parseDate(c.getLong(c.getColumnIndex(EPISODE_AIRDATE)), null))
            .withOverview(c.getString(c.getColumnIndex(EPISODE_OVERVIEW)))
            .withDirectors(c.getString(c.getColumnIndex(EPISODE_DIRECTORS)))
            .withWriters(c.getString(c.getColumnIndex(EPISODE_WRITERS)))
            .withGuestStars(c.getString(c.getColumnIndex(EPISODE_GUESTSTARS)))
            .withImageFileName(c.getString(c.getColumnIndex(EPISODE_IMAGE)))
            .withSeenMark(Boolean.valueOf(c.getString(c.getColumnIndex(EPISODE_SEENMARK))))
            .build();
    }
}
