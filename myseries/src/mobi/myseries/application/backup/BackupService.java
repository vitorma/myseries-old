package mobi.myseries.application.backup;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.ListenerSet;
import android.os.AsyncTask;
import android.os.Handler;

public class BackupService {

    private static final long TIME_IN_MINUTES = 1;

    private ListenerSet<BackupListener> listeners;

    private SeriesRepository repository;

    private DropboxHelper dropboxHelper;
    
    private final AtomicBoolean isRunning;
    private final ExecutorService executor;

    private Handler handler;

    public BackupService(SeriesRepository repository, DropboxHelper dropboxHelper) {
        this.listeners = new ListenerSet<BackupListener>();
        this.repository = repository;
        this.dropboxHelper = dropboxHelper;
        
        this.isRunning = new AtomicBoolean(false);
        this.executor = Executors.newSingleThreadExecutor();
}
    
    public BackupService withHandler(Handler handler) {
        this.handler = handler;
        return this;
    }

    public void doBackup(BackupMode backupMode) {
        OperationTask backupTask;
        OperationResult result = null;

        backupTask =
                new BackupTask(backupMode, repository);
        try {
            Future<?> future = executor.submit(backupTask);
            future.get(TIME_IN_MINUTES, TimeUnit.MINUTES);
            result = backupTask.result();

            if (!result.success()) {
                this.notifyListenersOfFailure((result.error()));
                return;
            }
        } catch (InterruptedException e) {
            // Should never happen
            e.printStackTrace();
            this.isRunning.set(false);
            return;

        } catch (ExecutionException e) {
            e.printStackTrace();
            notifyListenersOfFailure((Exception) e.getCause());
            return;

        } catch (TimeoutException e) {
            e.printStackTrace();
            notifyListenersOfFailure(new BackupTimeoutException(e));
            return;
        }
        this.notifyListenersOfBackupSucess();
    }

    public void restoreBackup(BackupMode backupMode) {
        new restoreBackupAsyncTask(backupMode).execute();
    }
    
    public DropboxHelper getDropboxHelper() {
        return this.dropboxHelper;
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
        this.isRunning.set(false);

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onBackupSucess();
                
                }
            }
        });
    }

    private void notifyListenersOfFailure(final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onRestoreFailure(e);
                
                }
            }
        });
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
