package mobi.myseries.gui.appwidget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.ScheduleWidgetPreferences;
import mobi.myseries.domain.model.Series;
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
    private ScheduleWidgetPreferences preferences;
    private RadioGroup sortModeRadioGroup;
    private CheckedTextView showSpecialEpisodes;
    private CheckedTextView showSeenEpisodes;
    private Map<Series, CheckedTextView> seriesToShow;
    private Button cancelButton;
    private Button saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.schedulewidget_preferences);
        this.getExtraAppWidgetIdOrFinish();
        this.setUpViews();
        this.getActionBar().setTitle(R.string.widget_preferences);
    }

    private void getExtraAppWidgetIdOrFinish() {
        this.appWidgetId = this.tryGettingAppWidgetIdFromExtras();

        this.setResult(
                Activity.RESULT_CANCELED,
                new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.appWidgetId));

        if (this.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            this.finish();
        } else {
            this.preferences = App.preferences().forScheduleWidget(this.appWidgetId);
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

        switch (this.preferences.sortMode()) {
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

        this.showSpecialEpisodes.setChecked(this.preferences.showSpecialEpisodes());
        this.showSpecialEpisodes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleWidgetPreferenceActivity.this.showSpecialEpisodes.toggle();
            }
        });
    }

    private void setUpSeenEpisodesOptions() {
        this.showSeenEpisodes = (CheckedTextView) this.findViewById(R.id.showSeenEpisodes);

        this.showSeenEpisodes.setChecked(this.preferences.showSeenEpisodes());
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

        Collection<Series> followedSeries = App.seriesFollowingService().getAllFollowedSeries();
        int counter = 0;

        for (Series s : followedSeries) {
            View v = inflater.inflate(R.layout.schedulewidget_preferences_filter_option, null);
            final CheckedTextView seriesCheck = (CheckedTextView) v.findViewById(R.id.checkBox);

            seriesCheck.setText(s.name());
            seriesCheck.setChecked(this.preferences.showSeries(s.id()));
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
                this.preferences.putSortMode(SortMode.OLDEST_FIRST);
                break;
            case R.id.newest_first:
                this.preferences.putSortMode(SortMode.NEWEST_FIRST);
                break;
        }
    }

    private void saveSpecialEpisodesPreference() {
        this.preferences.putIfShowSpecialEpisodes(this.showSpecialEpisodes.isChecked());
    }

    private void saveSeenEpisodesPreference() {
        this.preferences.putIfShowWatchedEpisodes(this.showSeenEpisodes.isChecked());
    }

    private void saveSeriesToShowPreference() {
        for (Series s : this.seriesToShow.keySet()) {
            this.preferences.putIfShowSeries(s.id(), this.seriesToShow.get(s).isChecked());
        }
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
