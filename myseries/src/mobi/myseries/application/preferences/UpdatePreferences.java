package mobi.myseries.application.preferences;

public class UpdatePreferences {
    /* XXX (Cleber) Get these values from strings.xml */
    public static final String UPDATE_AUTOMATICALLY_ALWAYS = "UPDATE_AUTOMATICALLY_ALWAYS";
    public static final String UPDATE_AUTOMATICALLY_ONLY_ON_WIFI = "UPDATE_AUTOMATICALLY_ONLY_ON_WIFI";
    public static final String UPDATE_AUTOMATICALLY_NEVER = "UPDATE_AUTOMATICALLY_NEVER";

    /* Full name after the prefix is added: Update.whenUpdateAutomatically */
    /* XXX (Cleber) Get this value from strings.xml */
    private static final String WHEN_UPDATE_AUTOMATICALLY_KEY = "whenUpdateAutomatically";

    private PrimitivePreferences mPrimitive;

    public UpdatePreferences(PrimitivePreferences primitive) {
        mPrimitive = primitive;
    }

    public String whenUpdateAutomatically() {
        return mPrimitive.getString(WHEN_UPDATE_AUTOMATICALLY_KEY, UPDATE_AUTOMATICALLY_ALWAYS);
    }

    public boolean updateAutomatically() {
        return !updateNever();
    }

    public boolean updateAlways() {
        return whenUpdateAutomatically().equals(UPDATE_AUTOMATICALLY_ALWAYS);
    }

    public boolean updateOnlyOnWifi() {
        return whenUpdateAutomatically().equals(UPDATE_AUTOMATICALLY_ONLY_ON_WIFI);
    }

    public boolean updateNever() {
        return whenUpdateAutomatically().equals(UPDATE_AUTOMATICALLY_NEVER);
    }

    public UpdatePreferences putWhenUpdateAutomatically(String whenUpdateAutomatically) {
        mPrimitive.putString(WHEN_UPDATE_AUTOMATICALLY_KEY, whenUpdateAutomatically);

        return this;
    }
}
