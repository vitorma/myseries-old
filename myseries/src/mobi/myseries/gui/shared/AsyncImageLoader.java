package mobi.myseries.gui.shared;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import mobi.myseries.application.App;
import mobi.myseries.shared.Validate;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class AsyncImageLoader {

    private static AtomicBoolean pause = new AtomicBoolean(false);

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);

            this.bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private static class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private final BitmapFetchingMethod bitmapFetchingMethod;

        public BitmapWorkerTask(BitmapFetchingMethod bitmapFetchingMethod, ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            this.imageViewReference = new WeakReference<ImageView>(imageView);
            this.bitmapFetchingMethod = bitmapFetchingMethod;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Void... params) {
            if(pause.get()) {
                synchronized (pause) {
                    try {
                        pause.wait();
                    } catch (InterruptedException e) {
                        //LOG someting
                    }
                }
            }
            return this.bitmapFetchingMethod.loadBitmap();
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private static boolean cancelPotentialWork(BitmapFetchingMethod bitmapFetchingMethod, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            BitmapFetchingMethod workerTaskFetchingMethod = bitmapWorkerTask.bitmapFetchingMethod;

            if (bitmapFetchingMethod.equals(workerTaskFetchingMethod)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
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


    public interface BitmapFetchingMethod {
        public Bitmap loadBitmap();
    }

    public static void loadBitmapOn(BitmapFetchingMethod bitmapFetchingMethod, Bitmap defaultBitmap, ImageView destinationView) {
        Validate.isNonNull(bitmapFetchingMethod, "bitmapFetchingMethod");
        Validate.isNonNull(defaultBitmap, "defaultBitmap");
        Validate.isNonNull(destinationView, "destinationView");

        if (cancelPotentialWork(bitmapFetchingMethod, destinationView)) {
            BitmapWorkerTask workerTask = new BitmapWorkerTask(bitmapFetchingMethod, destinationView);
            AsyncDrawable asyncDrawable = new AsyncDrawable(resources(), defaultBitmap, workerTask);

            destinationView.setImageDrawable(asyncDrawable);

            workerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    // TODO(Gabriel): Dependency injection in static attribute for testing purposes
    // its terrible but ...
    public static Resources resources = null;

    private static Resources resources() {
        if (resources == null) {
            return App.resources();
        } else {
            return resources;
        }
    }

    public static void pause() {
        pause.set(true);
    }

    public static void resume() {
        synchronized (pause) {
            pause.set(false);
            pause.notifyAll();
        }
        
    }
}
