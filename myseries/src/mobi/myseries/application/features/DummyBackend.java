package mobi.myseries.application.features;

import mobi.myseries.application.Log;
import mobi.myseries.shared.Validate;
import android.app.Activity;

public class DummyBackend implements StoreBackend {

    @Override
    public void buy(Sku sku, Activity activity) {
        Validate.isNonNull(sku, "sku");
        Validate.isNonNull(activity, "activity");

        Log.d(getClass().getCanonicalName(), "DummyStore: buying " + sku);

        // TODO Auto-generated method stub
    }
}
