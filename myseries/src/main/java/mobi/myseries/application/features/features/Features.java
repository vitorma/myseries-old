package mobi.myseries.application.features.features;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mobi.myseries.application.Log;
import mobi.myseries.application.features.product.Feature;
import mobi.myseries.application.features.product.Product;
import mobi.myseries.application.features.store.Store;
import mobi.myseries.application.features.store.StoreListener;
import mobi.myseries.application.preferences.Preferences;
import mobi.myseries.shared.Validate;

public class Features {

    private final Store store;
    private final StoreListener storeListener = new StoreListener() {
        @Override
        public void onProductsChanged() {
            queryStoreForEnabledFeatures();
        }
    };

    private final FeaturesPersistence persistence;

    private volatile Set<Feature> enabledFeatures;

    public Features(Store store, Preferences preferences) {
        Validate.isNonNull(store, "store");
        Validate.isNonNull(preferences, "preferences");

        this.store = store;
        this.store.register(this.storeListener);

        this.persistence = new FeaturesPersistence(new SharedPreferencesFeaturesPersistenceBackend(preferences.forFeatures()));

        this.enabledFeatures = this.persistence.load();
        Log.d(getClass().getCanonicalName(), "Loaded features: " + this.enabledFeatures);
        this.queryStoreForEnabledFeatures();
    }

    public boolean isVisible(Feature feature) {
        return feature == Feature.CLOUD_BACKUP ||
               feature == Feature.FEATURE_SHOP;
    }

    public boolean isEnabled(Feature feature) {
        return this.enabledFeatures.contains(feature);
    }

    private void queryStoreForEnabledFeatures() {
        this.store.productsWithAvailabilityInformation(new Store.AvailableProductsResultListener() {

            @Override
            public void onSuccess(Set<Product> products) {
                Set<Feature> newFeatures = availableFeaturesFromProducts(products);

                Log.d(getClass().getCanonicalName(), "Persisting new features");
                persistence.save(newFeatures);

                Log.d(getClass().getCanonicalName(), "Reloading enabled features from persistence");
                enabledFeatures = persistence.load();

                // TODO(Gabriel): notify that there are new features available
            }

            @Override
            public void onFailure() {
                // keep the current features.
            }
        });
    }

    private Set<Feature> availableFeaturesFromProducts(Collection<Product> products) {
        Set<Feature> availableFeatures = new HashSet<Feature>();

        for (Product p : products) {
            if (p.isOwned()) {
                availableFeatures.addAll(p.description().features());
            }
        }
        Log.d(getClass().getCanonicalName(), "New features available: " + availableFeatures);

        return Collections.unmodifiableSet(availableFeatures);
    }
}
