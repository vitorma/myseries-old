package mobi.myseries.application.features;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;

import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;
import mobi.myseries.application.Log;
import mobi.myseries.application.activityevents.ActivityEventsService;
import mobi.myseries.application.features.backend.DummyBackend;
import mobi.myseries.application.features.backend.StoreBackend;
import mobi.myseries.application.features.backend.googleplay.GooglePlayStore;
import mobi.myseries.application.features.product.Availability;
import mobi.myseries.application.features.product.Product;
import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;
import mobi.myseries.shared.Validate;

public class Store extends ApplicationService<StoreListener> {

    private final ProductCatalog productCatalog;

    private volatile StoreBackend backend;

    public Store(Environment environment, ActivityEventsService activityEventsService) {
        super(environment);
        Validate.isNonNull(activityEventsService, "activityEventsService");

        StoreBackend backend;
        if (GooglePlayStore.isGooglePlayInstalled(environment.context())) {
            backend = new GooglePlayStore(environment.context(), activityEventsService);
        } else {
            backend = new DummyBackend();
        }
        this.backend = backend;

        // XXX(Gabriel): Implement and use a production products catalog.
        this.productCatalog = new TestProductsCatalog();

        // XXX
        //this.backend.loadProducts(implementedProductsSkus);
    }

    public Set<Product> productsWithoutAvilabilityInformation() {
        return this.productCatalog.productsWithoutPrice();
    }

    public void productsAvailableForPurchase(final AvailableProductsResultListener listener) {

        this.backend.availableProductsFrom(
                this.productCatalog.implementedProductsSkus(),
                new StoreBackend.AvailabilityResultListener() {

            @Override
            public void onSuccess(Map<Sku, Availability> availabilities) {
                final Set<Product> products = new HashSet<Product>(productCatalog.implementedProducts().size());

                for (ProductDescription d : productCatalog.implementedProducts()) {
                    Availability a = availabilities.get(d.sku());

                    if (a != null) {
                        Log.d(getClass().getCanonicalName(), "Created product with availability: " + d.sku());
                        products.add(new Product(a.price(), d));
                    } else {
                        Log.d(getClass().getCanonicalName(), "Availability not found for: " + d.sku());
                    }
                }

                if (listener != null) {
                    runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(products);
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
                // TODO Auto-generated method stub

                if (listener != null) {
                    runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailure();
                        }
                    });
                }
            }
        });
    }
    public static interface AvailableProductsResultListener {
        public void onSuccess(Set<Product> products);
        public void onFailure(); // TODO(Gabriel) handle exceptions
    }

    /* TODO?
    public Set<Product> ownedProducts() {
        //XXX
        return new HashSet<Product>();
    }
    */

    public void buy(Product product, Activity activity) {
        // XXX
        this.backend.buy(product.sku(), activity);
    }
}
