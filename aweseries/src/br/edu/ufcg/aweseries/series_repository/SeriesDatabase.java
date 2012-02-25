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

package br.edu.ufcg.aweseries.series_repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Numbers;

public class SeriesDatabase extends SQLiteOpenHelper implements SeriesRepository {

    private static final String DATABASE_NAME = "aweseries_db";

    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_SERIES =
        "CREATE TABLE Series (" +
        "id INTEGER PRIMARY KEY, " +
        "name TEXT NOT NULL, " +
        "status TEXT, " +
        "airsDay TEXT, " +
        "airsTime TEXT, " +
        "firstAired INTEGER, " +
        "runtime TEXT, " +
        "network TEXT, " +
        "overview TEXT, " +
        "genres TEXT, " +
        "actors TEXT, " +
        "posterFileName, " +
        "poster BLOB);";

    private static final String CREATE_TABLE_EPISODES =
        "CREATE TABLE Episode (" +
        "id INTEGER PRIMARY KEY, " +
        "seriesId INTEGER NOT NULL, " +
        "number INTEGER NOT NULL, " +
        "seasonNumber INTEGER NOT NULL, " +
        "name TEXT, " +
        "firstAired INTEGER, " +
        "overview TEXT, " +
        "director TEXT, " +
        "writer TEXT, " +
        "guestStars TEXT, " +
        "poster TEXT, " +
        "viewed TEXT, " +
        "FOREIGN KEY(seriesId) REFERENCES Series(id) ON DELETE CASCADE);";

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

    public SeriesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //SQLiteOpenHelper--------------------------------------------------------------------------------------------------

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

    //SeriesRepository--------------------------------------------------------------------------------------------------

    @Override
    public void insert(Series series) {
        if (series == null)
            throw new IllegalArgumentException("series should not be null");

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("Series", null, this.contentValuesBy(series));
        for (Episode e : series.seasons().episodes()) {
            //Log.d("SeriesDataBase", "id: " + e.id() + " airdate: " + e.airDate());
            db.insert("Episode", null, this.contentValuesBy(e));
        }
        db.close();
    }

    @Override
    public void update(Series series) {
        if (series == null)
            throw new IllegalArgumentException("series should not be null");

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("Episode", "seriesId=?", new String[] {String.valueOf(series.id())});
        db.update("Series", this.contentValuesBy(series), "id=?", new String[] {String.valueOf(series.id())});

        for (Episode e : series.seasons().episodes()) {
            db.insert("Episode", null, this.contentValuesBy(e));
        }

        db.close();
    }

    @Override
    public void updateAll(Collection<Series> seriesCollection) {
        if (seriesCollection == null)
            throw new IllegalArgumentException("seriesCollection should not be null");

        for (Series s : seriesCollection) {
            this.update(s);
        }
    }

    @Override
    public void delete(Series series) {
        if (series == null)
            throw new IllegalArgumentException("series should not be null");

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Series", "id=?", new String[] {String.valueOf(series.id())});
        db.close();
    }

    @Override
    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Series", null, null);
        db.close();
    }

    @Override
    public boolean contains(Series series) {
        if (series == null)
            throw new IllegalArgumentException("series should not be null");

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
            Log.d("SeriesDatabase", "There is no series with id " + seriesId);
            c.close();
            return null;
        }
        Series s = this.seriesByCurrentPositionOf(c);
        s.seasons().includingAll(this.getAllEpisodesOf(s, db));
        c.close();
        db.close();
        return s;
    }

    @Override
    public List<Series> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_ALL_SERIES, null);
        List<Series> allSeries = new ArrayList<Series>();
        while (c.moveToNext()) {
            Series s = this.seriesByCurrentPositionOf(c);
            s.seasons().includingAll(this.getAllEpisodesOf(s, db));
            allSeries.add(s);
        }
        c.close();
        db.close();
        return allSeries;
    }

    //Private ----------------------------------------------------------------------------------------------------------

    private List<Episode> getAllEpisodesOf(Series s, SQLiteDatabase db) {
        final Cursor c = db.rawQuery(SELECT_ALL_EPISODES_OF_A_SERIES, new String[] {String.valueOf(s.id())});
        final List<Episode> allEpisodes = new ArrayList<Episode>();
        while(c.moveToNext()) {
            allEpisodes.add(this.episodeByCurrentPositionOf(c));
        }
        c.close();
        return allEpisodes;
    }

    private ContentValues contentValuesBy(Series s) {
        final ContentValues cv = new ContentValues();
        cv.put("id", s.id());
        cv.put("name", s.name());
        cv.put("status", s.status());
        cv.put("airsDay", s.airDay());
        cv.put("airsTime", s.airTime());
        cv.put("firstAired", Numbers.parseLong(s.airDate(), null));
        cv.put("runtime", s.runtime());
        cv.put("network", s.network());
        cv.put("overview", s.overview());
        cv.put("genres", s.genres());
        cv.put("actors", s.actors());
        cv.put("posterFileName", s.posterFileName());
        return cv;
    }

    private ContentValues contentValuesBy(Episode e) {
        final ContentValues cv = new ContentValues();
        cv.put("id", e.id());
        cv.put("seriesId", e.seriesId());
        cv.put("number", e.number());
        cv.put("seasonNumber", e.seasonNumber());
        cv.put("name", e.name());
        cv.put("firstAired", Numbers.parseLong(e.airDate(), null));
        cv.put("overview", e.overview());
        cv.put("director", e.directors());
        cv.put("writer", e.writers());
        cv.put("guestStars", e.guestStars());
        cv.put("poster", e.imageFileName());
        cv.put("viewed", String.valueOf(e.wasSeen()));
        return cv;
    }

    private Series seriesByCurrentPositionOf(Cursor c) {
        return Series.builder()
        .withId(c.getInt(c.getColumnIndex("id")))
        .withName(c.getString(c.getColumnIndex("name")))
        .withStatus(c.getString(c.getColumnIndex("status")))
        .withAirDay(c.getString(c.getColumnIndex("airsDay")))
        .withAirTime(c.getString(c.getColumnIndex("airsTime")))
        .withAirDate(Dates.parseDate(c.getLong(c.getColumnIndex("firstAired")), null))
        .withRuntime(c.getString(c.getColumnIndex("runtime")))
        .withNetwork(c.getString(c.getColumnIndex("network")))
        .withOverview(c.getString(c.getColumnIndex("overview")))
        .withGenres(c.getString(c.getColumnIndex("genres")))
        .withActors(c.getString(c.getColumnIndex("actors")))
        .withPosterFileName(c.getString(c.getColumnIndex("posterFileName")))
        .build();
    }

    private Episode episodeByCurrentPositionOf(Cursor c) {
        return Episode.builder()
        .withId(c.getInt(c.getColumnIndex("id")))
        .withSeriesId(c.getInt(c.getColumnIndex("seriesId")))
        .withNumber(c.getInt(c.getColumnIndex("number")))
        .withSeasonNumber(c.getInt(c.getColumnIndex("seasonNumber")))
        .withName(c.getString(c.getColumnIndex("name")))
        .withAirDate(Dates.parseDate(c.getLong(c.getColumnIndex("firstAired")), null))
        .withOverview(c.getString(c.getColumnIndex("overview")))
        .withDirectors(c.getString(c.getColumnIndex("director")))
        .withWriters(c.getString(c.getColumnIndex("writer")))
        .withGuestStars(c.getString(c.getColumnIndex("guestStars")))
        .withImageFileName(c.getString(c.getColumnIndex("poster")))
        .withSeenMark(Boolean.valueOf(c.getString(c.getColumnIndex("viewed"))))
        .build();
    }
}
