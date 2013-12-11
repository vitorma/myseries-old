package mobi.myseries.application.features.backend.googleplay;

import mobi.myseries.application.features.FailureListener;

public interface SuperHelperListener extends FailureListener {
    public void onProductsChanged();
}