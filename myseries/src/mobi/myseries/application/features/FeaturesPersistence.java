package mobi.myseries.application.features;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Base64;

import mobi.myseries.shared.Validate;

public class FeaturesPersistence {

    public static interface PersistenceBackend {
        public void saveState(State newState);
        public State retrieveState();
    }
    public static class State {
        public int nonce;
        public String base64Signature;
        public List<Feature> features; 
    }

    private static SecureRandom randomNumberGenerator = new SecureRandom();

    private static String deviceSalt() {
        // http://android-developers.blogspot.com.br/2011/03/identifying-app-installations.html
        return android.provider.Settings.Secure.ANDROID_ID;
    }

    private final PersistenceBackend persistenceBackend;

    public FeaturesPersistence(PersistenceBackend persistenceBackend) {
        Validate.isNonNull(persistenceBackend, "persistenceBackend");
        this.persistenceBackend = persistenceBackend;
    }

    public void save(Set<Feature> featuresToSave) {
        State newState = new State();

        newState.features = new ArrayList<Feature>(featuresToSave);
        newState.nonce = randomNumberGenerator.nextInt();
        newState.base64Signature = this.generateBase64SignatureFor(newState.nonce, newState.features);

        this.persistenceBackend.saveState(newState);
    }

    public Set<Feature> load() {
        State savedState = this.persistenceBackend.retrieveState();

        if (savedState != null
                && savedState.features != null
                && savedState.base64Signature != null
                && this.verifyBase64SignatureFor(savedState.nonce, savedState.features, savedState.base64Signature)) {
            return Collections.unmodifiableSet(new HashSet<Feature>(savedState.features));
        } else {
            return Collections.unmodifiableSet(new HashSet<Feature>());
        }
    }

    private String generateBase64SignatureFor(int nonce, List<Feature> features) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] nonceBytes = Integer.toString(nonce).getBytes();
        byte[] saltBytes = deviceSalt().getBytes();
        byte[] featuresBytes = features.toString().getBytes();

        digest.update(nonceBytes);
        digest.update(saltBytes);
        digest.update(featuresBytes);

        return Base64.encodeToString(digest.digest(), Base64.DEFAULT);
    }

    private boolean verifyBase64SignatureFor(int nonce, List<Feature> features, String signature) {
        return generateBase64SignatureFor(nonce, features).equals(signature);
    }
}
