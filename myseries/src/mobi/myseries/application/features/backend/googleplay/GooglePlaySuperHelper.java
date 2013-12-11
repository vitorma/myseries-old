/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobi.myseries.application.features.backend.googleplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import mobi.myseries.application.activityevents.ActivityEventsListener;
import mobi.myseries.application.features.FailureListener;
import mobi.myseries.application.features.PurchaseListener;
import mobi.myseries.application.features.backend.RemoteStoreApiException;
import mobi.myseries.application.features.backend.RemoteStoreApiNotAvailableException;
import mobi.myseries.application.features.backend.googleplay.util.IabHelper;
import mobi.myseries.application.features.backend.googleplay.util.IabResult;
import mobi.myseries.application.features.backend.googleplay.util.Inventory;
import mobi.myseries.application.features.backend.googleplay.util.Purchase;
import mobi.myseries.application.features.product.Price;
import mobi.myseries.application.features.product.Sku;
import mobi.myseries.shared.Validate;

/**
 * Example game using in-app billing version 3.
 *
 * Before attempting to run this sample, please read the README file. It
 * contains important information on how to set up this project.
 *
 * All the game-specific logic is implemented here in MainActivity, while the
 * general-purpose boilerplate that can be reused in any app is provided in the
 * classes in the util/ subdirectory. When implementing your own application,
 * you can copy over util/*.java to make use of those utility classes.
 *
 * This game is a simple "driving" game where the player can buy gas
 * and drive. The car has a tank which stores gas. When the player purchases
 * gas, the tank fills up (1/4 tank at a time). When the player drives, the gas
 * in the tank diminishes (also 1/4 tank at a time).
 *
 * The user can also purchase a "premium upgrade" that gives them a red car
 * instead of the standard blue one (exciting!).
 *
 * The user can also purchase a subscription ("infinite gas") that allows them
 * to drive without using up any gas while that subscription is active.
 *
 * It's important to note the consumption mechanics for each item.
 *
 * PREMIUM: the item is purchased and NEVER consumed. So, after the original
 * purchase, the player will always own that item. The application knows to
 * display the red car instead of the blue one because it queries whether
 * the premium "item" is owned or not.
 *
 * INFINITE GAS: this is a subscription, and subscriptions can't be consumed.
 *
 * GAS: when gas is purchased, the "gas" item is then owned. We consume it
 * when we apply that item's effects to our app's world, which to us means
 * filling up 1/4 of the tank. This happens immediately after purchase!
 * It's at this point (and not when the user drives) that the "gas"
 * item is CONSUMED. Consumption should always happen when your game
 * world was safely updated to apply the effect of the purchase. So,
 * in an example scenario:
 *
 * BEFORE:      tank at 1/2
 * ON PURCHASE: tank at 1/2, "gas" item is owned
 * IMMEDIATELY: "gas" is consumed, tank goes to 3/4
 * AFTER:       tank at 3/4, "gas" item NOT owned any more
 *
 * Another important point to notice is that it may so happen that
 * the application crashed (or anything else happened) after the user
 * purchased the "gas" item, but before it was consumed. That's why,
 * on startup, we check if we own the "gas" item, and, if so,
 * we have to apply its effects to our world and consume it. This
 * is also very important!
 *
 * @author Bruno Oliveira (Google)
 */
public class GooglePlaySuperHelper implements ActivityEventsListener {

    private final Context context;
    private final boolean debugMode = true;

    // Debug tag, for logging
    private final String TAG = getClass().getCanonicalName();

    public static class Products {
        public final Set<Sku> ownedSkus = new HashSet<Sku>();
        public final Map<Sku, Price> skusInStore = new HashMap<Sku, Price>();
    }

    private final String base64PublicKey;

    // (arbitrary) request code for the purchase flow
    private static final int RC_REQUEST = 3;

    // The helper object
    private IabHelper mHelper;

    public GooglePlaySuperHelper(Context context, String base64PublicKey) {
        Validate.isNonNull(context, "context");
        Validate.isNonNull(base64PublicKey, "base64PublicKey");

        this.context = context;

        this.base64PublicKey = base64PublicKey;
    }

    private Sku skuFromPurchase(Purchase purchase) {
        Validate.isNonNull(purchase, "purchase");
        return new Sku(purchase.getSku());
    }

    private List<String> skuValuesFrom(Collection<Sku> skus) {
        List<String> strSkus = new ArrayList<String>(skus.size());
        for (Sku s : skus) {
            strSkus.add(s.value());
        }
        return strSkus;
    }

    public void buy(Sku product, Activity activity, PurchaseListener listener) {
        this.setUp(thenStartPurchasing(product, activity, listener), listener);
    }

    public void loadProducts(Set<Sku> implementedProducts, LoadProductsListener listener) {
        this.setUp(thenLoadProducts(implementedProducts,listener, thenDestroy()), listener);
    }
    public static interface LoadProductsListener extends FailureListener {
        public void onSuccess(Products products);
    }

    private Runnable thenLoadProducts(final Set<Sku> implementedProducts, final LoadProductsListener listener, final Runnable nextAction) {

        return new Runnable() {
            @Override
            public void run() {
                mHelper.queryInventoryAsync(true, skuValuesFrom(implementedProducts), new GotInventoryListener(implementedProducts, listener, nextAction));
            }
        };
    }

    private Runnable thenDestroy() {
        return new Runnable() {
            @Override
            public void run() {
                onDestroy();
            }
        };
    }

    private Runnable thenStartPurchasing(final Sku product, final Activity activity, final PurchaseListener listener) {
        return new Runnable() {
            @Override
            public void run() {
                startPurchasing(product, activity, listener);
            }
        };
    }

