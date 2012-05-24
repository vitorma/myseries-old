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

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Validate;
import android.os.AsyncTask;

public class SearchSeriesService {
    private SeriesSource seriesSource;
    private ListenerSet<SearchSeriesListener> listenerSet;
    private static List<Series> lastSearchResult;

    public SearchSeriesService(SeriesSource seriesSource) {
        Validate.isNonNull(seriesSource, "seriesSource");
        this.seriesSource = seriesSource;
        this.listenerSet = new ListenerSet<SearchSeriesListener>();
    }
    
    public void registerListener(SearchSeriesListener listener){
        this.listenerSet.register(listener);
    }
    
    public void deregisterListener(SearchSeriesListener listener){
        this.listenerSet.deregister(listener);
    }

    public void search(String seriesName, String language) {

        new SearchSeriesTask(this.seriesSource, listenerSet).execute(seriesName, language);
}
    public static List<Series> getLastSearchResult(){
        return lastSearchResult;
        
}
    private static class SearchSeriesTask extends AsyncTask<String, Void, AsyncTaskResult<List<Series>>> {
        private SeriesSource seriesSource;
        private ListenerSet<SearchSeriesListener> listenerSet;
        

        private SearchSeriesTask(SeriesSource seriesSource, ListenerSet<SearchSeriesListener> listenerSet) {
            this.seriesSource = seriesSource;
            this.listenerSet = listenerSet;
        }
        
        @Override
        protected void onPreExecute() {
            lastSearchResult = null;
            for (SearchSeriesListener l : listenerSet) {
                l.onStart();
            }
        }

        @Override
        protected AsyncTaskResult<List<Series>> doInBackground(String... params) {
            String seriesName = params[0];
            String language = params[1];

            try {
                List<Series> seriesList = this.seriesSource.searchFor(seriesName, language);
                if (!seriesList.isEmpty())
                    return new AsyncTaskResult<List<Series>>(seriesList);

                return new AsyncTaskResult<List<Series>>(new SearchSeriesException(Message.NO_RESULTS_FOUND_FOR_CRITERIA));
            } catch (InvalidSearchCriteriaException e) {
                return new AsyncTaskResult<List<Series>>(new SearchSeriesException(Message.INVALID_SEARCH_CRITERIA, e));
            } catch (ConnectionFailedException e) {
                return new AsyncTaskResult<List<Series>>(new SearchSeriesException(Message.CONNECTION_FAILED, e));
            } catch (ParsingFailedException e) {
                return new AsyncTaskResult<List<Series>>(new SearchSeriesException(Message.PARSING_FAILED, e));
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<List<Series>> taskResult) {
            lastSearchResult = taskResult.result();
            for (SearchSeriesListener l : listenerSet) {
                if (taskResult.error() == null){
                    l.onSucess(Collections.unmodifiableList(taskResult.result()));
                }else{
                    l.onFaluire(taskResult.error());
                }
                l.onFinish();
            }
        }
    }
}