/*
 *   Action.java
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

package mobi.myseries.gui.appwidget;

public interface Action {
    public static final String REFRESH = "mobi.myseries.gui.appwidget.REFRESH";
    public static final String GO_TO_FIRST = "mobi.myseries.gui.appwidget.SCROLL_TO_FIRST";
    public static final String GO_TO_LAST = "mobi.myseries.gui.appwidget.SCROLL_TO_LAST";
    public static final String GO_TO_PREVIOUS = "mobi.myseries.gui.appwidget.SCROLL_TO_PREVIOUS";
    public static final String GO_TO_NEXT = "mobi.myseries.gui.appwidget.SCROLL_TO_NEXT";
    public static final String SETUP = "mobi.myseries.gui.appwidget.SETUP";
}