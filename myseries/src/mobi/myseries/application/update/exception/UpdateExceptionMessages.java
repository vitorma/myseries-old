package mobi.myseries.application.update.exception;

import android.content.Context;
import mobi.myseries.R;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;

public class UpdateExceptionMessages {

    public static String messageFor(Context context, Exception e) {
        if (e instanceof ConnectionFailedException) {
            return context.getString(R.string.update_connection_failed);

        } else if (e instanceof ConnectionTimeoutException) {
            return context.getString(R.string.update_connection_timeout);

        } else if (e instanceof ParsingFailedException) {
            return context.getString(R.string.update_parsing_failed);

        } else if (e instanceof SeriesNotFoundException) {
            return context.getString(
                    R.string.update_series_not_found,
                    ((SeriesNotFoundException) e).seriesName());

        } else if (e instanceof UpdateMetadataUnavailableException) {
            return context.getString(R.string.update_metadata_unavailable);

        } else if (e instanceof NetworkUnavailableException) {
            return context.getString(R.string.update_network_unavailable);

        } else if (e instanceof UpdateTimeoutException) {
            return context.getString(R.string.update_timeout);

        } else {
            return e.getMessage();

        }
    }

    // TODO(Gabriel): shortMessageFor(exception) to be used in the notifications
}
