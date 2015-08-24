package mobi.myseries.application.activityevents;

import android.content.Intent;

public interface ActivityEventsListener {
    public void onActivityResult(int requestCode, int resultCode, Intent data);
}
