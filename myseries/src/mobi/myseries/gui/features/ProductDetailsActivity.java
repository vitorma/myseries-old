package mobi.myseries.gui.features;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.Log;
import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;
import mobi.myseries.shared.Validate;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

public class ProductDetailsActivity extends Activity {

    private final static String SKU_KEY = "sku";

    public static Intent newInstance(Context context, Sku productSku) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra(SKU_KEY, productSku);

        return intent;
    }

    private ProductDescription mProductDescription;
    private ProductDetailsItemAdapter currentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Sku productSku = (Sku) intent.getExtras().getSerializable(SKU_KEY);
        Validate.isNonNull(productSku, "productSku");

        mProductDescription = App.store().productDescriptionFor(productSku);
        Validate.isNonNull(mProductDescription, "mProductDescription");

        Log.d(getClass().getCanonicalName(), "onCreateDialog: mProductDescription = " + mProductDescription);

        setUpViewFor(mProductDescription);
    }

    private void setUpViewFor(ProductDescription productDescription) {
        this.setContentView(R.layout.features_product_details);
        this.getActionBar().hide();

        TextView noPicturesMessage = (TextView) this.findViewById(R.id.no_screenshots_message);
        Validate.isNonNull(noPicturesMessage, "noPicturesMessage");

        ViewPager picturesPager = (ViewPager) this.findViewById(R.id.picturesPager);
        Validate.isNonNull(picturesPager, "picturesPager");

        this.currentAdapter = new ProductDetailsItemAdapter(productDescription.picturesResourceIds());
        picturesPager.setAdapter(currentAdapter);

        // Show the empty view if needed
        if (productDescription.picturesResourceIds().isEmpty()) {
            picturesPager.setVisibility(View.INVISIBLE);
            noPicturesMessage.setVisibility(View.VISIBLE);
        } else {
            picturesPager.setVisibility(View.VISIBLE);
            noPicturesMessage.setVisibility(View.GONE);
        }
    }
}
