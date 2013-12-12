package mobi.myseries.application.features;

import mobi.myseries.application.features.product.Sku;

public interface PurchaseListener extends FailureListener {
    public void onSuccess(Sku product);
}
