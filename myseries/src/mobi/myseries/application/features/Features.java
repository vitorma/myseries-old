package mobi.myseries.application.features;

public class Features {

    public boolean isVisible(Feature feature) {
        // XXX
        return feature == Feature.CLOUD_BACKUP ||
               feature == Feature.FEATURE_SHOP;
        //return false;
    }

    public boolean isEnabled(Feature feature) {
        //XXX
        return false;
    }

    public boolean isAvailableForPurchase(Feature feature) {
        //XXX
        return false;
    }
}
