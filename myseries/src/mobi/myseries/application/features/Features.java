package mobi.myseries.application.features;

public class Features {

    public boolean isVisible(Feature feature) {
        // XXX
        if(feature == Feature.CLOUD_BACKUP)
            return true;
        //return feature == Feature.BACKUP;
        //return feature == Feature.FEATURE_SHOP;
        return false;
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
