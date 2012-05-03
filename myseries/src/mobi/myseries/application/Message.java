/*
 *   Message.java
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

import mobi.myseries.R;
import android.content.Context;

public class Message {
    private static final Context CONTEXT = App.environment().context();

    /* Search Series */
    public static final String NO_RESULTS_FOUND_FOR_CRITERIA = stringFrom(R.string.no_results_found_for_criteria);
    public static final String INVALID_SEARCH_CRITERIA = stringFrom(R.string.invalid_search_criteria);
    public static final String CONNECTION_FAILED = stringFrom(R.string.connection_failed_message);
    public static final String PARSING_FAILED = stringFrom(R.string.parsing_failed);

    private static String stringFrom(int resourceId) {
        return CONTEXT.getString(resourceId);
    }
}
