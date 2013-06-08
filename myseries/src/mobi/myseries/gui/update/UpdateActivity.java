package mobi.myseries.gui.update;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.preferences.Preferences;
import mobi.myseries.gui.preferences.UpdatePreferences;
import mobi.myseries.gui.shared.ToastBuilder;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class UpdateActivity extends Activity {

    public static Intent newIntent(Context context) {
        return new Intent(context, UpdateActivity.class);
    }

    private Button updateButton;

    private RadioGroup automaticUpdatesRadioGroup;
    private Button cancelButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.update);
        this.setResult(Activity.RESULT_CANCELED);
        this.setupActionBar();
        this.setupViews();
        this.loadSettings();
        this.setUpUpdateButton();
        this.setUpCancelButton();
        this.setUpSaveButton();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getActionBar();

        actionBar.setTitle(R.string.settings);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private void setupViews() {
        this.automaticUpdatesRadioGroup =
                (RadioGroup) this.findViewById(R.id.automaticUpdatesRadioGroup);
    }

    private void loadSettings() {
        UpdatePreferences settings = this.settingsProviderFor(this);

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

    private void setUpCancelButton() {
        this.cancelButton = (Button) this.findViewById(R.id.cancelButton);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateActivity.this.finish();

            }
        });
    }

    private void setUpSaveButton() {
        this.saveButton = (Button) this.findViewById(R.id.saveButton);

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateActivity.this.save();

            }
        });
    }

    protected void save() {
        this.saveSettings();
        this.finishOk();
    }

    private void saveSettings() {
        switch (this.automaticUpdatesRadioGroup.getCheckedRadioButtonId()) {
        case R.id.doNotUpdateRadioButton:
            this.settingsProviderFor(this).putUpdateAutomatically(false);
            break;

        case R.id.wifiOnlyRadioButton:
            this.settingsProviderFor(this).putUpdateAutomatically(true)
                    .putUpdateOnDataPlan(false);

            break;

        case R.id.wifiOrDataPlanRadioButton:
            this.settingsProviderFor(this).putUpdateAutomatically(true)
                    .putUpdateOnDataPlan(true);

            break;
        }
    }

    private void finishOk() {
        this.setResult(Activity.RESULT_OK);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private UpdatePreferences settingsProviderFor(Context context) {
        return Preferences.forUpdate();
    }

    private void runManualUpdate() {
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_update).build().show();
        } else {
            App.updateSeriesService().updateData();
        }
    }
}
