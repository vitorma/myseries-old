package mobi.myseries.application.preferences;

import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class PrimitivePreferences implements Publisher<SharedPreferences.OnSharedPreferenceChangeListener>{
    public static final String SEPARATOR = ".";
    private static final String DEFAULT_KEY_PREFIX = "";
    private static final String DEFAULT_KEY_SUFFIX = "";

    private Context context;
    private String name;
    private String keyPrefix;
    private String keySuffix;

    public PrimitivePreferences(Context context, String name) {
        Validate.isNonNull(context, "context");
        Validate.isNonBlank(name, "name");

        this.context = context;
        this.name = name;
        this.keyPrefix = DEFAULT_KEY_PREFIX;
        this.keySuffix = DEFAULT_KEY_SUFFIX;
    }

    public PrimitivePreferences addKeyPrefix(String prefix) {
        Validate.isNonBlank(prefix, "prefix");

        this.keyPrefix += prefix + SEPARATOR ;

        return this;
    }

    public PrimitivePreferences addKeySuffix(String suffix) {
        Validate.isNonBlank(suffix, "suffix");

        this.keySuffix += SEPARATOR + suffix;

        return this;
    }

    public int getInt(String key, int defaultValue) {
        return this.getSharedPreferences().getInt(this.compose(key), defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getSharedPreferences().getBoolean(this.compose(key), defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return this.getSharedPreferences().getString(this.compose(key), defaultValue);
    }

    public void putInt(String key, int value) {
        this.getEditor().putInt(this.compose(key), value).commit();
    }

    public void putBoolean(String key, boolean value) {
        this.getEditor().putBoolean(this.compose(key), value).commit();
    }

    public void putString(String key, String value) {
        this.getEditor().putString(this.compose(key), value).commit();
    }

    public void remove(String key) {
        this.getEditor().remove(this.compose(key)).commit();
    }

    public void clear() {
        for (String key : this.getSharedPreferences().getAll().keySet()) {
            if (key.startsWith(this.keyPrefix) && key.endsWith(this.keySuffix)) {
                this.remove(key);
            }
        }
    }

    private SharedPreferences getSharedPreferences() {
        return this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return this.getSharedPreferences().edit();
    }

    public String compose(String key) {
        Validate.isNonBlank(key, "key");

        return this.keyPrefix + key + this.keySuffix;
    }

    @Override
    public boolean register(OnSharedPreferenceChangeListener listener) {
        this.getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        return true;
    }

    @Override
    public boolean deregister(OnSharedPreferenceChangeListener listener) {
        this.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
        return true;
    }
}
