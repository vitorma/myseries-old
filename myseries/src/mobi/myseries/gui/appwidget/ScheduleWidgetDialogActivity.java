package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.schedule.dualpane.ScheduleDualPaneActivity;
import mobi.myseries.gui.schedule.singlepane.ScheduleDetailActivity;
import mobi.myseries.gui.shared.Extra;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ScheduleWidgetDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.schedulewidget_popup);

        final int scheduleMode = getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
        final int position = getIntent().getExtras().getInt(Extra.POSITION);
        ScheduleSpecification specification = App.preferences().forSchedule().fullSpecification();
        final Episode episode = App.schedule().mode(scheduleMode, specification).episodeAt(position);

        TextView toggleEpisodeWatchMark = (TextView) findViewById(R.id.toggleEpisodeWatchMark);
        toggleEpisodeWatchMark.setText(
                episode.watched() ?
                R.string.schedulewidget_dialog_mark_episode_as_unwatched :
                R.string.schedulewidget_dialog_mark_episode_as_watched);
        toggleEpisodeWatchMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (episode.watched()) {
                    App.markingService().markAsUnwatched(episode);
                } else {
                    App.markingService().markAsWatched(episode);
                }

                finish();
            }
        });

        TextView seeEpisodeDetails = (TextView) findViewById(R.id.seeEpisodeDetails);
        seeEpisodeDetails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDualPane = App.resources().getBoolean(R.bool.isTablet);

                Intent intent = new Intent(
                        getApplicationContext(),
                        isDualPane ? ScheduleDualPaneActivity.class : ScheduleDetailActivity.class);

                intent
                    .putExtra(Extra.SCHEDULE_MODE, scheduleMode)
                    .putExtra(Extra.POSITION, position)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                finish();
                startActivity(intent);
            }
        });
    }
}
