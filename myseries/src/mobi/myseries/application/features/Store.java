package mobi.myseries.application.features;

import java.util.Set;

import android.app.Activity;

import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;

public abstract class Store extends ApplicationService<StoreListener> {

    public Store(Environment environment) {
        super(environment);
    }

    public abstract Set<Product> ownedProducts();
    public abstract Set<Product> productsAvailableForPurchase();

    public abstract void buy(Sku sku, Activity activity);
}