    /* The code below is based upon the main activity of the sample application */

    private void setUp(final Runnable nextAction, final FailureListener failureListener) {
        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = this.base64PublicKey; //XXX "CONSTRUCT_YOUR_KEY_AND_PLACE_IT_HERE";

        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this.context, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(this.debugMode);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new SetupFinishedListener(nextAction, failureListener));
    }

    private class SetupFinishedListener implements IabHelper.OnIabSetupFinishedListener {
        private final Runnable nextAction;
        private FailureListener failureListener;

        public SetupFinishedListener(Runnable nextAction, FailureListener failureListener) {
            this.nextAction = nextAction;
            this.failureListener = failureListener;
        }

        @Override
        public void onIabSetupFinished(IabResult result) {
            try {
                doItAndThrowExceptionOnFailure(result);

                if (nextAction != null) {
                    nextAction.run();
                }
            } catch (Throwable t) {
                onDestroy();

                if (failureListener != null) {
                    failureListener.onFailure(t);
                }
            }
        }

        private void doItAndThrowExceptionOnFailure(IabResult result)
                throws RemoteStoreApiException, RemoteStoreApiNotAvailableException {
            Log.d(TAG, "Setup finished.");

            if (!result.isSuccess()) {
                // Oh noes, there was a problem.
                throw complain("Problem setting up in-app billing: " + result);
            }

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) throw new RemoteStoreApiNotAvailableException();

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(TAG, "Setup successful.");
        }
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    private class GotInventoryListener implements IabHelper.QueryInventoryFinishedListener {
        private final Runnable nextAction;
        private final Set<Sku> implementedProducts;
        private final LoadProductsListener listener;

        public GotInventoryListener(Set<Sku> implementedProducts, LoadProductsListener listener, Runnable nextAction) {
            this.nextAction = nextAction;
            this.implementedProducts = implementedProducts;
            this.listener = listener;
        }

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            try {
                doItAndThrowExceptionOnFailure(result, inventory);

                if (nextAction != null) {
                    nextAction.run();
                }
            } catch (Throwable t) {
                onDestroy();

                if (listener != null) {
                    listener.onFailure(t);
                }
            }
        }

        public void doItAndThrowExceptionOnFailure(IabResult result, Inventory inventory)
                throws RemoteStoreApiException, RemoteStoreApiNotAvailableException {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) throw new RemoteStoreApiNotAvailableException();

            // Is it a failure?
            if (result.isFailure()) {
                throw complain("Failed to query inventory: " + result);
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            Products products = new Products();

            for (Sku product : implementedProducts) {
                String sku = product.value();

                if (inventory.hasDetails(sku)) {
                    Price price = new Price(inventory.getSkuDetails(sku).getPrice());

                    Log.d(TAG, product + " is available");
                    products.skusInStore.put(product, price);
                } else {
                    Log.d(TAG, product + " is not available");
                }

                if (inventory.hasPurchase(sku)) {
                    Log.d(TAG, "Purchase found for " + sku + ". Checking its developer payload.");

                    if (verifyDeveloperPayload(inventory.getPurchase(sku))) {
                        Log.d(TAG, "User owns " + product);
                        products.ownedSkus.add(product);
                    } else {
                        Log.d(TAG, "User doesn't own " + product);
                    }
                }
            }

            if (this.listener != null) {
                this.listener.onSuccess(products);
            }

            Log.d(TAG, "Inventory query finished.");
            updateUi();
        }
    }

    private void startPurchasing(Sku product, Activity activity, PurchaseListener listener) {

        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");

        /* for security, generate your payload here for verification. See the comments on
         * verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         * an empty string, but on a production app you should carefully generate this. */
        String payload = this.calculateDeveloperPayload();

        mHelper.launchPurchaseFlow(activity, product.value(), RC_REQUEST,
                new PurchaseFinishedListener(listener), payload);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            //super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    // Callback for when a purchase is finished
    private class PurchaseFinishedListener implements IabHelper.OnIabPurchaseFinishedListener {
        private final PurchaseListener listener;

        public PurchaseFinishedListener(PurchaseListener listener) {
            this.listener = listener;
        }

        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            try {
                this.doItAndThrowExceptionOnFailure(result, purchase);
            } catch (Throwable t) {
                listener.onFailure(t);
            } finally {
                onDestroy();
            }
        }

        public void doItAndThrowExceptionOnFailure(IabResult result, Purchase purchase)
                throws RemoteStoreApiNotAvailableException, RemoteStoreApiException {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) throw new RemoteStoreApiNotAvailableException();

            if (result.isFailure()) {
                throw complain("Error purchasing: " + result);
            }

            if (!verifyDeveloperPayload(purchase)) {
                throw complain("Error purchasing. Authenticity verification failed.");
            } else {
                Log.d(TAG, "Purchase successful.");

                Sku product = skuFromPurchase(purchase);

                Log.d(TAG, "Purchase is " + product + ".");
                listener.onSuccess(product);
                updateUi();
            }
        }
    };

    // We're being destroyed. It's important to dispose of the helper here!
    private void onDestroy() {
        //super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    // updates UI to reflect model
    private void updateUi() {
        // XXX
    }

    private RemoteStoreApiException complain(String message) {
        Log.e(TAG, "**** IAB Error: " + message);
        return new RemoteStoreApiException(message);
    }



    private String calculateDeveloperPayload() {
        // TODO(Gabriel): It should be implemented in a way to avoid replay attacks and link
        // the product to the user instead of the device.
        return "";
    }

    /** Verifies the developer payload of a purchase. */
    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }
}
