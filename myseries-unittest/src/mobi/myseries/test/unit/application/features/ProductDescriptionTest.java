package mobi.myseries.test.unit.application.features;

import mobi.myseries.application.features.ProductDescription;
import mobi.myseries.application.features.Sku;

import android.test.AndroidTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class ProductDescriptionTest extends AndroidTestCase {

    private static final Sku sku1 = new Sku("PRODUCT1");
    private static final Sku sku2 = new Sku("PRODUCT2");

    private static ProductDescription newProductWithSku(Sku sku) {
        return new ProductDescription(sku);
    }

    /* Test assumptions */

    public void testProductSkusAreDifferent() {
        assertThat(sku1, not(equalTo(sku2)));
    }

    /* Constructor */

    public void testItCannotBeInstatiatedWithNullSku() {
        try {
            new ProductDescription(null);
            fail("Should have thrown an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /* Entity tests*/

    public void testTwoProductsAreEqualIfTheyHaveTheSameSku() {
        Sku sku = sku1;

        ProductDescription p1 = newProductWithSku(sku);
        ProductDescription p2 = newProductWithSku(sku);

        assertThat(p1, equalTo(p2));
        assertThat(p2, equalTo(p1));
    }

    public void testTwoProductsAreDifferentIfTheyHaveDifferentSkus() {
        ProductDescription p1 = newProductWithSku(sku1);
        ProductDescription p2 = newProductWithSku(sku2);

        assertThat(p1, not(equalTo(p2)));
        assertThat(p2, not(equalTo(p1)));
    }

    public void testNoProductIsEqualToNull() {
        Sku sku = sku1;
        ProductDescription product = newProductWithSku(sku);

        assertThat(product, not(equalTo(null)));
    }

    public void testTheProductsNameMustNeverBeNull() {
        Sku sku = sku1;
        ProductDescription product = newProductWithSku(sku);

        assertThat(product.name(), not(nullValue()));
    }

    public void testTheProductsFeaturesMustNeverBeNull() {
        Sku sku = sku1;
        ProductDescription product = newProductWithSku(sku);

        assertThat(product.features(), not(nullValue()));
    }
}
