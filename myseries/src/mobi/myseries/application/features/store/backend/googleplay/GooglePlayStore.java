package mobi.myseries.application.features.store.backend.googleplay;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.Log;
import mobi.myseries.application.activityevents.ActivityEventsService;
import mobi.myseries.application.features.product.Availability;
import mobi.myseries.application.features.product.Price;
import mobi.myseries.application.features.product.Sku;
import mobi.myseries.application.features.store.PurchaseListener;
import mobi.myseries.application.features.store.backend.StoreBackend;
import mobi.myseries.application.features.store.backend.googleplay.GooglePlaySuperHelper.Products;
import mobi.myseries.shared.Validate;

public class GooglePlayStore implements StoreBackend {

    /**
     * Copied and pasted from:
     * http://stackoverflow.com/questions/10551531/cannot-determine-whether-google-play-store-is-installed-or-not-on-android-device
     * http://stackoverflow.com/questions/14499019/how-to-check-that-device-has-play-store-and-user-has-signed-in-using-their-goog
     */
    public static boolean isGooglePlayInstalled(Context context) {
        String playPackage = "com.android.vending";
        return isPackageInstalled(playPackage, context);
    }

    private static boolean isPackageInstalled(String packageName, Context context) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private static String base64PublicKey() {
        Resources res = App.context().getResources();
        return res.getString(R.string.googleInAppBilling_key_part_0) +
               res.getString(R.string.googleInAppBilling_key_part_1) +
               res.getString(R.string.googleInAppBilling_key_part_2) +
               res.getString(R.string.googleInAppBilling_key_part_3) +
               res.getString(R.string.googleInAppBilling_key_part_4) +
               res.getString(R.string.googleInAppBilling_key_part_5) +
               res.getString(R.string.googleInAppBilling_key_part_6) +
               res.getString(R.string.googleInAppBilling_key_part_7) +
               res.getString(R.string.googleInAppBilling_key_part_8) +
               res.getString(R.string.googleInAppBilling_key_part_9);
    }

    private final GooglePlaySuperHelper helper;

    public GooglePlayStore(Context context, ActivityEventsService activityEventsService) {
        Validate.isNonNull(context, "context");
        Validate.isNonNull(activityEventsService, "activityEventsService");

        this.helper = new GooglePlaySuperHelper(context, base64PublicKey());

        activityEventsService.register(this.helper);
    }

    @Override
    public void availableProductsFrom(final Set<Sku> availableSkus, final AvailabilityResultListener listener) {
        // XXX
        this.helper.loadProducts(availableSkus, new GooglePlaySuperHelper.LoadProductsListener() {
            @Override
            public void onSuccess(Products products) {
                Map<Sku, Availability> availabilities = new HashMap<Sku, Availability>(products.skusInStore.size());

                for (Sku s : availableSkus) {
                    if (products.skusInStore.containsKey(s)) {
                        boolean isOwned = products.ownedSkus.contains(s);
                        Price price = products.skusInStore.get(s);

                        availabilities.put(s, new Availability(price, isOwned));

                        Log.d(getClass().getCanonicalName(), "Created availability for " + s);
                    }
                }

                listener.onSuccess(availabilities);
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO Auto-generated method stub
                listener.onFailure();
            }
        });
    }

    // XXX
    @Override
    public void buy(Sku sku, Activity activity, PurchaseListener purchaseListener) {
        // TODO set up purchase listener to notify store listeners when a purchase is complete.
        // Should the listener be defined here or in Store?
        Log.d(getClass().getCanonicalName(), "GooglePlayStore: buying " + sku);
        this.helper.buy(sku, activity, purchaseListener);
    }
}
