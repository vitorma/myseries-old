/*
 *   SeriesListAdapter.java
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

import mobi.myseries.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageButton;

public class SeenMark extends ImageButton implements Checkable {
    private boolean isChecked;

    private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

    public SeenMark(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setUpDrawables();
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            this.refreshDrawableState();
        }
    }

    public void toggle() {
        this.setChecked(!this.isChecked);
    }

    @Override
    public boolean performClick() {
        this.toggle();

        return super.performClick();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

        if (this.isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }

        return drawableState;
    }

    private void setUpDrawables() {
        Drawable d = this.getResources().getDrawable(R.drawable.checkbox);

        this.setImageDrawable(d);
        this.setBackgroundDrawable(null);
    }
}