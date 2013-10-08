package mobi.myseries.gui.shared;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class PauseOnScrollListener implements OnScrollListener {

    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;
    private final OnScrollListener externalListener;

    public PauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        this(pauseOnScroll, pauseOnFling, null);
    }

    public PauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling,
            OnScrollListener customListener) {
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
            AsyncImageLoader.resume();
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            if (pauseOnScroll) {
                AsyncImageLoader.pause();
            }
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
            if (pauseOnFling) {
                AsyncImageLoader.pause();
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