/*
 *   TheTVDBStreamFactory.java
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

package mobi.myseries.domain.source;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class TheTVDBStreamFactory implements StreamFactory {
	final int CONNECTION_TIMEOUT_IN_MILLIS = 7000;
	
    private UrlFactory urlFactory;

    public TheTVDBStreamFactory(String apiKey) {
        this.urlFactory = new UrlFactory(apiKey);
    }

    @Override
    public InputStream streamForSeries(int seriesId, Language language)
            throws StreamCreationFailedException, ConnectionFailedException, ConnectionTimeoutException {
        URL url = this.urlFactory.urlForSeries(seriesId, language);
        return this.buffered(this.streamFrom(this.connectionTo(url)));
    }

    @Override
    public InputStream streamForSeriesSearch(String seriesName, Language language)
            throws StreamCreationFailedException, ConnectionFailedException, ConnectionTimeoutException {
        URL url = this.urlFactory.urlForSeriesSearch(seriesName, language);
        return this.buffered(this.streamFrom(this.connectionTo(url)));
    }

    @Override
    public InputStream streamForSeriesPoster(String fileName)
            throws StreamCreationFailedException, ConnectionFailedException, ConnectionTimeoutException {
        URL url = this.urlFactory.urlForSeriesPoster(fileName);
        return this.buffered(this.streamFrom(this.connectionTo(url)));
    }

    @Override
    public InputStream streamForEpisodeImage(String fileName)
            throws StreamCreationFailedException, ConnectionFailedException, ConnectionTimeoutException {
        URL url = this.urlFactory.urlForEpisodeImage(fileName);
        return this.buffered(this.streamFrom(this.connectionTo(url)));
    }

    private BufferedInputStream buffered(InputStream stream) {
        return new BufferedInputStream(stream);
    }

    private InputStream streamFrom(URLConnection connection) throws StreamCreationFailedException {
        InputStream stream = null;

        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            throw new StreamCreationFailedException(e);
        }

        return stream;
    }

    private URLConnection connectionTo(URL url) throws ConnectionFailedException, ConnectionTimeoutException {
        URLConnection connection = null;

        try {
            connection = url.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT_IN_MILLIS);
            connection.connect();
        } catch (SocketTimeoutException e) {
			throw new ConnectionTimeoutException(e);

        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        
		}

        return connection;
    }

    @Override
    public InputStream streamForUpdatesSince(long dateInMiliseconds) throws StreamCreationFailedException, ConnectionFailedException, ConnectionTimeoutException {
        long currentTime = System.currentTimeMillis();
        
        URL url;
        
        if (currentTime - dateInMiliseconds < oneDay()) {
            url = this.urlFactory.urlForLastDayUpdates();
            Log.d(getClass().getName(), "Downloading update metadata for LAST DAY");

        } else if (currentTime - dateInMiliseconds < oneWeek()) {
            url = this.urlFactory.urlForLastWeekUpdates();
            Log.d(getClass().getName(), "Downloading update metadata for LAST WEEK");
            
        } else if (currentTime - dateInMiliseconds < oneMonth()) {
            url = this.urlFactory.urlForLastMonthUpdates();
            Log.d(getClass().getName(), "Downloading update metadata for LAST MONTH");
            
        } else {
            url = this.urlFactory.urlForAllAvailableUpdates();
            Log.d(getClass().getName(), "Downloading update metadata for EVER");
        }
        
        ZipInputStream iStream = zipped(buffered(streamFrom(connectionTo(url))));
        
        try {
            iStream.getNextEntry();
        } catch (IOException e) {
            throw new StreamCreationFailedException(e);
        }
        
        return iStream;
    }
    
    private long oneWeek() {
        return 7L * oneDay();
    }

    private long oneDay() {
        return 24L * 60L * 60L * 1000L;
    }
    
    private long oneMonth() {
        return 30L * oneDay();
    }

    private ZipInputStream zipped(InputStream stream) {
        return new ZipInputStream(stream);
    }
}
