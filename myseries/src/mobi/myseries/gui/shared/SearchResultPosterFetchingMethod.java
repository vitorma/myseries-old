package mobi.myseries.gui.shared;

import mobi.myseries.application.App;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.gui.shared.AsyncImageLoader.BitmapFetchingMethod;
import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;

public class SearchResultPosterFetchingMethod implements BitmapFetchingMethod {

    private final SearchResult result;

    public SearchResultPosterFetchingMethod(SearchResult result) {
        Validate.isNonNull(result, "result");

        this.result = result;
    }

    @Override
    public Bitmap loadCachedBitmap() {
        return App.imageService().getCachedPosterOf(this.result);
    }

    @Override
    public Bitmap loadBitmap() {
        Bitmap poster = null;
        try {
            poster = App.imageService().getPosterOf(this.result);
        } catch (ConnectionFailedException e) {
        } catch (NetworkUnavailableException e) {
        }
        return poster;
    }
}
