package mobi.myseries.gui.appwidget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.features.features.Feature;
import mobi.myseries.application.preferences.SchedulePreferences;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.features.FeaturesActivity;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.gui.shared.SortMode;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

public class ScheduleWidgetPreferenceActivity extends Activity {

    public static Intent newIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, ScheduleWidgetPreferenceActivity.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return intent;
    }

    private int appWidgetId;
    private SchedulePreferences schedulePreferences;
    private RadioGroup sortModeRadioGroup;
    private CheckedTextView showSpecialEpisodes;
    private CheckedTextView showSeenEpisodes;
    private Map<Series, CheckedTextView> seriesToShow;
    private Button cancelButton;
    private Button saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getExtraAppWidgetIdOrFinish();

        if (App.features().isEnabled(Feature.SCHEDULE_WIDGET)) {
            this.setTheme(R.style.MySeriesTheme);
            this.setContentView(R.layout.schedulewidget_preferences);
            this.setUpViews();
            this.getActionBar().setTitle(R.string.widget_preferences);
        } else {
            this.setContentView(R.layout.schedulewidget_popup_buy);
            this.setUpDialogView();
        }
    }

    private void setUpDialogView() {
            Button negativeButton = (Button) findViewById(R.id.negativeButton);
            Button positiveButton = (Button) findViewById(R.id.positiveButton);

            negativeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            positiveButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(FeaturesActivity.newIntent(getApplicationContext()));
                }
            });
    }

    private void getExtraAppWidgetIdOrFinish() {
        this.appWidgetId = this.tryGettingAppWidgetIdFromExtras();

        this.setResult(
                Activity.RESULT_CANCELED,
                new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.appWidgetId));

        if (this.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            this.finish();
        } else {
            this.schedulePreferences = App.preferences().forSchedule();
        }
    }

    private int tryGettingAppWidgetIdFromExtras() {
        Bundle extras = this.getIntent().getExtras();

        return extras != null ?
               extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) :
               AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    private void setUpViews() {
        this.setUpSortModeOptions();
        this.setUpSpecialEpisodesOptions();
        this.setUpSeenEpisodesOptions();
        this.setUpSeriesToShowOptions();
        this.setUpCancelButton();
        this.setUpSaveButton();
    }

    private void setUpSortModeOptions() {
        this.sortModeRadioGroup = (RadioGroup) this.findViewById(R.id.sortModeRadioGroup);

        switch (this.schedulePreferences.sortMode()) {
            case SortMode.OLDEST_FIRST:
                this.sortModeRadioGroup.check(R.id.oldest_first);
                break;
            case SortMode.NEWEST_FIRST:
                this.sortModeRadioGroup.check(R.id.newest_first);
                break;
        }
    }

    private void setUpSpecialEpisodesOptions() {
        this.showSpecialEpisodes = (CheckedTextView) this.findViewById(R.id.showSpecialEpisodes);

        this.showSpecialEpisodes.setChecked(this.schedulePreferences.showSpecialEpisodes());
        this.showSpecialEpisodes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleWidgetPreferenceActivity.this.showSpecialEpisodes.toggle();
            }
        });
    }

    private void setUpSeenEpisodesOptions() {
        this.showSeenEpisodes = (CheckedTextView) this.findViewById(R.id.showSeenEpisodes);

        this.showSeenEpisodes.setChecked(this.schedulePreferences.showWatchedEpisodes());
        this.showSeenEpisodes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleWidgetPreferenceActivity.this.showSeenEpisodes.toggle();
            }
        });
    }

    private void setUpSeriesToShowOptions() {
        LayoutInflater inflater = LayoutInflater.from(this.getApplicationContext());
        LinearLayout seriesToShowPanel = (LinearLayout) this.findViewById(R.id.seriesToShowPanel);
        this.seriesToShow = new HashMap<Series, CheckedTextView>();

        TreeSet<Series> followedSeries = new TreeSet<Series>(SeriesComparator.byAscendingAlphabeticalOrder());
        followedSeries.addAll(App.seriesFollowingService().getAllFollowedSeries());

        int counter = 0;

        for (Series s : followedSeries) {
            View v = inflater.inflate(R.layout.schedulewidget_preferences_filter_option, null);
            final CheckedTextView seriesCheck = (CheckedTextView) v.findViewById(R.id.checkBox);

            seriesCheck.setText(s.name());
            seriesCheck.setChecked(!this.schedulePreferences.hideSeries(s.id()));
            seriesCheck.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    seriesCheck.toggle();
                }
            });

            counter++;
            if (counter == followedSeries.size()) {
                View divider = v.findViewById(R.id.divider);
                divider.setVisibility(View.INVISIBLE);
            }

            seriesToShowPanel.addView(v);
            this.seriesToShow.put(s, seriesCheck);
        }
    }

    private void setUpCancelButton() {
        this.cancelButton = (Button) this.findViewById(R.id.cancelButton);

        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleWidgetPreferenceActivity.this.finish();
            }
        });
    }

    private void setUpSaveButton() {
        this.saveButton = (Button) this.findViewById(R.id.saveButton);

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleWidgetPreferenceActivity.this.onSave();
            }
        });
    }

    private void onSave() {
        this.saveSortModePreference();
        this.saveSpecialEpisodesPreference();
        this.saveSeenEpisodesPreference();
        this.saveSeriesToShowPreference();
        this.updateAppWidget();
        this.finishOk();
    }

    private void saveSortModePreference() {
        switch (this.sortModeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.oldest_first:
                this.schedulePreferences.putSortMode(SortMode.OLDEST_FIRST);
                break;
            case R.id.newest_first:
                this.schedulePreferences.putSortMode(SortMode.NEWEST_FIRST);
                break;
        }
    }

    private void saveSpecialEpisodesPreference() {
        this.schedulePreferences.putIfShowSpecialEpisodes(this.showSpecialEpisodes.isChecked());
    }

    private void saveSeenEpisodesPreference() {
        this.schedulePreferences.putIfShowWatchedEpisodes(this.showSeenEpisodes.isChecked());
    }

    private void saveSeriesToShowPreference() {
        this.schedulePreferences.putSeriesToHide(seriesToHideIds());
    }

    private int[] seriesToHideIds() {
        int[] seriesToHideIds = new int[this.seriesToShow.size()];

        int length = 0;

        for (Entry<Series, CheckedTextView> entry : this.seriesToShow.entrySet()) {
            if (!entry.getValue().isChecked()) {
                seriesToHideIds[length] = entry.getKey().id();
                length++;
            }
        }

        return Arrays.copyOf(seriesToHideIds, length);
    }

    private void updateAppWidget() {
        ScheduleWidget.setUp(
            this.getApplicationContext(),
            AppWidgetManager.getInstance(this.getApplicationContext()),
            this.appWidgetId);
    }

    private void finishOk() {
        Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.appWidgetId);
        this.setResult(Activity.RESULT_OK, resultValue);
        this.finish();
    }
}
