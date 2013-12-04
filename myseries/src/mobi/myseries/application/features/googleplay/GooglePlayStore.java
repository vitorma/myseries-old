package mobi.myseries.application.features.googleplay;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import mobi.myseries.application.Log;
import mobi.myseries.application.activityevents.ActivityEventsService;
import mobi.myseries.application.features.Product;
import mobi.myseries.application.features.Sku;
import mobi.myseries.application.features.StoreBackend;
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
        return "your public key here"; //"XXX TODO Put a public key here";
    }

    private final GooglePlaySuperHelper helper;

    private final Set<Product> ownedProducts = new HashSet<Product>();
    private final Set<Product> availableProducts = new HashSet<Product>();

    public GooglePlayStore(Context context, ActivityEventsService activityEventsService) {
        Validate.isNonNull(context, "context");
        Validate.isNonNull(activityEventsService, "activityEventsService");

        this.helper = new GooglePlaySuperHelper(context, base64PublicKey());
        this.helper.register(this.helperListener);

        activityEventsService.register(this.helper);
    }


    // XXX
    public Set<Product> ownedProducts() {
        synchronized (this.ownedProducts) {
            return Collections.unmodifiableSet(this.ownedProducts);
        }
    }

    // XXX
    @Override
    public void buy(Sku sku, Activity activity) {
        Log.d(getClass().getCanonicalName(), "GooglePlayStore: buying " + sku);
        //this.helper.buy(sku, activity);
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
