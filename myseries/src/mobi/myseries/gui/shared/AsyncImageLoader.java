package mobi.myseries.gui.shared;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import mobi.myseries.application.App;
import mobi.myseries.shared.Validate;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class AsyncImageLoader {

    public static interface BitmapFetchingMethod {
        public Bitmap loadBitmap();
        public Bitmap loadCachedBitmap();
    }

    // TODO(Gabriel): This is for a smooth transition between the class with static methods
    // in its public interface and the one with private methods. Should we make it deprecated?
    // The point in doing this is that the loader now has an important state related to some
    // fragments: its paused state. A single global AsyncImageLoader being paused and resumed
    // by some unrelated views is a potential source of bugs. Let's keep things localized.
    public static final AsyncImageLoader globalInstance = new AsyncImageLoader();

    private Resources resources = null;
    private AtomicBoolean paused = new AtomicBoolean(false);

    public AsyncImageLoader() {
        this(App.resources());
    }

    public AsyncImageLoader(Resources resources) {
        Validate.isNonNull(resources, "resources");
        this.resources = resources;
    }

    public void pause() {
        paused.set(true);
    }

    public void resume() {
        paused.set(false);
        synchronized (paused) {
            paused.notifyAll();
        }
    }

    public void loadBitmapOn(BitmapFetchingMethod bitmapFetchingMethod,
            Bitmap defaultBitmap, ImageView destinationView) {
        loadBitmapOn(bitmapFetchingMethod, defaultBitmap, destinationView, null);
    }

    public void loadBitmapOn(BitmapFetchingMethod bitmapFetchingMethod,
            Bitmap defaultBitmap, ImageView destinationView,
            ProgressBar progressBar) {
        Validate.isNonNull(bitmapFetchingMethod, "bitmapFetchingMethod");
        Validate.isNonNull(defaultBitmap, "defaultBitmap");
        Validate.isNonNull(destinationView, "destinationView");

        if (!cancelPotentialWork(bitmapFetchingMethod, destinationView)) {
            BitmapWorkerTask workerTask = new BitmapWorkerTask(
                    bitmapFetchingMethod, defaultBitmap, destinationView, progressBar);
            AsyncDrawable asyncDrawable = new AsyncDrawable(
                    resources, defaultBitmap, workerTask);

            destinationView.setImageDrawable(asyncDrawable);

            workerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                BitmapWorkerTask bitmapWorkerTask) {
            super(res);

            this.bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
                    bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private class BitmapWorkerTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<ProgressBar> progressBarReference;
        private final BitmapFetchingMethod bitmapFetchingMethod;
        private final Bitmap defaultBitmap;

        private Bitmap bitmap;

        public BitmapWorkerTask(
                BitmapFetchingMethod bitmapFetchingMethod,
                Bitmap defaultBitmap,
                ImageView imageView,
                ProgressBar progressBar) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            this.imageViewReference = new WeakReference<ImageView>(imageView);
            this.progressBarReference = new WeakReference<ProgressBar>(progressBar);
            this.bitmapFetchingMethod = bitmapFetchingMethod;
            this.defaultBitmap = defaultBitmap;
        }

        @Override
        protected void onPreExecute() {
            this.bitmap = this.bitmapFetchingMethod.loadCachedBitmap();

            if (bitmap != null) {
                setBitmap();
                hideProgressBar();
                this.cancel(true);
                return;
            }

            ProgressBar progressBar = progressBarReference.get();
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        // Decode image in background.
        @Override
        protected Void doInBackground(Void... params) {
            if (this.bitmap != null) {
                return null;
            }

            while (paused.get()) {
                synchronized (paused) {
                    try {
                        paused.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (!isCancelled()) {
                this.bitmap = this.bitmapFetchingMethod.loadBitmap();
            }

            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void param) {
            if (isCancelled()) {
                return;
            }

            setBitmap();
            hideProgressBar();
        }

        private void hideProgressBar() {
            ProgressBar progressBar = progressBarReference.get();
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }

        private void setBitmap() {
            ImageView imageView = imageViewReference.get();
            BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask && imageView != null) {
                if (this.bitmap == null) {
                    this.bitmap = defaultBitmap;
                }
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private static boolean cancelPotentialWork(
            BitmapFetchingMethod bitmapFetchingMethod, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            BitmapFetchingMethod workerTaskFetchingMethod = bitmapWorkerTask.bitmapFetchingMethod;

            if (bitmapFetchingMethod.equals(workerTaskFetchingMethod)) {
                // The same work is already in progress
                return true;
            } else {
                // cancel previous work
                bitmapWorkerTask.cancel(true);
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return false;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}
