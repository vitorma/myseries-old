package mobi.myseries.gui.preferences;

public abstract class BackupPreferences<T extends BackupPreferences<T>> {
    private static final String ACCOUNT = "Account";
    private static final String REMOTE_LOCATION = "RemoteLocation";
    private static final String DEFAULT_REMOTE_LOCATION = "Apps/MySeries";
    private PrimitivePreferences primitive;

    public BackupPreferences(String name) {
        this.primitive = new PrimitivePreferences(name);
    }
    
    @SuppressWarnings("unchecked")
    public <S> T suffixingKeysWith(S suffix) {
        this.primitive.appendingSuffixToKeys(suffix.toString());

        return (T) this;
    }
    
}

