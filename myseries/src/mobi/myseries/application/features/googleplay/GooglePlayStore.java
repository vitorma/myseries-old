package mobi.myseries.application.features.googleplay;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;

import mobi.myseries.application.Environment;
import mobi.myseries.application.Log;
import mobi.myseries.application.activityevents.ActivityEventsService;
import mobi.myseries.application.features.Product;
import mobi.myseries.application.features.ProductDescription;
import mobi.myseries.application.features.Sku;
import mobi.myseries.application.features.Store;
import mobi.myseries.shared.Validate;

public class GooglePlayStore extends Store {

    private static final String base64PublicKey = "your public key here"; //"XXX TODO Put a public key here";

    private final GooglePlaySuperHelper helper;

    private final Set<Product> ownedProducts = new HashSet<Product>();
    private final Set<Product> availableProducts = new HashSet<Product>();

    // XXX(Gabriel): Move this to Store
    private static final Set<ProductDescription> implementedProducts =
            Collections.unmodifiableSet(
            new HashSet<ProductDescription>(
                    Arrays.asList(
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

    public GooglePlayStore(Environment environment, ActivityEventsService activityEventsService) {
        super(environment);
        Validate.isNonNull(activityEventsService, "activityEventsService");

        this.helper = new GooglePlaySuperHelper(
            this.environment().context(), implementedProductsSkus, base64PublicKey);

        this.helper.register(this.helperListener);

        activityEventsService.register(this.helper);
        this.helper.loadProducts();
    }


    @Override
    public Set<Product> ownedProducts() {
        synchronized (this.ownedProducts) {
            return Collections.unmodifiableSet(this.ownedProducts);
        }
    }

    @Override
    public Set<Product> productsAvailableForPurchase() {
        synchronized (this.availableProducts) {
            return Collections.unmodifiableSet(this.availableProducts);
        }

        /* TODO
        return new HashSet<Product>(
                Arrays.asList(
                        new Product(new Price("$1.00"), new ProductDescription(new Sku("android.test.purchased"))),
                        new Product(new Price("$2.00"), new ProductDescription(new Sku("android.test.canceled"))),
                        new Product(new Price("$3.00"), new ProductDescription(new Sku("android.test.refunded"))),
                        new Product(new Price("$4.00"), new ProductDescription(new Sku("android.test.item_unavailable")))));
        */
    }

    @Override
    public void buy(Sku sku, Activity activity) {
        Log.d(getClass().getCanonicalName(), "GooglePlayStore: buying " + sku);
        this.helper.buy(sku, activity);
    }


    private final SuperHelperListener helperListener = new SuperHelperListener() {
        @Override
        public void onProductsChanged() {
            // TODO Auto-generated method stub
            // reload products, notify StoreListeners
            Log.d(getClass().getCanonicalName(), "GooglePlayStore helperListener onProductsChanged called.");
        }

        @Override
        public void onError() {
            // TODO Auto-generated method stub
            // reload products, notify StoreListeners
            Log.d(getClass().getCanonicalName(), "GooglePlayStore helperListener onError called.");
        }
    };
}
