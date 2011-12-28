/*
 *   AweseriesMedWidgetProvider.java
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

package br.edu.ufcg.aweseries.gui.desktop_widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class AweseriesMedWidgetProvider extends AweseriesWidgetProvider {
    private static int LIMIT = 4;

    public AweseriesMedWidgetProvider() {
        super();
    }
    
    @Override
    protected Intent createUpdateIntent(Context context) {
        Intent intent = new Intent(context, UpdateServiceMed.class);
        return intent;
    }
    
    public static class UpdateServiceMed extends UpdateService {
        public UpdateServiceMed() {
            super("br.edu.ufcg.aweseries.gui.desktop_widget.AweseriesMedWidgetProvider$UpdateServiceMed");
        }
        
        @Override
        public void onHandleIntent(Intent intent) {
            ComponentName componentName = new ComponentName(this, AweseriesMedWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);

            Intent i = new Intent(this, AweseriesMedWidgetProvider.class);
            manager.updateAppWidget(
                    componentName,
                    buildUpdate(this, super.layout(), super.itemLayout(),
                            super.noItemLayout(), LIMIT, i));
        }
    }
}
