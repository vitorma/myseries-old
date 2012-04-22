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
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ChunkBar extends View {
    private boolean[] parts;
    private Paint foreground;
    private Paint background;
    private Paint textPaint;
    private static final int DEFAULT_DRAWING_COLOR = Color.rgb(50, 182, 231);
    private final int DEFAULT_BACKGROUND_COLOR = Color.DKGRAY;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

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
        this.foreground.setColor(DEFAULT_DRAWING_COLOR);
        this.background = new Paint();
        this.background.setColor(DEFAULT_BACKGROUND_COLOR);
        this.textPaint = new Paint();
        this.textPaint.setColor(DEFAULT_TEXT_COLOR);
    }

    public void setBackgroundColor(int color) {
        this.background.setColor(color);
    }

    public void setForegroundColor(int color) {
        this.foreground.setColor(color);
    }

    public void setTextColor(int color) {
        this.textPaint.setColor(color);
    }

    public void setTextSize(float textSize) {
        Validate.isTrue(textSize > 0.0, "textSize should be greater than 0");
        this.textPaint.setTextSize(textSize);
    }

    public void setParts(boolean[] parts) {
        Validate.isNonNull(parts, "parts");

        this.parts = parts;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int availableParts = 0;
        for (boolean part : this.parts) {
            if (part) {
                ++availableParts;
            }
        }

        String text = String.format(" %d/%d ", availableParts, this.parts.length);
        float textWidth = this.textPaint.measureText(text);

        int d = 2;
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - (int) textWidth - 2
                * d;

        float partWidth = (float) (width) / parts.length;
        float partHeight = (float) height;

        RectF rect = new RectF(getPaddingLeft(), getPaddingTop(), width, height);
        canvas.drawRect(rect, background);

        for (int i = 0; i < this.parts.length; ++i) {
            if (this.parts[i]) {
                canvas.drawRect(i * partWidth + getPaddingLeft(), getPaddingTop(), (i + 1)
                        * partWidth, partHeight - getPaddingBottom(), this.foreground);
            }
        }

        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Align.RIGHT);

        canvas.drawText(text, getMeasuredWidth() - getPaddingRight(), height, textPaint);

        canvas.save();
    }
}
