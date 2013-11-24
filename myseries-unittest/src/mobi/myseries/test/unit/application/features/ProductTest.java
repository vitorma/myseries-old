package mobi.myseries.test.unit.application.features;

import mobi.myseries.application.features.Product;
import mobi.myseries.application.features.ProductId;
import android.test.AndroidTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class ProductTest<P extends Product<I>, I extends ProductId> extends AndroidTestCase {

    protected abstract P newProductWithId(I id);

    protected abstract I newProductId1();
    protected abstract I newProductId2();
    
    /* Test assumptions */

    public void testProductIdsAreDifferent() {
        assertThat(newProductId1(), not(equalTo(newProductId2())));
    }

    public void testProductIdsAreIdempotent() {
        assertThat(newProductId1(), equalTo(newProductId1()));
        assertThat(newProductId2(), equalTo(newProductId2()));
    }

    /* Entity tests*/

    public void testTwoProductsAreEqualIfTheyHaveTheSameId() {
        I id = newProductId1();

        P p1 = newProductWithId(id);
        P p2 = newProductWithId(id);

        assertThat(p1, equalTo(p2));
        assertThat(p2, equalTo(p1));
    }

    public void testTwoProductsAreDifferentIfTheyHaveDifferentIds() {
        I id1 = newProductId1();
        I id2 = newProductId2();

        P p1 = newProductWithId(id1);
        P p2 = newProductWithId(id2);

        assertThat(p1, not(equalTo(p2)));
        assertThat(p2, not(equalTo(p1)));
    }

    public void testNoProductIsEqualToNull() {
        I id = newProductId1();
        P product = newProductWithId(id);

        assertThat(product, not(equalTo(null)));
    }
}
