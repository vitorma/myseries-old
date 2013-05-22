package mobi.myseries.application.backup;

import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.ListenerSet;
import android.os.AsyncTask;

public class BackupService {

    private ListenerSet<BackupListener> listeners;

    private SeriesRepository repository;

    //private SharedPreferences preferences;

    public BackupService(SeriesRepository repository) {
        this.listeners = new ListenerSet<BackupListener>();
        this.repository = repository;
        //this.preferences = App.context().getSharedPreferences("mobi.myseries.gui.settings.MySeriesPreferences", 0);
}
    public void doBackup(BackupMode backupMode) {
        new doBackupAsyncTask(backupMode).execute();
    }

    public void restoreBackup(BackupMode backupMode) {
        new restoreBackupAsyncTask(backupMode).execute();
    }

    private class doBackupAsyncTask extends AsyncTask<Void, Void, AsyncTaskResult<Void>> {

        private BackupMode backupMode;

        public doBackupAsyncTask(BackupMode backupMode) {
            this.backupMode = backupMode;
        }

        @Override
        protected AsyncTaskResult<Void> doInBackground(Void... params) {
            try {
                backupMode.backupDB(repository.db());
                //backupMode.backupPreferences(preferences);
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

        private BackupMode backupMode;

        public restoreBackupAsyncTask(BackupMode backupMode) {
            this.backupMode = backupMode;
        }

        @Override
        protected AsyncTaskResult<Void> doInBackground(Void... params) {
            
            try {
                repository.restoreFrom(this.backupMode.getBackup().getAbsolutePath());
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
