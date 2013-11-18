package mobi.myseries.gui.update;

import java.util.Date;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.notification.NotificationDispatcher;
import mobi.myseries.application.preferences.UpdatePreferences;
import mobi.myseries.application.update.BaseUpdateListener;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.shared.NotificationDispatcherForOrdinaryViews;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

public class UpdateActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, UpdateActivity.class);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        this.setupViews();
        this.loadSettings();

        App.updateSeriesService().register(this.updateListener);
    }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.updates);
    }

    @Override
    protected int layoutResource() {
        return R.layout.update;
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }

    private Button updateButton;

    private RadioGroup automaticUpdatesRadioGroup;

    private ProgressBar updateProgressBar;
    private TextView updateStatusTextView;
    private NotificationDispatcher updateNotificationDispatcher;

    private TextView latestSuccessfulUpdateTextView;

    private void setupViews() {
        this.automaticUpdatesRadioGroup = (RadioGroup) this.findViewById(R.id.automaticUpdatesRadioGroup);
        this.automaticUpdatesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                saveSettings(checkedId);
            }
        });

        this.updateProgressBar = (ProgressBar) this.findViewById(R.id.updateNotificationProgressBar);
        this.updateStatusTextView = (TextView) this.findViewById(R.id.updateNotificationStatusMessage);
        this.updateNotificationDispatcher = new NotificationDispatcherForOrdinaryViews(updateStatusTextView, updateProgressBar,
                                                                                       this.getText(R.string.update_not_running));

        this.latestSuccessfulUpdateTextView = (TextView) this.findViewById(R.id.latestSuccessfulUpdateMessage);
        this.refreshLatestSuccessfulUpdateTextView();
    }

    private void saveSettings(int checkedButtonId) {
        switch (checkedButtonId) {
        case R.id.doNotUpdateRadioButton:
            this.updatePreferences()
                    .putUpdateAutomatically(false);
            break;

        case R.id.wifiOnlyRadioButton:
            this.updatePreferences()
                    .putUpdateAutomatically(true)
                    .putUpdateOnDataPlan(false);
            break;

        case R.id.wifiOrDataPlanRadioButton:
            this.updatePreferences()
                    .putUpdateAutomatically(true)
                    .putUpdateOnDataPlan(true);
            break;
        }
    }

    private void loadSettings() {
        UpdatePreferences settings = this.updatePreferences();

        if (!settings.updateAutomatically()) {
            this.automaticUpdatesRadioGroup.check(R.id.doNotUpdateRadioButton);

        } else if (settings.updateOnDataPlan()) {
            this.automaticUpdatesRadioGroup.check(R.id.wifiOrDataPlanRadioButton);

        } else {
            this.automaticUpdatesRadioGroup.check(R.id.wifiOnlyRadioButton);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.notificationService().setUpdateNotificationDispatcher(this.updateNotificationDispatcher);
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.notificationService().removeUpdateNotificationDispatcher(this.updateNotificationDispatcher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private UpdatePreferences updatePreferences() {
        return App.preferences().forUpdate();
    }

    private final UpdateListener updateListener = new BaseUpdateListener() {
        @Override
        public void onUpdateFinish() {
            refreshLatestSuccessfulUpdateTextView();
        }
    };

    private void refreshLatestSuccessfulUpdateTextView() {
        Long latestSuccessfulUpdateLong = App.updateSeriesService().latestSuccessfulUpdate();

        if (latestSuccessfulUpdateLong == null) {
            this.latestSuccessfulUpdateTextView.setVisibility(View.INVISIBLE);
        } else {
            Date latestSuccessfulUpdate = new Date(latestSuccessfulUpdateLong);

            String date = DateFormat.getDateFormat(this).format(latestSuccessfulUpdate);
            String time = DateFormat.getTimeFormat(this).format(latestSuccessfulUpdate);

            this.latestSuccessfulUpdateTextView.setText(this.getString(R.string.latest_successful_update, date, time));
            this.latestSuccessfulUpdateTextView.setVisibility(View.VISIBLE);
        }
    }
}
