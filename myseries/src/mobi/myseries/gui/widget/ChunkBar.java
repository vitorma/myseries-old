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

package mobi.myseries.gui.widget;

import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ChunkBar extends View {
    private static final int DEFAULT_DRAWING_COLOR = Color.rgb(50, 182, 231);
    private static final int DEFAULT_BACKGROUND_COLOR = Color.rgb(27, 27, 27);
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_BACKGROUND_COLOR = Color.rgb(27, 27, 27);
    private static final String TEXT_WIDTH_PARAMETER = "0000/0000";

    private boolean[] parts;
    private Paint foreground;
    private Paint background;
    private Paint textPaint;
    private Paint textBackground;

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
        this.textBackground = new Paint();
        this.textBackground.setColor(DEFAULT_TEXT_BACKGROUND_COLOR);
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

    public void setTextBackgroundColor(int color) {
        this.textBackground.setColor(color);
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

        String text = String.format("%d/%d", availableParts, this.parts.length);
        float textWidth = this.textPaint.measureText(TEXT_WIDTH_PARAMETER);

        int d = 2;
        int height = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();
        int width = this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight() - (int) textWidth - 2 * d;

        float partWidth = (float) (width) / this.parts.length;
        float partHeight = (float) height;

        RectF rect = new RectF(this.getPaddingLeft(), this.getPaddingTop(), width, height);
        canvas.drawRect(rect, background);

        for (int i = 0; i < this.parts.length; ++i) {
            if (this.parts[i]) {
                canvas.drawRect(
                        i * partWidth + this.getPaddingLeft(),
                        this.getPaddingTop(),
                        (i + 1) * partWidth,
                        partHeight - this.getPaddingBottom(),
                        this.foreground);
            }
        }

        int textBackgroundRight = this.getMeasuredWidth() + (int) textWidth;

        RectF rect2 = new RectF(width, this.getPaddingTop(), textBackgroundRight, height);
        canvas.drawRect(rect2, textBackground);

        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.RIGHT);

        int internalPadding = 2;
        float textY = height - 2;
        canvas.drawText(text, this.getMeasuredWidth() - this.getPaddingRight() - internalPadding, textY, this.textPaint);

        canvas.save();
    }
}
