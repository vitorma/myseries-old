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

    @Override
    public boolean isChecked() {
        return this.isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            this.refreshDrawableState();
        }
    }

    @Override
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
        this.setBackgroundResource(0);
    }
}
