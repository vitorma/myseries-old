package mobi.myseries.application.features;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;

import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;
import mobi.myseries.application.activityevents.ActivityEventsService;
import mobi.myseries.application.features.googleplay.GooglePlayStore;
import mobi.myseries.shared.Validate;

public class Store extends ApplicationService<StoreListener> {

    private static final Set<ProductDescription> implementedProducts =
            Collections.unmodifiableSet(new HashSet<ProductDescription>(Arrays.asList(
                    new ProductDescription(new Sku("android.test.purchased")),
                    new ProductDescription(new Sku("android.test.canceled")),
                    new ProductDescription(new Sku("android.test.refunded")),
                    new ProductDescription(new Sku("android.test.item_unavailable")))));

    private static final Set<Sku> implementedProductsSkus;
    static {
        Set<Sku> implementedSkus = new HashSet<Sku>();
        for (ProductDescription p : implementedProducts) {
            implementedSkus.add(p.sku());
        }
        implementedProductsSkus = Collections.unmodifiableSet(implementedSkus);
    }

    private static final Set<Product> productsWithoutPrice;
    static {
        Set<Product> products = new HashSet<Product>();
        for (ProductDescription p : implementedProducts) {
            products.add(new Product(Price.NotAvailable, p));
        }
        productsWithoutPrice = Collections.unmodifiableSet(products);
    }

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

        // XXX
        //this.backend.loadProducts(implementedProductsSkus);
    }

    public Set<Product> productsWithoutAvilabilityInformation() {
        return productsWithoutPrice;
    }

    public void productsAvailableForPurchase(final AvailableProductsResultListener listener) {
        //return Collections.unmodifiableSet(this.availableProducts);

        /**/ //TODO
        final Set<Product> products = new HashSet<Product>(Arrays.asList(
                new Product(new Price("$1.00"), new ProductDescription(new Sku("android.test.purchased"))),
                new Product(new Price("$2.00"), new ProductDescription(new Sku("android.test.canceled"))),
                new Product(new Price("$3.00"), new ProductDescription(new Sku("android.test.refunded"))),
                new Product(new Price("$4.00"), new ProductDescription(new Sku("android.test.item_unavailable")))));

        run(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        });
        // */
    }
    public static interface AvailableProductsResultListener {
        public void onSuccess(Set<Product> products);
        public void onFailure(Exception e);
    }

    public Set<Product> ownedProducts() {
        //XXX
        return new HashSet<Product>();
    }

    public void buy(Product product, Activity activity) {
        // XXX
        this.backend.buy(product.sku(), activity);
    }
}
