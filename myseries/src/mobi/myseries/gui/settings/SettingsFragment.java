package mobi.myseries.gui.settings;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.notification.service.NotificationScheduler;
import mobi.myseries.application.preferences.UpdatePreferences;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("Update.whenUpdateAutomatically")) {
            setUpPrefWhenUpdateAutomatically();
        } else if (key.equals(getString(R.string.prefKey_notification_advance_minutes))) {
        	NotificationScheduler.setupAlarm(this.getActivity());        	
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setUpPrefWhenUpdateAutomatically();
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /* Auxiliary */

    private void setUpPrefWhenUpdateAutomatically() {
        UpdatePreferences prefs = App.preferences().forUpdate();
        int key = R.string.prefKey_update_whenUpdateAutomatically;

        if (prefs.updateNever()) {
            setSummary(key, R.string.settings_update_automatically_never);
            return;
        }

        if (prefs.updateOnlyOnWifi()) {
            setSummary(key, R.string.settings_update_automatically_only_on_wifi);
            return;
        }

        setSummary(key, R.string.settings_update_automatically_always);
    }

    private void setSummary(int key, int summary) {
        findPreference(getText(key)).setSummary(summary);
    }
}