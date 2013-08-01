package mobi.myseries.gui.update;

import java.util.Date;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.notification.DeterminateProgressNotification;
import mobi.myseries.application.notification.IndeterminateProgressNotification;
import mobi.myseries.application.notification.Notification;
import mobi.myseries.application.notification.NotificationDispatcher;
import mobi.myseries.application.notification.TextOnlyNotification;
import mobi.myseries.application.preferences.UpdatePreferences;
import mobi.myseries.application.update.listener.UpdateFinishListener;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.shared.ToastBuilder;
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
        this.setUpUpdateButton();

        App.updateSeriesService().register(this.updateFinishListener);
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
        return false; // TODO(Cleber): It should be top level some day.
    }

    @Override
    protected Intent navigateUpIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    private Button updateButton;

    private RadioGroup automaticUpdatesRadioGroup;

    private ProgressBar updateProgressBar;
    private TextView updateStatusTextView;

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

    private void setUpUpdateButton() {
        this.updateButton = (Button) this.findViewById(R.id.updateButton);
        this.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateActivity.this.runManualUpdate();
            }
        });
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

    private final NotificationDispatcher updateNotificationDispatcher = new NotificationDispatcher() {

        @Override
        public void notifyTextOnlyNotification(TextOnlyNotification notification) {
            updateProgressBar.setIndeterminate(false);
            updateProgressBar.setMax(0);
            updateProgressBar.setProgress(0);

            updateStatusTextView.setText(notification.message());
        }

        @Override
        public void notifyIndeterminateProgressNotification(
                IndeterminateProgressNotification notification) {
            updateProgressBar.setIndeterminate(true);

            updateStatusTextView.setText(notification.message());
        }

        @Override
        public void notifyDeterminateProgressNotification(
                DeterminateProgressNotification notification) {
            updateProgressBar.setIndeterminate(false);
            updateProgressBar.setMax(notification.totalProgress());
            updateProgressBar.setProgress(notification.currentProgress());

            updateStatusTextView.setText(notification.message());
        }

        @Override
        public void cancel(Notification notification) {
            updateProgressBar.setIndeterminate(false);
            updateProgressBar.setMax(0);
            updateProgressBar.setProgress(0);

            updateStatusTextView.setText(R.string.update_not_running);
        }
    };

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

    private void runManualUpdate() {
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_update).build().show();
        } else {
            App.updateSeriesService().updateData();
        }
    }

    private final UpdateFinishListener updateFinishListener = new UpdateFinishListener() {
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
