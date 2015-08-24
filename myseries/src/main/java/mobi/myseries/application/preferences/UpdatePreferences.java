package mobi.myseries.application.preferences;

import mobi.myseries.R;
import android.content.Context;

public class UpdatePreferences extends BasePreferences {

    public UpdatePreferences(Context context) {
        super(context);
    }

    public String whenUpdateAutomatically() {
        return getString(
                key(R.string.prefKey_update_whenUpdateAutomatically),
                stringValue(R.string.prefValue_update_automatically_default));
    }

    public boolean updateAutomatically() {
        return !updateNever();
    }

    public boolean updateAlways() {
        return whenUpdateAutomatically().equals(
                stringValue(R.string.prefValue_update_automatically_always));
    }

    public boolean updateOnlyOnWifi() {
        return whenUpdateAutomatically().equals(
                stringValue(R.string.prefValue_update_automatically_only_on_wifi));
    }

    public boolean updateNever() {
        return whenUpdateAutomatically().equals(
                stringValue(R.string.prefValue_update_automatically_never));
    }

    public UpdatePreferences putWhenUpdateAutomatically(String whenUpdateAutomatically) {
        putString(
                key(R.string.prefKey_update_whenUpdateAutomatically),
                whenUpdateAutomatically);

        return this;
    }
}
