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
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
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
    private TextPaint textPaint;
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
        this.textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG | TextPaint.SUBPIXEL_TEXT_FLAG);
        this.textPaint.setColor(DEFAULT_TEXT_COLOR);
        this.textBackground = new Paint();
        this.textBackground.setColor(DEFAULT_TEXT_BACKGROUND_COLOR);
    }

    @Override
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

        int internalPadding = 2;
        float height = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();

        textPaint.setTextSize(height);
        textPaint.setTextAlign(Paint.Align.RIGHT);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(TEXT_WIDTH_PARAMETER, 0, TEXT_WIDTH_PARAMETER.length(), textBounds);

        float textWidth = this.textPaint.measureText(TEXT_WIDTH_PARAMETER);

        float width =
                this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight()
                        - textWidth - (2 * internalPadding);

        float partWidth = (width) / this.parts.length;
        float partHeight = height;

        RectF rect = new RectF(this.getPaddingLeft(), this.getPaddingTop(), width, height);
        canvas.drawRect(rect, background);

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

            canvas.drawRect(((i * partWidth) + this.getPaddingLeft()), this.getPaddingTop(),
                    (j) * partWidth, partHeight - this.getPaddingBottom(), this.foreground);

            i = j;
        }

        int textBackgroundRight = this.getMeasuredWidth() + (int) textWidth;

        RectF rect2 = new RectF(width, this.getPaddingTop(), textBackgroundRight, height);
        canvas.drawRect(rect2, textBackground);

        float diff = this.getMeasuredHeight() - textBounds.height();
        float textY = textBounds.height() + (diff / 2.0f);

        canvas.drawText(text, this.getMeasuredWidth() - this.getPaddingRight() - internalPadding,
                textY, this.textPaint);
    }
}
