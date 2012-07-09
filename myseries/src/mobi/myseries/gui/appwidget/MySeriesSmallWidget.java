/*
 *   MySeriesSmallWidget.java
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


public class MySeriesSmallWidget extends MySeriesWidget {

    @Override
    protected Class updateServiceClass() {
        return UpdateServiceSmall.class;
    }

    public static class UpdateServiceSmall extends UpdateService {
        public UpdateServiceSmall() {
            super("mobi.myseries.gui.appwidget.MySeriesSmallWidget$UpdateServiceSmall");
        }

        protected Class widgetClass() {
            return MySeriesSmallWidget.class;
        }
    }
}
