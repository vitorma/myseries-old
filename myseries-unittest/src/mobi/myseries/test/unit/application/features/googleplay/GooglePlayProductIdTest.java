package mobi.myseries.test.unit.application.features.googleplay;

import mobi.myseries.application.features.googleplay.GooglePlayProductId;
import mobi.myseries.test.unit.application.features.ProductIdTest;

public class GooglePlayProductIdTest extends ProductIdTest<GooglePlayProductId> {

    private static final GooglePlayProductId productA() {
        return new GooglePlayProductId("Product A");
    }

    private static final GooglePlayProductId productB() {
        return new GooglePlayProductId("Product B");
    }

    public void testItCannotBeInstantiatedWithANullString() {
        try {
            new GooglePlayProductId(null);
            fail("Should have thrown and IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @Override
    protected GooglePlayProductId newProductA1() {
        return productA();
    }

    @Override
    protected GooglePlayProductId newProductA2() {
        return productA();
    }

    @Override
    protected GooglePlayProductId newProductA3() {
        return productA();
    }

    @Override
    protected GooglePlayProductId newProductB() {
        return productB();
    }
}
