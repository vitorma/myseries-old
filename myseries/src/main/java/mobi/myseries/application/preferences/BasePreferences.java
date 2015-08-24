package mobi.myseries.application.preferences;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class BasePreferences implements Publisher<SharedPreferences.OnSharedPreferenceChangeListener> {
    private static final String SEPARATOR = "_";
    private static final String DEFAULT_KEY_PREFIX = "";
    private static final String DEFAULT_KEY_SUFFIX = "";

    private Context mContext;
    private String mKeyPrefix;
    private String mKeySuffix;

    public BasePreferences(Context context) {
        Validate.isNonNull(context, "context");

        mContext = context;
        mKeyPrefix = DEFAULT_KEY_PREFIX;
        mKeySuffix = DEFAULT_KEY_SUFFIX;
    }

    public int getInt(String key, int defaultValue) {
        return getSharedPreferences().getInt(compose(key), defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(compose(key), defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return getSharedPreferences().getString(compose(key), defaultValue);
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return getSharedPreferences().getStringSet(compose(key), defaultValue);
    }

    public void putInt(String key, int value) {
        getEditor().putInt(compose(key), value).commit();
    }

    public void putBoolean(String key, boolean value) {
        getEditor().putBoolean(compose(key), value).commit();
    }

    public void putString(String key, String value) {
        getEditor().putString(compose(key), value).commit();
    }

    public void putStringSet(String key, Set<String> value) {
        getEditor().putStringSet(compose(key), value).commit();
    }

    /* Publisher<SharedPreferences.OnSharedPreferenceChangeListener> */

    @Override
    public boolean register(OnSharedPreferenceChangeListener listener) {
        getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);

        return true;
    }

    @Override
    public boolean deregister(OnSharedPreferenceChangeListener listener) {
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);

        return true;
    }

    /* Auxiliary */

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    private SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    private String compose(String key) {
        Validate.isNonBlank(key, "key");

        return mKeyPrefix + key + mKeySuffix;
    }

    protected String key(int keyResourceId) {
        return mContext.getString(keyResourceId);
    }

    protected String key(int keyResourceId, Object... args) {
        return mContext.getString(keyResourceId, args);
    }

    protected String stringValue(int valueResourceId) {
        return mContext.getString(valueResourceId);
    }

    protected void addKeyPrefix(String prefix) {
        Validate.isNonBlank(prefix, "prefix");

        mKeyPrefix += prefix + SEPARATOR ;
    }

    protected void addKeySuffix(String suffix) {
        Validate.isNonBlank(suffix, "suffix");

        mKeySuffix += SEPARATOR + suffix;
    }

    protected void addValueToStringSet(String key, String value) {
        Set<String> oldSet = getStringSet(key, new HashSet<String>());
        Set<String> newSet = new HashSet<String>(oldSet);

        newSet.add(value);

        putStringSet(key, newSet);
    }

    protected void removeValueFromStringSet(String key, String value) {
        Set<String> oldSet = getStringSet(key, new HashSet<String>());
        Set<String> newSet = new HashSet<String>();

        for (String s : oldSet) {
            if (!s.equals(value)) { newSet.add(s); }
        }

        putStringSet(key, newSet);
    }

    protected void removeAllValuesFromStringSet(String key, Collection<String> values) {
        Set<String> oldSet = getStringSet(key, new HashSet<String>());
        Set<String> newSet = new HashSet<String>(oldSet);

        newSet.removeAll(values);

        putStringSet(key, newSet);
    }

    protected void remove(String key) {
        getEditor().remove(compose(key)).commit();
    }
}
