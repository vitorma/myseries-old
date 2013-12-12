package mobi.myseries.application.features.backend;

import java.util.Map;
import java.util.Set;

import mobi.myseries.application.features.product.Availability;
import mobi.myseries.application.features.product.Sku;

import android.app.Activity;

public interface StoreBackend {

    public void availableProductsFrom(Set<Sku> availableSkus, AvailabilityResultListener listener);

    public static interface AvailabilityResultListener {
        public void onSuccess(Map<Sku, Availability> availabilities);
        public void onFailure(); // TODO: deal with exceptions
    }

    public void buy(Sku sku, Activity activity);
}
