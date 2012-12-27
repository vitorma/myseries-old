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
import mobi.myseries.shared.imageprocessing.LinearGradient;
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
    private static final int OPAQUE_ALPHA = 255;
    private static final int MIN_ALLOWED_ALPHA = 100;
    private static final double ONE_PIXEL = 1.0;
    private Paint p = new Paint();
    private boolean[] parts;
    private RectF rect;
    private int[] stops = {};
    private LinearGradient backgroundGradient = new LinearGradient()
            .from(ChunkBar.DEFAULT_BACKGROUND_COLOR)
            .to(ChunkBar.DEFAULT_BACKGROUND_COLOR);
    private LinearGradient foregroundGradient = new LinearGradient()
            .from(ChunkBar.DEFAULT_DRAWING_COLOR)
            .to(ChunkBar.DEFAULT_BACKGROUND_COLOR);

    public ChunkBar(Context context) {
        this(context, null);
    }

    public ChunkBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ChunkBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.parts = new boolean[] { false };
        this.rect = new RectF();
    }

    private void drawBackgroundNoParts(Canvas canvas) {
        this.updateRect();
        this.p.setAlpha(ChunkBar.OPAQUE_ALPHA);
        this.p.setColor(this.backgroundGradient.startColor());
        canvas.drawRect(this.rect, this.p);
    }

    private void drawBackgroundWithParts(Canvas canvas) {
        this.updateRect();

        this.p.setColor(this.backgroundGradient.endColor());
        this.p.setAlpha(ChunkBar.OPAQUE_ALPHA);
        canvas.drawRect(this.rect, this.p);

        float partWidth = (this.usableWidth()) / this.parts.length;

        this.backgroundGradient.withSteps(this.stops.length);

        int slice = 0;
        for (int stop : this.stops) {
            this.p.setColor(this.backgroundGradient.colorOfPiece(slice));

            canvas.drawRect(
                    this.rect.left,
                    this.rect.top,
                    this.rect.left += (stop * partWidth),
                    this.rect.bottom,
                    this.p);

            ++slice;
        }

    }

    private void drawForegroundNoParts(Canvas canvas) {
        float partWidth = (this.usableWidth()) / this.parts.length;
        float partHeight = this.usableHeight();

        int i = 0;
        int j;

        this.p.setAlpha(ChunkBar.OPAQUE_ALPHA);
        this.p.setColor(this.foregroundGradient.startColor());

        while (i < this.parts.length) {
            if (!this.parts[i]) {
                ++i;
                continue;
            }

            j = i + 1;
            while ((j < this.parts.length) && (this.parts[i] == this.parts[j])) {
                ++j;
            }

            if (((j - i) * partWidth) >= ChunkBar.ONE_PIXEL) {
                this.p.setAlpha(ChunkBar.OPAQUE_ALPHA);

                canvas.drawRect(((i * partWidth) + this.getPaddingLeft()), this.getPaddingTop(),
                        (j) * partWidth, partHeight - this.getPaddingBottom(), this.p);
            } else {
                this.p.setAlpha(
                        (int) (ChunkBar.MIN_ALLOWED_ALPHA
                        + ((ChunkBar.OPAQUE_ALPHA - ChunkBar.MIN_ALLOWED_ALPHA)
                                * (j - i) * partWidth)
                        )
                        );

                canvas.drawRect(((i * partWidth) + this.getPaddingLeft()), this.getPaddingTop(),
                        ((i) * partWidth) + 1, partHeight - this.getPaddingBottom(),
                        this.p);
            }

            i = j;
        }
    }

    private void drawForegroundWithParts(Canvas canvas) {
        float partWidth = (this.usableWidth()) / this.parts.length;
        float partHeight = this.usableHeight();

        int i = 0;
        int j;
        int slice = 0;
        int lastStop = 0;
        int slicePart = 1;

        this.foregroundGradient.withSteps(this.stops.length);
        this.p.setAlpha(ChunkBar.OPAQUE_ALPHA);
        this.p.setColor(this.foregroundGradient.colorOfPiece(slice));

        while (i < this.parts.length) {
            if (((slice + 1) < this.stops.length) && (slicePart > this.stops[slice])) {
                slicePart = 1;
                lastStop += this.stops[slice];
                ++slice;
                this.p.setColor(this.foregroundGradient.colorOfPiece(slice));
            }

            j = i + 1;
            while ((j < this.parts.length) && (this.parts[i] == this.parts[j]) && ((i - lastStop) < this.stops[slice])) {
                ++j;
            }

            if (!this.parts[i]) {

            } else             if (((j - i) * partWidth) >= ChunkBar.ONE_PIXEL) {
                this.p.setAlpha(ChunkBar.OPAQUE_ALPHA);

                canvas.drawRect(((i * partWidth) + this.getPaddingLeft()), this.getPaddingTop(),
                        (j) * partWidth, partHeight - this.getPaddingBottom(), this.p);
            } else {
                this.p.setAlpha(
                        (int) (ChunkBar.MIN_ALLOWED_ALPHA
                        + ((ChunkBar.OPAQUE_ALPHA - ChunkBar.MIN_ALLOWED_ALPHA)
                                * (j - i) * partWidth)
                        )
                        );

                canvas.drawRect(((i * partWidth) + this.getPaddingLeft()), this.getPaddingTop(),
                        ((i) * partWidth) + 1, partHeight - this.getPaddingBottom(),
                        this.p);
            }

            ++i;
            ++slicePart;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.stops.length > 1) {
            this.drawBackgroundWithParts(canvas);
            this.drawForegroundWithParts(canvas);
        } else {
            this.drawBackgroundNoParts(canvas);
            this.drawForegroundNoParts(canvas);
        }

    }

    @Override
    public void setBackgroundColor(int color) {
        this.backgroundGradient.from(color).to(color);
    }

    public void setBackgroundGradient(LinearGradient linearGradient) {
        Validate.isNonNull(linearGradient, "linearGradient");

        this.backgroundGradient = new LinearGradient(linearGradient);
    }

    public void setForegroundColor(int color) {
        this.foregroundGradient.from(color).to(color);
    }

    public void setForegroundGradient(LinearGradient linearGradient) {
        Validate.isNonNull(linearGradient, "linearGradient");

        this.foregroundGradient = new LinearGradient(linearGradient);
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

    private void updateRect() {
        this.rect.left = this.getPaddingLeft();
        this.rect.top = this.getPaddingTop();
        this.rect.right = this.usableWidth();
        this.rect.bottom = this.usableHeight();

    }

    private float usableHeight() {
        return this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();
    }

    private float usableWidth() {
        return this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }

}
