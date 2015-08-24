package mobi.myseries.application.notification;

import android.net.Uri;

public abstract class Notification {

    private final int id;
    private final boolean isContinuous;
    private final CharSequence message;
    private Uri mSoundUri;
    private boolean mVibration;

    protected Notification(int id, boolean isContinuous, CharSequence message) {
        this.id = id;
        this.isContinuous = isContinuous;
        this.message = message;
        this.mSoundUri = null;
        this.mVibration = false;
    }

    public void setVibration(boolean vibrationEnabled) {
        this.mVibration = vibrationEnabled;

    }

    public boolean vibration() {
        return this.mVibration;
    }

    public void setSoundUri(Uri soundUri) {
        this.mSoundUri = soundUri;
    }

    public Uri soundUri() {
        return this.mSoundUri;
    }

    public int id() {
        return this.id;
    }

    public boolean isContinuous() {
        return this.isContinuous;
    }

    public CharSequence message() {
        return this.message;
    }

    public abstract void notifyVisit(NotificationDispatcher dispatcher);
}
