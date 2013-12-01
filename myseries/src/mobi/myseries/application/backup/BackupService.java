package mobi.myseries.application.backup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import mobi.myseries.application.App;
import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.Environment;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.application.backup.json.EpisodeSnippet;
import mobi.myseries.application.backup.json.JsonHelper;
import mobi.myseries.application.backup.json.SeriesSnippet;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ParsingFailedException;

public class BackupService extends ApplicationService<BackupListener> {

    private ImageService imageService;
    private RestoreTask currentRestoreTask;
    private boolean restoreIsRunning = false;

    public BackupService(Environment environment, ImageService imageService) {
        super(environment);
        this.imageService = imageService;
    }

    public void doBackup(final BackupMode backupMode) {
        notifyOnBackupStart();
        this.run(new Runnable() {
            @Override
            public void run() {
                File jsonFile = null;
                try {
                    jsonFile = createSeriesJsonFile();
                } catch (Exception e) {
                    notifyOnBackupFail(e);
                    return;
                }

                try {
                    notifyOnBackupRunning(backupMode);
                    backupMode.backupFile(jsonFile);
                    notifyOnBackupCompleted(backupMode);
                } catch (Exception e) {
                    notifyOnBackupFail(backupMode, e);
                }
            }
        });
    }

    public void doBackup(final Collection<BackupMode> backupModes) {
        notifyOnBackupStart();
        this.run(new Runnable() {
            @Override
            public void run() {
                File jsonFile = null;
                try {
                    jsonFile = createSeriesJsonFile();
                } catch (Exception e) {
                    notifyOnBackupFail(e);
                    return;
                }
                for (BackupMode backupMode : backupModes) {
                    try {
                        notifyOnBackupRunning(backupMode);
                        backupMode.backupFile(jsonFile);
                        notifyOnBackupCompleted(backupMode);
                    } catch (Exception e) {
                        notifyOnBackupFail(backupMode, e);
                    }
                    
                }
            }
        });
    }

    public void restoreBackup(BackupMode backupMode) {
        this.currentRestoreTask = new RestoreTask(environment(), backupMode,
                imageService);
        App.updateSeriesService().cancel();
        this.run(this.currentRestoreTask);
    }

    public void cancelCurrentRestore() {
        if (currentRestoreTask == null)
            return;
        currentRestoreTask.cancel();
        currentRestoreTask = null;
    }

    public DropboxHelper dropboxHelper() {
        return environment().dropboxHelper();
    }

    private File createSeriesJsonFile() throws IOException,
            FileNotFoundException {

        File seriesCacheFile = new File(App.context().getCacheDir(),
                "myseries.bkp");
        Collection<Series> series = environment().seriesRepository().getAll();
        JsonHelper.writeSeriesJsonStream(new BufferedOutputStream(
                new FileOutputStream(seriesCacheFile)), series);
        return seriesCacheFile;
    }

    public class RestoreTask implements Runnable {
        private BackupMode backupMode;

        private boolean isCancelled = false;

        public RestoreTask(Environment environment, BackupMode backupMode,
                ImageService imageService) {
            this.backupMode = backupMode;
        }

        public void run() {
            notifyOnRestoreRunning(backupMode);
            restoreIsRunning = true;
            Collection<SeriesSnippet> seriesJson = null;
            try {
                seriesJson = this.getSeriesFromFile("myseries.bkp");
                this.restoreSeries(seriesJson);
                if(isCancelled()) {
                    notifyRestoreCancel();
                    return;
                }
                notifyOnRestoreCompleted(backupMode);
            } catch (Exception e) {
                notifyOnRestoreFail(backupMode, e);
            } finally {
                restoreIsRunning = false;
            }
        }

        private Collection<SeriesSnippet> getSeriesFromFile(String fileName)
                throws Exception, IOException {
            File cacheFile = null;
            try {
                cacheFile = new File(App.context().getCacheDir(), fileName);
                this.backupMode.downloadBackupToFile(cacheFile);
                return JsonHelper.readSeriesJsonStream(new BufferedInputStream(
                        new FileInputStream(cacheFile)));
            } finally {
                if (cacheFile != null)
                    cacheFile.delete();
            }
        }

        private void restoreSeries(Collection<SeriesSnippet> series)
                throws ParsingFailedException, ConnectionFailedException,
                NetworkUnavailableException {
            Collection<Series> seriesToRestore = new ArrayList<Series>();

            for (SeriesSnippet s : series) {
                if (this.isCancelled())
                    return;
                Series fetchedSeries = environment().traktApi().fetchSeries(
                        s.id());
                for (EpisodeSnippet episodeSnippet : s.episodes()) {
                    for(Episode episode : fetchedSeries.episodes())
                        if(episodeSnippet.isTheSameAs(episode)) {
                            episode.markAsWatched();
                    }
                }
                seriesToRestore.add(fetchedSeries);
                notifyRestoreProgress(seriesToRestore.size(), series.size());
            }
            if (this.isCancelled())
                return;

            environment().seriesRepository().clear();
            imageService.clear();

            for (Series s : seriesToRestore) {
                if (this.isCancelled())
                    return;
                environment().seriesRepository().insert(s);
            }

            int current = 0;
            for (Series s : seriesToRestore) {
                current++;
                if (this.isCancelled())
                    return;
                imageService.downloadAndSavePosterOf(s);
                notifyRestorePosterDownloadProgress(current,
                        seriesToRestore.size());
            }
        }

        public boolean isCancelled() {
            return this.isCancelled;
        }

        public void cancel() {
            this.isCancelled = true;
        }
    }

    private void notifyOnBackupRunning(final BackupMode backupMode) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onBackupRunning(backupMode);
                }
            }
        });
    }

    private void notifyOnBackupStart() {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onBackupStart();
                }
            }
        });
    }

    private void notifyOnBackupCompleted(final BackupMode backupMode) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onBackupCompleted(backupMode);
                }
            }
        });
    }

    private void notifyOnBackupFail(final Exception e) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onBackupFailure(e);
                }
            }
        });
    }

    private void notifyOnBackupFail(final BackupMode backupMode,
            final Exception exception) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onBackupFailure(backupMode, exception);
                }
            }
        });
    }

    private void notifyOnRestoreRunning(final BackupMode backupMode) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onRestoreRunning(backupMode);
                }
            }
        });
    }

    private void notifyOnRestoreFail(final BackupMode backupMode,
            final Exception exception) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onRestoreFailure(backupMode, exception);
                }
            }
        });
    }

    private void notifyRestoreProgress(final int current, final int total) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onRestoreProgress(current, total);
                }
            }
        });
    }

    private void notifyRestorePosterDownloadProgress(final int current,
            final int total) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onRestorePosterDownloadProgress(current, total);
                }
            }
        });
    }

    private void notifyRestoreCancel() {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onRestoreCancelled();
                }
            }
        });
    }
    
    private void notifyOnRestoreCompleted(final BackupMode backupMode) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (BackupListener listener : listeners()) {
                    listener.onRestoreCompleted(backupMode);
                }
            }
        });
    }

    public boolean restoreIsRunning() {
        return restoreIsRunning;
    }

}
