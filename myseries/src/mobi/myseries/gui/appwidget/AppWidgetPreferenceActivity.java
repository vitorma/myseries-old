package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.Android;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class AppWidgetPreferenceActivity extends SherlockActivity {

    /* CLASS MEMBERS */

    private static final String PREFS_NAME = "mobi.myseries.gui.appwidget.MyScheduleWidgetPreference";
    private static final String PREF_SCHEDULE_MODE_KEY = "scheduleMode_";
    private static final String PREF_SORT_MODE_KEY = "sortMode_";
    private static final String SHOW_SPECIAL_EPISODES_KEY = "showSpecialEpisodes_";
    private static final String SHOW_SEEN_EPISODES_KEY = "showSeenEpisodes_";
    private static final String SHOW_SERIES_KEY = "showSeries_";

    public static Intent newIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, AppWidgetPreferenceActivity.class);

        intent.putExtra(Extra.APPWIDGET_ID, appWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return intent;
    }

    public static int scheduleModeBy(Context context, int appWidgetId) {
        return getIntPreference(context, appWidgetId, PREF_SCHEDULE_MODE_KEY, ScheduleMode.NEXT);
    }

    public static int sortModeBy(Context context, int appWidgetId) {
        return getIntPreference(context, appWidgetId, PREF_SORT_MODE_KEY, SortMode.OLDEST_FIRST);
    }

    public static boolean showSpecialEpisodes(Context context, int appWidgetId) {
        return getBooleanPreference(context, appWidgetId, SHOW_SPECIAL_EPISODES_KEY, false);
    }

    public static boolean showSeenEpisodes(Context context, int appWidgetId) {
        return getBooleanPreference(context, appWidgetId, SHOW_SEEN_EPISODES_KEY, false);
    }

    public static boolean showSeries(Context context, int appWidgetId, int seriesId) {
        return getBooleanPreference(context, appWidgetId, SHOW_SERIES_KEY + seriesId, true);
    }

    public static int getIntPreference(Context context, int appWidgetId, String key, int defaultValue) {
        return getPreferences(context)
            .getInt(key + appWidgetId, defaultValue);
    }

    private static boolean getBooleanPreference(Context context, int appWidgetId, String key, boolean defaultValue) {
        return getPreferences(context)
            .getBoolean(key + appWidgetId, defaultValue);
    }

    public static boolean saveIntPreference(Context context, int appWidgetId, String key, int value) {
        return getPreferences(context)
            .edit()
            .putInt(key + appWidgetId, value)
            .commit();
    }

    private static boolean saveBooleanPreference(Context context, int appWidgetId, String key, boolean value) {
        return getPreferences(context)
            .edit()
            .putBoolean(key + appWidgetId, value)
            .commit();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /* INSTANCE MEMBERS */

    private int appWidgetId;
    private RadioGroup scheduleModeRadioGroup;
    private RadioGroup sortModeRadioGroup;
    private Button cancelButton;
    private Button saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.appwidget_myschedule_preferences);
        this.setResult(Activity.RESULT_CANCELED);
        this.getExtraAppWidgetIdOrFinish();
        this.setupViews();
        this.getSupportActionBar().setTitle(R.string.widget_preferences);
    }

    private void getExtraAppWidgetIdOrFinish() {
        this.appWidgetId = this.tryGettingAppWidgetIdFromExtras();

        if (this.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            this.finish();
        }
    }

    private int tryGettingAppWidgetIdFromExtras() {
        Bundle extras = this.getIntent().getExtras();

        return extras != null ?
               extras.getInt(Extra.APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) :
               AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    private void setupViews() {
        this.setUpSortModeLabel();
        this.setupScheduleModeRadioGroup();
        this.setupSortModeRadioGroup();
        this.setupCancelButton();
        this.setupSaveButton();
    }

    private void setUpSortModeLabel() {
        TextView sortModeLabel = (TextView) this.findViewById(R.id.sortMode);
        String format = this.getString(R.string.sort_by_format);
        String episodes = this.getString(R.string.episodes);

        sortModeLabel.setText(String.format(format, episodes).toUpperCase());
    }

    private void setupScheduleModeRadioGroup() {
        this.scheduleModeRadioGroup = (RadioGroup) this.findViewById(R.id.scheduleModeRadioGroup);

        switch (scheduleModeBy(this, this.appWidgetId)) {
            case ScheduleMode.RECENT:
                this.scheduleModeRadioGroup.check(R.id.recent);
                break;
            case ScheduleMode.NEXT:
                this.scheduleModeRadioGroup.check(R.id.next);
                break;
            case ScheduleMode.UPCOMING:
                this.scheduleModeRadioGroup.check(R.id.upcoming);
                break;
        }
    }

    private void setupSortModeRadioGroup() {
        this.sortModeRadioGroup = (RadioGroup) this.findViewById(R.id.sortModeRadioGroup);

        switch (sortModeBy(this, this.appWidgetId)) {
            case SortMode.OLDEST_FIRST:
                this.sortModeRadioGroup.check(R.id.oldest_first);
                break;
            case SortMode.NEWEST_FIRST:
                this.sortModeRadioGroup.check(R.id.newest_first);
                break;
        }
    }

    private void setupCancelButton() {
        this.cancelButton = (Button) this.findViewById(R.id.cancelButton);

        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppWidgetPreferenceActivity.this.finish();
            }
        });
    }

    private void setupSaveButton() {
        this.saveButton = (Button) this.findViewById(R.id.saveButton);

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppWidgetPreferenceActivity.this.onSave();
            }
        });
    }

    private void onSave() {
        this.saveScheduleModePreference();
        this.saveSortModePreference();
        this.updateAppWidget();
        this.finishOk();
    }

    private void saveScheduleModePreference() {
        switch (this.scheduleModeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.recent:
                saveIntPreference(this, this.appWidgetId, PREF_SCHEDULE_MODE_KEY, ScheduleMode.RECENT);
                break;
            case R.id.next:
                saveIntPreference(this, this.appWidgetId, PREF_SCHEDULE_MODE_KEY, ScheduleMode.NEXT);
                break;
            case R.id.upcoming:
                saveIntPreference(this, this.appWidgetId, PREF_SCHEDULE_MODE_KEY, ScheduleMode.UPCOMING);
                break;
        }
    }

    private void saveSortModePreference() {
        switch (this.sortModeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.oldest_first:
                saveIntPreference(this, this.appWidgetId, PREF_SORT_MODE_KEY, SortMode.OLDEST_FIRST);
                break;
            case R.id.newest_first:
                saveIntPreference(this, this.appWidgetId, PREF_SORT_MODE_KEY, SortMode.NEWEST_FIRST);
                break;
        }
    }

    private void updateAppWidget() {
        if (Android.isHoneycombOrHigher()) {
            ScheduleWidgetV11.setUp(this, AppWidgetManager.getInstance(this), this.appWidgetId);
        } else {
            ScheduleWidgetV8.setUp(this, this.appWidgetId);
        }
    }

    private void finishOk() {
        Intent resultValue = new Intent().putExtra(Extra.APPWIDGET_ID, this.appWidgetId);
        this.setResult(Activity.RESULT_OK, resultValue);
        this.finish();
    }
}
