package mobi.myseries.gui.features;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.Log;
import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;
import mobi.myseries.shared.Validate;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class ProductDetailsDialogFragment extends DialogFragment {

    private final static String SKU_KEY = "sku";

    public static ProductDetailsDialogFragment newInstance(Sku productSku) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(SKU_KEY, productSku);

        ProductDetailsDialogFragment instance = new ProductDetailsDialogFragment();
        instance.setArguments(arguments);

        return instance;
    }

    private ProductDescription mProductDescription;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Sku productSku = (Sku) this.getArguments().getSerializable(SKU_KEY);
        Validate.isNonNull(productSku, "productSku");

        mProductDescription = App.store().productDescriptionFor(productSku);
        Validate.isNonNull(mProductDescription, "mProductDescription");

        Log.d(getClass().getCanonicalName(), "onCreateDialog: mProductDescription = " + mProductDescription);

        return createDialogFor(mProductDescription);
    }

    private Dialog createDialogFor(ProductDescription productDescription) {
        // TODO XXX FIXME Build dialog

        Dialog dialog = new Dialog(this.getActivity(), R.style.MySeriesTheme_Dialog);
        dialog.setContentView(R.layout.features_product_details);

        return dialog;
    }
}
