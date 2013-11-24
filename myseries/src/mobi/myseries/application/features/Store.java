package mobi.myseries.application.features;

import java.util.List;

public interface Store<P extends Product<I>, I extends ProductId> {
    public List<P> ownedProducts();
    public List<P> productsAvailableForPurchase();

    // TODO(Gabriel): It should enforce the right type of ProductId 
    public void buy(ProductId productId);
}
