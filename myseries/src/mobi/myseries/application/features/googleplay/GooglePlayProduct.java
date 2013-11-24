package mobi.myseries.application.features.googleplay;

import mobi.myseries.application.features.Product;
import mobi.myseries.shared.Validate;

public class GooglePlayProduct extends Product<GooglePlayProductId> {

    private GooglePlayProductId id;

    public GooglePlayProduct(GooglePlayProductId id) {
        Validate.isNonNull(id, "id");
        this.id = id;
    }

    @Override
    public GooglePlayProductId id() {
        return this.id;
    }

    @Override
    public String name() {
        // TODO Auto-generated method stub
        //return null;
        return "GooglePlayProduct(" + this.id() + ")";
    }

    @Override
    public String price() {
        // TODO Auto-generated method stub
        //return null;
        return "US$ 2.00";
    }

}
