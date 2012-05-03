/*
 *   SearchSeriesService.java
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

package mobi.myseries.application;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.Validate;
import android.os.AsyncTask;

public class SearchSeriesService {
    private static final long TIMEOUT = 5L;

    private SeriesSource seriesSource;

    public SearchSeriesService(SeriesSource seriesSource) {
        Validate.isNonNull(seriesSource, "seriesSource");
        this.seriesSource = seriesSource;
    }

    public List<Series> search(String seriesName, String language) throws Exception {
        AsyncTask<String, Void, AsyncTaskResult<List<Series>>> task = new SearchSeriesTask(this.seriesSource);
        task.execute(seriesName, language);

        AsyncTaskResult<List<Series>> taskResult = null;

        try {
            taskResult = task.get(TIMEOUT, TimeUnit.SECONDS);
        } catch (CancellationException e) {
            //TODO Find a better message
            throw new SearchSeriesException("CancellationException", e);
        } catch (ExecutionException e) {
            //TODO Find a better message
            throw new SearchSeriesException("ExecutionException", e);
        } catch (InterruptedException e) {
            //TODO Find a better message
            throw new SearchSeriesException("InterruptedException", e);
        } catch (TimeoutException e) {
            //TODO Find a better message
            throw new SearchSeriesException("TimeoutException", e);
        }

        if(taskResult.error() != null){
            throw taskResult.error();
        }

        if (taskResult.result().isEmpty()) {
            throw new SearchSeriesException(Message.NO_RESULTS_FOUND_FOR_CRITERIA);
        }

        return Collections.unmodifiableList(taskResult.result());
    }

    private static class SearchSeriesTask extends AsyncTask<String, Void, AsyncTaskResult<List<Series>>> {
        private SeriesSource seriesSource;

        private SearchSeriesTask(SeriesSource seriesSource) {
            this.seriesSource = seriesSource;
        }

        @Override
        protected AsyncTaskResult<List<Series>> doInBackground(String... params) {
            String seriesName = params[0];
            String language = params[1];

            try {
                List<Series> seriesList= this.seriesSource.searchFor(seriesName, language);
                return new AsyncTaskResult<List<Series>>(seriesList);
            } catch (InvalidSearchCriteriaException e) {
                return new AsyncTaskResult<List<Series>>(new SearchSeriesException(Message.INVALID_SEARCH_CRITERIA, e));
            } catch (ConnectionFailedException e) {
                return new AsyncTaskResult<List<Series>>(new SearchSeriesException(Message.CONNECTION_FAILED, e));
            } catch (ParsingFailedException e) {
                return new AsyncTaskResult<List<Series>>(new SearchSeriesException(Message.PARSING_FAILED, e));
            }
        }
    }
}
