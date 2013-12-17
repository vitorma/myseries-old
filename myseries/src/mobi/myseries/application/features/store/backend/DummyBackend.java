package mobi.myseries.application.features.store.backend;

import java.util.HashMap;
import java.util.Set;

import mobi.myseries.application.Log;
import mobi.myseries.application.features.product.Availability;
import mobi.myseries.application.features.product.Sku;
import mobi.myseries.application.features.store.PurchaseListener;
import mobi.myseries.shared.Validate;
import android.app.Activity;

public class DummyBackend implements StoreBackend {


    @Override
    public void availableProductsFrom(Set<Sku> availableSkus, AvailabilityResultListener listener) {
        if (listener != null) {
            listener.onSuccess(new HashMap<Sku, Availability>());
        }
    }

    @Override
    public void buy(Sku sku, Activity activity, PurchaseListener purchaseListener) {
        Validate.isNonNull(sku, "sku");
        Validate.isNonNull(activity, "activity");

        Log.d(getClass().getCanonicalName(), "DummyStore: buying " + sku);

        if (purchaseListener != null) {
            purchaseListener.onSuccess(sku);
        }
    }
}
