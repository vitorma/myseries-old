/*
 *   ChunkBar.java
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

package br.edu.ufcg.aweseries.gui;

import br.edu.ufcg.aweseries.util.Validate;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ChunkBar extends View {

    private boolean[] parts;
    private Paint foreground;
    private Paint background;

    public ChunkBar(Context context) {
        this(context, null);
    }

    public ChunkBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ChunkBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.parts = new boolean[] { false };
        this.foreground = new Paint();
        this.foreground.setColor(Color.GREEN);
        this.background = new Paint();
        this.background.setColor(android.R.color.background_light);
    }

    public void setParts(boolean[] parts) {
        Validate.isNonNull(parts, "parts");

        this.parts = parts;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        float partWidth = (float) (width - 4) / parts.length;
        float partHeight = (float) height - 4;
        float dx = 2;
        float dy = 2;

        RectF rect = new RectF(1, 1, width - 1, height - 1);
        canvas.drawRoundRect(rect, height / 5, height / 5, background);

        RectF rect2 = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rect2, height / 5, height / 5, background);

        for (int i = 0; i < this.parts.length; ++i) {
            if (this.parts[i]) {
                canvas.drawRect(i * partWidth, dy, (i + 1) * partWidth, partHeight - dy,
                        this.foreground);
            }
        }

        canvas.save();
    }
}
