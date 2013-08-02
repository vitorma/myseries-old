package mobi.myseries.application.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import mobi.myseries.application.App;
import mobi.myseries.application.backup.json.JsonHelper;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.FilesUtil;

public class RestoreTask implements OperationTask {
    private BackupMode backupMode;
    private OperationResult result;
    private SeriesRepository repository;
    
    public RestoreTask(BackupMode backupMode, SeriesRepository repository) {
        this.backupMode = backupMode;
        this.repository = repository;
    }

    @Override
    public void run() {
        try {
            Collection<Series> seriesJson = getSeriesFromFile("myseries.json");
            restoreSeries(seriesJson);
            String preferencesJson = getJsonFromFile("preferences.json");
            restorePreferences(preferencesJson);
        } catch (Exception e) {
            this.result = new OperationResult().withError(e);
            e.printStackTrace();
            return;
        }
        this.result = new OperationResult();

    }
    
    private String getJsonFromFile(String jsonFileName) throws Exception, IOException {
        File cacheFile = new File(App.context().getCacheDir(), jsonFileName);
        backupMode.downloadBackupToFile(cacheFile);
        String cacheFilePath = cacheFile.getAbsolutePath();
        String stringContent = FilesUtil.readFileAsString(cacheFilePath, null);
        return stringContent;
    }

    private Collection<Series> getSeriesFromFile(String jsonFileName) throws Exception, IOException {
        File cacheFile = new File(App.context().getCacheDir(), jsonFileName);
        backupMode.downloadBackupToFile(cacheFile);
        JsonHelper.readSeriesJsonStream(new FileInputStream(cacheFile));
        return JsonHelper.readSeriesJsonStream(new FileInputStream(cacheFile));
    }

    private void restoreSeries(Collection<Series> series) {
        for (Series s : series) {
            repository.insert(s);
        }
    }

    private void restorePreferences(String stringContent) {
        Map<String,?> preferences = JsonHelper.preferencesFromJson(stringContent);
        Editor preferenceEditor = App.context().getSharedPreferences("mobi.myseries.preferences", Context.MODE_PRIVATE).edit();
        for (Entry<String, ?> entry : preferences.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof Boolean)
                preferenceEditor.putBoolean(key, ((Boolean) value).booleanValue());
            else if (value instanceof Float)
                preferenceEditor.putFloat(key, ((Float) value).floatValue());
            else if (value instanceof Integer)
                preferenceEditor.putInt(key, ((Integer) value).intValue());
            else if (value instanceof Long)
                preferenceEditor.putLong(key, ((Long) value).longValue());
            else if (value instanceof String)
                preferenceEditor.putString(key, ((String) value));
        }
        preferenceEditor.commit();
    }

    @Override
    public OperationResult result() {
        return this.result;
    }

}
