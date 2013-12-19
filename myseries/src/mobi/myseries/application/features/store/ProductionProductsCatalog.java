package mobi.myseries.application.features.store;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;

import mobi.myseries.R;
import mobi.myseries.application.features.features.Feature;
import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;
import mobi.myseries.shared.Validate;

public class ProductionProductsCatalog extends ProductCatalog {

    private final Context context;

    private final Set<ProductDescription> implementedProducts =
            Collections.unmodifiableSet(new HashSet<ProductDescription>(Arrays.asList(
            new ProductDescription(new Sku("cloud_backup")) {
                @Override
                public String name() {
                    return string(R.string.cloud_backup);
                }
                @Override
                public String description() {
                    return string(R.string.cloud_backup_description);
                }
                @Override
                public Set<Feature> features() {
                    return new HashSet<Feature>(Arrays.asList(Feature.CLOUD_BACKUP));
                }
            },
            new ProductDescription(new Sku("schedule_widget")) {
                @Override
                public String name() {
                    return string(R.string.schedule_widget);
                }
                @Override
                public String description() {
                    return string(R.string.schedule_widget_description);
                }
                @Override
                public Set<Feature> features() {
                    return new HashSet<Feature>(Arrays.asList(Feature.SCHEDULE_WIDGET));
                }
                @Override
                public List<Integer> picturesResourceIds() {
                    // TODO(Cleber,Gabriel): Add screenshot of the widget on the lock screen 
                    return Arrays.asList(
                            R.drawable.features_schedulewidget_homescreen,
                            R.drawable.features_schedulewidget_settings,
                            R.drawable.features_schedulewidget_homescreen_checking_episode);
                }
            })));

    private String string(int id) {
        return this.context.getResources().getString(id);
    }

    public ProductionProductsCatalog(Context context) {
        Validate.isNonNull(context, "context");
        this.context = context;
    }

    @Override
    public Set<ProductDescription> implementedProducts() {
        return this.implementedProducts;
    }
}
