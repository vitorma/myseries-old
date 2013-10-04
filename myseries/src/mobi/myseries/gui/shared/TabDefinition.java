package mobi.myseries.gui.shared;

import android.app.Fragment;

public class TabDefinition {
    private int title;
    private Fragment fragment;

    public TabDefinition (int titleResourceId, Fragment fragment) {
        this.title = titleResourceId;
        this.fragment = fragment;
    }

    public int title() {
        return this.title;
    }

    public Fragment fragment() {
        return this.fragment;
    }
}
