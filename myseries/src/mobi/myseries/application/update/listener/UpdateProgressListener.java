package mobi.myseries.application.update.listener;

public interface UpdateProgressListener {
    public void onCheckingForUpdates();
    public void onUpdateNotNecessary();

    public void onUpdateStart();

    public void onUpdateProgress(float progress);

    public void onUpdateSuccess();
    public void onUpdateFailure(Exception e);
}
