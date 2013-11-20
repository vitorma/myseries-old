package mobi.myseries.gui.settings;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.UpdatePreferences;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    //XXX (Cleber) Get preference keys from a string resource

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("Update.whenUpdateAutomatically")) {
            setUpPrefWhenUpdateAutomatically();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setUpPrefWhenUpdateAutomatically();
    }

    private void setUpPrefWhenUpdateAutomatically() {
        UpdatePreferences prefs = App.preferences().forUpdate();
        String key = "Update.whenUpdateAutomatically";

        if (prefs.updateNever()) {
            setSummary(key, getString(R.string.settings_update_automatically_never));
            return;
        }

        if (prefs.updateOnlyOnWifi()) {
            setSummary(key, getString(R.string.settings_update_automatically_only_on_wifi));
            return;
        }

        setSummary(key, getString(R.string.settings_update_automatically_always));
    }

    private void setSummary(String key, String summary) {
        findPreference(key).setSummary(summary);
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
}