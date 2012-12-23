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

package mobi.myseries.gui.shared;

import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ChunkBar extends View {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.rgb(27, 27, 27);
    private static final int DEFAULT_DRAWING_COLOR = Color.rgb(50, 182, 231);

    private Paint background;
    private Paint foreground;
    private boolean[] parts;
    private RectF rect;
    private int[] stops = {};

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
        this.foreground.setColor(ChunkBar.DEFAULT_DRAWING_COLOR);
        this.background = new Paint();
        this.background.setColor(ChunkBar.DEFAULT_BACKGROUND_COLOR);
        this.rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float height = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();

        float width =
                this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();

        float partWidth = (width) / this.parts.length;
        float partHeight = height;

        this.rect.left = this.getPaddingLeft();
        this.rect.top = this.getPaddingTop();
        this.rect.right = width;
        this.rect.bottom = height;

        if (this.stops.length > 0) {
            int slice = 0;
            for (int stop : this.stops) {
                if (stop > this.parts.length) {
                    break;
                }

                canvas.drawRect(
                        this.rect.left,
                        this.rect.top,
                        this.rect.left += (stop * partWidth),
                        this.rect.bottom,
                        this.backgroundOfSlice(slice));
                ++slice;
            }
        } else {
            canvas.drawRect(this.rect, this.background);
        }

        int i = 0;
        int j;

        while (i < this.parts.length) {
            if (!this.parts[i]) {
                ++i;
                continue;
            }

            j = i + 1;
            while ((j < this.parts.length) && (this.parts[i] == this.parts[j])) {
                ++j;
            }

            if (((j - i) * partWidth) >= 1.0) {
                this.foreground.setAlpha(255);
                canvas.drawRect(((i * partWidth) + this.getPaddingLeft()), this.getPaddingTop(),
                        (j) * partWidth, partHeight - this.getPaddingBottom(), this.foreground);
            } else {
                this.foreground.setAlpha((int) (100 + (155 * (j - i) * partWidth)));
                canvas.drawRect(((i * partWidth) + this.getPaddingLeft()), this.getPaddingTop(),
                        ((i) * partWidth) + 1, partHeight - this.getPaddingBottom(),
                        this.foreground);
            }

            i = j;
        }
    }

    private Paint backgroundOfSlice(int slice) {
        Paint paint = new Paint(this.background);

        int middle = this.stops.length;
        int distance = middle - slice;

        int oldColor = this.background.getColor() & 0xFFFFFF;

        int blueStep = (distance * (((oldColor & 0x0000FF) >> 0) / (this.stops.length))) & 0xFF;
        int greenStep = (distance * (((oldColor & 0x00FF00) >> 8) / (this.stops.length))) & 0xFF;
        int redStep = (distance * (((oldColor & 0xFF0000) >> 16) / (this.stops.length))) & 0xFF;

        int color = 0xFF000000 | ((oldColor) - ((redStep << 16) | (greenStep << 8) | blueStep));

        paint.setColor(color);
        return paint;
    }

    @Override
    public void setBackgroundColor(int color) {
        this.background.setColor(color);
    }

    public void setForegroundColor(int color) {
        this.foreground.setColor(color);
    }

    public void setParts(boolean[] parts) {
        Validate.isNonNull(parts, "parts");

        this.parts = parts;
    }

    public void setStops(int[] stops) {
        Validate.isNonNull(stops, "stops");

        this.stops = stops;
    }

    public int[] stops() {
        return this.stops;
    }
}
