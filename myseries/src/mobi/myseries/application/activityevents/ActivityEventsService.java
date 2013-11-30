package mobi.myseries.application.activityevents;

import android.content.Intent;
import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;

/**
 * Notifies about activity's events. It was first created to decouple the features Store code
 * in application package from the features Activity's code for In App Billing purchases on
 * Google Play.
 *
 * Most notifications should be implemented on gui.activity.base.BaseActivity. Check that
 * class if you are looking for some bug ..Ahem.. event source.
 *
 * @author Gabriel Assis Bezerra
 */
public class ActivityEventsService extends ApplicationService<ActivityEventsListener> {

    public ActivityEventsService(Environment environment) {
        super(environment);
    }

    public void notifyOnActivityResult(int requestCode, int resultCode, Intent data) {
        for (ActivityEventsListener l : this.listeners()) {
            l.onActivityResult(requestCode, resultCode, data);
        }
    }
}
