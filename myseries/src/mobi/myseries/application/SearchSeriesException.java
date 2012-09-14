/*
 *   SearchSeriesException.java
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

public class SearchSeriesException extends Exception {
    private static final long serialVersionUID = 1L;
    private String title;

    public SearchSeriesException(String title, String message) {
        super(message);
        this.title = title;
    }

    public SearchSeriesException(String title, String message, Throwable cause) {
        super(message, cause);
        this.title = title;
    }
    
    public String getTitle(){
        return this.title;
    }
   
    
}

