package mobi.myseries.application.backup;

import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.ListenerSet;
import android.os.AsyncTask;

public class BackupService {

    private SdcardBackup sdcardBackup;

    private ListenerSet<BackupListener> listeners;

    private SeriesRepository repository;

    public BackupService(SeriesRepository repository) {
        this.sdcardBackup = new SdcardBackup();
        this.listeners = new ListenerSet<BackupListener>();
        this.repository = repository;
}
    public void doBackup() {
        new doBackupAsyncTask().execute();
    }

    public void restoreBackup() {
        new restoreBackupAsyncTask().execute();
    }

    public String sdCardPath() {
        return sdcardBackup.backupFilePath();
    }

    private class doBackupAsyncTask extends AsyncTask<Void, Void, AsyncTaskResult<Void>> {

        @Override
        protected AsyncTaskResult<Void> doInBackground(Void... params) {
            
            try {
                repository.exportTo(sdcardBackup.backupFilePath());
            } catch (Exception e) {
                return new AsyncTaskResult<Void>(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Void> result) {
            if (result == null) {
                notifyListenersOfBackupSucess();
            } else {
                notifyListenersOfFailure(result.error());
            }
        }
    }
    
    private class restoreBackupAsyncTask extends AsyncTask<Void, Void, AsyncTaskResult<Void>> {

        @Override
        protected AsyncTaskResult<Void> doInBackground(Void... params) {
            
            try {
                repository.restoreFrom(sdcardBackup.backupFilePath());
            } catch (Exception e) {
                return new AsyncTaskResult<Void>(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Void> result) {
            if (result == null) {
                notifyListenersOfRestoreSucess();
            } else {
                notifyListenersOfRestoreFailure(result.error());
            }
        }
    }

    private void notifyListenersOfBackupSucess() {
        for (BackupListener listener : listeners) {
            listener.onBackupSucess();
        }
    }

    private void notifyListenersOfFailure(Exception e) {
        for (BackupListener listener : listeners) {
            listener.onBackupFailure(e);
        }
    }
    
    private void notifyListenersOfRestoreFailure(Exception e) {
        for (BackupListener listener : listeners) {
            listener.onRestoreFailure(e);
        }
    }

    private void notifyListenersOfRestoreSucess() {
        for (BackupListener listener : listeners) {
            listener.onRestoreSucess();
        }
    }

    public boolean register(BackupListener listener) {
        return this.listeners.register(listener);
    }

    public boolean deregister(BackupListener listener) {
        return this.listeners.deregister(listener);
    }
}
