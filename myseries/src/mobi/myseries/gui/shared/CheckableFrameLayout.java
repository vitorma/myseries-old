package mobi.myseries.gui.shared;

import mobi.myseries.R;
import mobi.myseries.application.App;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class CheckableFrameLayout extends FrameLayout implements Checkable {
    private boolean checked;
    private boolean changeBackground;
    private OnCheckedListener listener;

    public CheckableFrameLayout(Context context) {
        super(context);
    }

    public CheckableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void changeBackgroundWhenChecked(boolean change) {
        this.changeBackground = change;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;

        if (this.changeBackground) {
            this.setBackgroundColor(
                    checked ?
                    App.resources().getColor(R.color.bg_navigation_drawer_list_selector) :
                    App.resources().getColor(R.color.transparent));
        }

        if (this.listener != null) {
            this.listener.onChecked(checked);
        }
    }

    @Override
    public boolean isChecked() {
        return this.checked;
    }

    @Override
    public void toggle() {
        this.setChecked(!this.checked);
    }

    public void setOnCheckedListener(OnCheckedListener listener) {
        this.listener = listener;
    }

    public static interface OnCheckedListener {
        public void onChecked(boolean checked);
    }
}
