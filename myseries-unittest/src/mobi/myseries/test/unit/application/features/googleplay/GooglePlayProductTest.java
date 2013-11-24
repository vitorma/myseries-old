package mobi.myseries.test.unit.application.features.googleplay;

import mobi.myseries.application.features.googleplay.GooglePlayProduct;
import mobi.myseries.application.features.googleplay.GooglePlayProductId;
import mobi.myseries.test.unit.application.features.ProductTest;

public class GooglePlayProductTest extends ProductTest<GooglePlayProduct, GooglePlayProductId> {

    @Override
    protected GooglePlayProduct newProductWithId(GooglePlayProductId id) {
        return new GooglePlayProduct(id);
    }

    public void testItCannotBeInstatiatedWithNullId() {
        try {
            new GooglePlayProduct(null);
            fail("Should have thrown an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    @Override
    protected GooglePlayProductId newProductId1() {
        return new GooglePlayProductId("PRODUCT1");
    }

    @Override
    protected GooglePlayProductId newProductId2() {
        return new GooglePlayProductId("PRODUCT2");
    }
}
