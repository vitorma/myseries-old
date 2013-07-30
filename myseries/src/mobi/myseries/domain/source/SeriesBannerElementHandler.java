package mobi.myseries.domain.source;

import mobi.myseries.shared.Validate;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;

public class SeriesBannerElementHandler {
    private Element bannerElement;

    public SeriesBannerElementHandler(RootElement rootElement) {
        Validate.isNonNull(rootElement, "rootElement");

        this.bannerElement = rootElement.getChild("Banner");
    }

    public SeriesBannerElementHandler lookForFirstPoster() {
        this.bannerElement.getChild("BannerPath").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String path = body.trim();

                if (path.startsWith("p")) {
                    throw new FirstPosterFoundInterruption(path);
                }
            }
        });

        return this;
    }

    public static class FirstPosterFoundInterruption extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private String firstPosterPath;

        private FirstPosterFoundInterruption(String path) {
            this.firstPosterPath = path;
        }

        public String firstPosterPath() {
            return this.firstPosterPath;
        }
    }
}
