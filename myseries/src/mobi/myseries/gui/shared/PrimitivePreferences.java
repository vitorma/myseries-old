/*
 *   PrimitivePreferences.java
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

package mobi.myseries.gui.shared;

import mobi.myseries.shared.Validate;
import android.content.Context;
import android.content.SharedPreferences;

public class PrimitivePreferences {
    private static final String DEFAULT_KEY_SUFFIX = "";

    private Context context;
    private String name;
    private String keySuffix;

    public PrimitivePreferences(Context context, String name) {
        Validate.isNonNull(context, "context");
        Validate.isNonBlank(name, "name");

        this.context = context;
        this.name = name;
        this.keySuffix = DEFAULT_KEY_SUFFIX;
    }

    public PrimitivePreferences setKeySuffix(String keySuffix) {
        Validate.isNonBlank(keySuffix, "keySuffix");

        this.keySuffix = keySuffix;

        return this;
    }

    public int getInt(String key, int defaultValue) {
        return this.getSharedPreferences()
                   .getInt(this.compose(key), defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getSharedPreferences()
                   .getBoolean(this.compose(key), defaultValue);
    }

    public boolean putInt(String key, int value) {
        return this.getEditor()
                   .putInt(this.compose(key), value)
                   .commit();
    }

    public boolean putBoolean(String key, boolean value) {
        return this.getEditor()
                   .putBoolean(this.compose(key), value)
                   .commit();
    }

    private SharedPreferences getSharedPreferences() {
        return this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return this.getSharedPreferences().edit();
    }

    private String compose(String key) {
        Validate.isNonBlank(key, "key");

        return key + this.keySuffix;
    }
}
