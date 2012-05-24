package mobi.myseries.gui.shared;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public abstract class CoverFlowAdapter extends BaseAdapter {
    private float width = 0f;
    private float height = 0f;
    private Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

    @Override
    public final Bitmap getItem(int position) {
        WeakReference<Bitmap> weakBitmapReference = this.bitmapMap.get(position);

        if (weakBitmapReference != null) {
            Bitmap bitmap = weakBitmapReference.get();
            if (bitmap != null) {return bitmap;}
        }

        Bitmap bitmap = this.createBitmap(position);
        this.bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));

        return bitmap;
    }

    @Override
    public final synchronized long getItemId(int position) {
        return position;
    }

    @Override
    public final synchronized ImageView getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            Context context = parent.getContext();
            imageView = new ImageView(context);
            imageView.setLayoutParams(new CoverFlow.LayoutParams((int) this.width, (int) this.height));
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(this.getItem(position));

        return imageView;
    }

    protected abstract Bitmap createBitmap(int position);

    public synchronized void setWidth(float width) {
        this.width = width;
    }

    public synchronized void setHeight(float height) {
        this.height = height;
    }
}
