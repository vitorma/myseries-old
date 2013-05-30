package mobi.myseries.application.update.listener;

public interface UpdateProgressListener {
    public void onCheckingForUpdates();
    public void onUpdateNotNecessary();

    public void onUpdateProgress(int current, int total);

    public void onUpdateSuccess();
    public void onUpdateFailure(Exception e);
}
