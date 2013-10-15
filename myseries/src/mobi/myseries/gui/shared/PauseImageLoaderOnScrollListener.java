package mobi.myseries.gui.shared;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class PauseImageLoaderOnScrollListener implements OnScrollListener {

    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;
    private final OnScrollListener externalListener;
    private final AsyncImageLoader imageLoader;

    public PauseImageLoaderOnScrollListener(AsyncImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        this(imageLoader, pauseOnScroll, pauseOnFling, null);
    }

    public PauseImageLoaderOnScrollListener(AsyncImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling,
            OnScrollListener customListener) {
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        externalListener = customListener;

        this.imageLoader = imageLoader;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
            imageLoader.resume();
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            if (pauseOnScroll) {
                imageLoader.pause();
            }
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
            if (pauseOnFling) {
                imageLoader.pause();
            }
            break;
        }
        if (externalListener != null) {
            externalListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        if (externalListener != null) {
            externalListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }
}