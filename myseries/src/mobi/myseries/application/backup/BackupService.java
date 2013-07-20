package mobi.myseries.application.backup;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.ListenerSet;
import android.os.Handler;

public class BackupService {

    private static final long TIME_IN_SECONDS = 30;

    private ListenerSet<BackupListener> listeners;

    private SeriesRepository repository;

    private DropboxHelper dropboxHelper;

    private final AtomicBoolean isRunning;
    private final ExecutorService executor;

    private Handler handler;

    public BackupService(SeriesRepository repository,
            DropboxHelper dropboxHelper) {
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

    public void doBackup(final BackupMode backupMode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runBackup(backupMode);
            }
        }).start();
    }

    private void runBackup(BackupMode backupMode) {
        OperationTask backupTask;
        OperationResult result = null;

        backupTask = new BackupTask(backupMode, repository);
        try {
            Future<?> future = executor.submit(backupTask);
            future.get(TIME_IN_SECONDS, TimeUnit.SECONDS);
            result = backupTask.result();
            if (!result.success()) {
                this.notifyListenersOfBackupFailure((result.error()));
                return;
            }
        } catch (InterruptedException e) {
            // Should never happen
            e.printStackTrace();
            this.isRunning.set(false);
            return;

        } catch (ExecutionException e) {
            e.printStackTrace();
            notifyListenersOfBackupFailure((Exception) e.getCause());
            return;

        } catch (TimeoutException e) {
            e.printStackTrace();
            notifyListenersOfBackupFailure(new BackupTimeoutException(e));
            return;
        }
        this.notifyListenersOfBackupSucess();
    }

    public void restoreBackup(final BackupMode backupMode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runRestore(backupMode);
            }
        }).start();
    }

    private void runRestore(BackupMode backupMode) {
        OperationTask restoreTask;
        OperationResult result = null;

        restoreTask = new RestoreTask(backupMode, repository);
        try {
            Future<?> future = executor.submit(restoreTask);
            future.get(TIME_IN_SECONDS, TimeUnit.SECONDS);
            result = restoreTask.result();
            if (!result.success()) {
                this.notifyListenersOfRestoreFailure((result.error()));
                return;
            }
        } catch (InterruptedException e) {
            // Should never happen
            e.printStackTrace();
            this.isRunning.set(false);
            return;

        } catch (ExecutionException e) {
            e.printStackTrace();
            notifyListenersOfRestoreFailure((Exception) e.getCause());
            return;

        } catch (TimeoutException e) {
            e.printStackTrace();
            notifyListenersOfRestoreFailure(new RestoreTimeoutException(e));
            return;
        }
        this.notifyListenersOfRestoreSucess();
    }

    public DropboxHelper getDropboxHelper() {
        return this.dropboxHelper;
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

    private void notifyListenersOfBackupFailure(final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onBackupFailure(e);

                }
            }
        });
    }

    private void notifyListenersOfRestoreFailure(final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onRestoreFailure(e);
                }
            }
        });
    }

    private void notifyListenersOfRestoreSucess() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onRestoreSucess();
                }
            }
        });
    }

    public boolean register(BackupListener listener) {
        return this.listeners.register(listener);
    }

    public boolean deregister(BackupListener listener) {
        return this.listeners.deregister(listener);
    }
}
