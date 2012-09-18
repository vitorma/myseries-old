/*
 *   StreamFactory.java
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

import java.io.InputStream;
import java.util.zip.ZipInputStream;

public interface StreamFactory {
    public InputStream streamForSeries(int seriesId, Language language)
            throws StreamCreationFailedException, ConnectionFailedException;

    public InputStream streamForSeriesSearch(String seriesName, Language language)
            throws StreamCreationFailedException, ConnectionFailedException;

    // XXX(gabriel) Remove ZipInputStream, keep InputStream. The parser must not care about zip files.
    public ZipInputStream streamForUpdatesSince(long dateInMiliseconds) 
            throws StreamCreationFailedException, ConnectionFailedException;

    public InputStream streamForSeriesPoster(String fileName)
            throws StreamCreationFailedException, ConnectionFailedException;

    public InputStream streamForEpisodeImage(String fileName)
            throws StreamCreationFailedException, ConnectionFailedException;
}
