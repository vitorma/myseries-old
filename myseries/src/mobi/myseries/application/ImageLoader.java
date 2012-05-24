package mobi.myseries.application;
import java.lang.ref.WeakReference;

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.ExternalStorageNotAvailableException;
import mobi.myseries.domain.repository.ImageDirectory;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
 
public class ImageLoader {
    
    private static final Resources resources = App.environment().context().getResources();
    private static final ImageDirectory IMAGE_DIRECTORY = new ImageDirectory(App.environment().context());
 
    public void load(String path, ImageView v, Bitmap defaultImage) {
        if(cancelPotentialLoad(path, v)) {
            LoadImageTask task = new LoadImageTask(v, defaultImage);
            LoadDrawable drawable = new LoadDrawable(task);
            v.setImageDrawable(drawable);
            task.execute(path);
        }
    }
 
    private Bitmap loadFile(String filePath) {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        bfo.outWidth = 50;
        bfo.outHeight = 50;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bfo);
        return bitmap;
    }
 
    private static boolean cancelPotentialLoad(String path, ImageView v) {
        LoadImageTask loadTask = getAsyncLoadImageTask(v);
 
        if(loadTask != null) {
            String taskPath = loadTask.getPath();
            if((taskPath == null) || (!taskPath.equals(path))) {
                loadTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }
 
    private static LoadImageTask getAsyncLoadImageTask(ImageView v) {
        if(v != null) {
            Drawable drawable = v.getDrawable();
            if(drawable instanceof LoadDrawable) {
                LoadDrawable asyncLoadedDrawable = (LoadDrawable)drawable;
                return asyncLoadedDrawable.getAsyncLoadTask();
            }
        }
        return null;
    }
 
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
 
        private String path;
        private final WeakReference<ImageView> imageViewReference;
        private final Bitmap defaultImage;
 
        public String getPath() {
            return path;
        }
 
        public LoadImageTask(ImageView v, Bitmap defaultImage) {
            this.imageViewReference = new WeakReference<ImageView>(v);
            this.defaultImage = defaultImage;
        }
 
        @Override
        protected void onPostExecute(Bitmap bmp) {
            if(imageViewReference != null) {
                ImageView v = imageViewReference.get();
                LoadImageTask loadTask = getAsyncLoadImageTask(v);
                if(this == loadTask) {
                    if(bmp == null)
                        v.setImageBitmap(defaultImage);
                    v.setImageBitmap(bmp);
                }
            }
        }
 
        @Override
        protected Bitmap doInBackground(String... params) {
            path = params[0];
            return loadFile(path);
        }
    }
 
    private class LoadDrawable extends BitmapDrawable {
        private final WeakReference<LoadImageTask> asyncLoadTaskReference;
 
        public LoadDrawable(LoadImageTask asyncLoadTask) {
            super(resources);
            asyncLoadTaskReference = new WeakReference<LoadImageTask>(asyncLoadTask);
        }
 
        public LoadImageTask getAsyncLoadTask() {
            return asyncLoadTaskReference.get();
        }
 
    }

    public void load(Series series, ImageView image, Bitmap genericPosterImage) {
        //TODO: try to get from cache
        String path;
        try {
            path = IMAGE_DIRECTORY.getPathForPoster(series);
            load(path, image, genericPosterImage);
        } catch (ExternalStorageNotAvailableException e) {
            image.setImageBitmap(genericPosterImage);
        }
        
        
    }
}