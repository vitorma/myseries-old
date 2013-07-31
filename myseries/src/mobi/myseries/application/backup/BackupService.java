package mobi.myseries.application.backup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import mobi.myseries.application.App;
import mobi.myseries.application.backup.exception.BackupTimeoutException;
import mobi.myseries.application.backup.exception.RestoreTimeoutException;
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

    private List<BackupMode> backupQueue = new ArrayList<BackupMode>();

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

    public OperationResult doBackup(final BackupMode backupMode) {
        return runBackup(backupMode);
    }

    private OperationResult runBackup(BackupMode backupMode) {
        OperationTask backupTask;
        OperationResult result = null;

        backupTask = new BackupTask(backupMode, repository);
        try {
            Future<?> future = executor.submit(backupTask);
            future.get(TIME_IN_SECONDS, TimeUnit.SECONDS);
            result = backupTask.result();
            // if (!result.success()) {
            // this.notifyListenersOfBackupFailure((result.error()));
            return result;
        } catch (InterruptedException e) {
            // Should never happen
            e.printStackTrace();
            this.isRunning.set(false);
            return new OperationResult().withError(e);

        } catch (ExecutionException e) {
            return new OperationResult().withError(e);

        } catch (TimeoutException e) {
            return new OperationResult()
                    .withError(new BackupTimeoutException(e));
        }
    }

    public OperationResult restoreBackup(final BackupMode backupMode) {
        return runRestore(backupMode);
    }

    private OperationResult runRestore(BackupMode backupMode) {
        OperationTask restoreTask;
        OperationResult result = null;

        restoreTask = new RestoreTask(backupMode, repository);
        try {
            Future<?> future = executor.submit(restoreTask);
            future.get(TIME_IN_SECONDS, TimeUnit.SECONDS);
            result = restoreTask.result();
            return result;
        } catch (InterruptedException e) {
            // Should never happen
            e.printStackTrace();
            this.isRunning.set(false);
            return new OperationResult().withError(e);

        } catch (ExecutionException e) {
            e.printStackTrace();
            return new OperationResult().withError((Exception) e.getCause());

        } catch (TimeoutException e) {
            e.printStackTrace();
            return new OperationResult().withError(new RestoreTimeoutException(
                    e));
        }
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

    private void notifyListenersOfBackupFailure(final BackupMode mode,
            final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onBackupFailure(mode, e);

                }
            }
        });
    }

    private void notifyListenersOfRestoreFailure(final BackupMode mode,
            final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onRestoreFailure(mode, e);
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

    public void addToqueue(BackupMode backupMode) {
        this.backupQueue.add(backupMode);

    }

    public void performBackup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (backupQueue.iterator().hasNext()) {
                    BackupMode mode = backupQueue.iterator().next();
                    performBackup(mode);
                }
            }
        }).start();

    }

    public void performBackup(BackupMode mode) {
        notifyListenersOfBackupRunning(mode);
        OperationResult result = doBackup(mode);
        if (!result.success()) {
            notifyListenersOfBackupFailure(mode, result.error());
        } else {
            notifyListenersOfBackupCompleted(mode);
        }
        backupQueue.remove(mode);
        if (backupQueue.iterator().hasNext()) {
            BackupMode mode2 = backupQueue.iterator().next();
            performBackup(mode2);
        }
    }

    public void performRestore(final BackupMode mode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                notifyListenersOfRestoreRunning(mode);
                OperationResult result = restoreBackup(mode);
                if (!result.success()) {
                    notifyListenersOfRestoreFailure(mode, result.error());
                } else {
                    notifyListenersOfRestoreCompleted(mode);
                    App.updateSeriesService().updateData();
                }
            }
        }).start();
    }

    private void notifyListenersOfBackupCompleted(final BackupMode mode) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onBackupCompleted(mode);
                }
            }
        });

    }

    private void notifyListenersOfRestoreCompleted(final BackupMode mode) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onRestoreCompleted(mode);
                }
            }
        });

    }

    private void notifyListenersOfBackupRunning(final BackupMode mode) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onBackupRunning(mode);
                }
            }
        });

    }

    private void notifyListenersOfRestoreRunning(final BackupMode mode) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners) {
                    listener.onRestoreRunning(mode);
                }
            }
        });

    }
}
