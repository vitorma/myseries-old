package mobi.myseries.application.preferences;

public class BackupPreferences {
    private static final String ACCOUNT = "Account";
    private static final String REMOTE_LOCATION = "RemoteLocation";
    private static final String DEFAULT_REMOTE_LOCATION = "Apps/MySeries";

    private PrimitivePreferences primitive;

    public BackupPreferences(PrimitivePreferences primitive) {
        this.primitive = primitive;
    }
}