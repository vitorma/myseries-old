package mobi.myseries.application.features.googleplay;

import java.util.Arrays;
import java.util.List;

import mobi.myseries.application.Log;
import mobi.myseries.application.features.ProductId;
import mobi.myseries.application.features.Store;

public class GooglePlayStore implements Store<GooglePlayProduct, GooglePlayProductId> {

    @Override
    public List<GooglePlayProduct> ownedProducts() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GooglePlayProduct> productsAvailableForPurchase() {
        // TODO Auto-generated method stub
        //return null;
        return Arrays.asList(new GooglePlayProduct(new GooglePlayProductId("1")), new GooglePlayProduct(new GooglePlayProductId("2")), new GooglePlayProduct(new GooglePlayProductId("3")));
    }

    @Override
    public void buy(ProductId productId) {
        // TODO Auto-generated method stub
        Log.d(getClass().getCanonicalName(), "GooglePlayStore: buying " + productId);
    }

    // XXX
    public void buy(GooglePlayProductId productId) {
        // TODO Auto-generated method stub
        
    }

}
