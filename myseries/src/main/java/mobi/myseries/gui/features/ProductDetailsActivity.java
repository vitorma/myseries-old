package mobi.myseries.gui.features;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

public class ProductDetailsActivity extends Activity {
    private final static String SKU_KEY = "sku";

    public static Intent newIntent(Context context, Sku productSku) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra(SKU_KEY, productSku);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.features_product_details);

        setUpViews();
    }

    private void setUpViews() {
        ViewPager picturesPager = (ViewPager) findViewById(R.id.picturesPager);
        TextView noPicturesMessage = (TextView) findViewById(R.id.no_screenshots_message);

        Sku sku = (Sku) getIntent().getExtras().getSerializable(SKU_KEY);
        ProductDescription productDescription = App.store().productDescriptionFor(sku);

        if (productDescription.picturesResourceIds().isEmpty()) {
            picturesPager.setVisibility(View.GONE);
            noPicturesMessage.setVisibility(View.VISIBLE);
        } else {
            picturesPager.setVisibility(View.VISIBLE);
            noPicturesMessage.setVisibility(View.GONE);

            picturesPager.setAdapter(new ProductDetailsItemAdapter(productDescription.picturesResourceIds()));
        }
    }
}
