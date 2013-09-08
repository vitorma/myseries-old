package mobi.myseries.test.unit.gui.shared;

import mobi.myseries.test.R;
import mobi.myseries.gui.shared.AsyncImageLoader;
import mobi.myseries.gui.shared.AsyncImageLoader.BitmapFetchingMethod;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.sameInstance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.test.InstrumentationTestCase;
import android.widget.ImageView;

public class AsyncImageLoaderTest extends InstrumentationTestCase {

    private Bitmap defaultBitmap;
    private Bitmap bitmapToBeLoaded;

    private ImageView destinationView;

    private BitmapFetchingMethod bitmapFetchingMethod;
    private BitmapFetchingMethod nullBitmapFetchingMethod;

    public void setUp() {
        this.bitmapToBeLoaded = BitmapFactory.decodeResource(
                this.getInstrumentation().getContext().getResources(),
                R.drawable.icon);

        this.defaultBitmap = BitmapFactory.decodeResource(
                this.getInstrumentation().getContext().getResources(),
                R.drawable.icon);

        this.destinationView = new ImageView(this.getInstrumentation().getContext());

        this.bitmapFetchingMethod =
                new BitmapFetchingMethod() {
                    @Override
                    public Bitmap loadBitmap() {
                        return bitmapToBeLoaded;
                    }
                };

        this.nullBitmapFetchingMethod =
                new BitmapFetchingMethod() {
                    @Override
                    public Bitmap loadBitmap() {
                        return bitmapToBeLoaded;
                    }
                };

        AsyncImageLoader.resources = this.getInstrumentation().getContext().getResources();
    }

    public void tearDown() {
        this.bitmapToBeLoaded = null;
        this.defaultBitmap = null;

        this.destinationView = null;

        this.bitmapFetchingMethod = null;
        this.nullBitmapFetchingMethod = null;
    }

    // Arguments validation

    public void testItShouldNotAcceptNullBitmapFetchingMethod() {

        try {
            AsyncImageLoader.loadBitmapOn(
                    null,
                    defaultBitmap,
                    destinationView);

            fail("Exception not thrown.");
        } catch (IllegalArgumentException e) {}
    }

    public void testItShouldNotAcceptNullDefaultImage() {
        ImageView destinationView = new ImageView(this.getInstrumentation().getContext());

        try {
            AsyncImageLoader.loadBitmapOn(
                    bitmapFetchingMethod,
                    null,
                    destinationView);

            fail("Exception not thrown.");
        } catch (IllegalArgumentException e) {}
    }

    public void testItShouldNotAcceptNullDestinationImageView() {
        try {
            AsyncImageLoader.loadBitmapOn(
                    bitmapFetchingMethod,
                    defaultBitmap,
                    null);

            fail("Exception not thrown.");
        } catch (IllegalArgumentException e) {}
    }

    // Behaviour

    public void testItShouldTakeTheImageAfterSomeTime() {
        ImageView destinationView = new ImageView(this.getInstrumentation().getContext());

        AsyncImageLoader.loadBitmapOn(
                bitmapFetchingMethod,
                defaultBitmap,
                destinationView);

        //waitForMiliseconds(500);

        assertTrue(((BitmapDrawable) destinationView.getDrawable()).getBitmap().sameAs(defaultBitmap));
    }

    public void testItShouldShowTheDefaultImageIfBitmapFetchingMethodReturnsNull() {
        ImageView destinationView = new ImageView(getInstrumentation().getContext());

        AsyncImageLoader.loadBitmapOn(
                nullBitmapFetchingMethod,
                defaultBitmap,
                destinationView);

        assertThat(((BitmapDrawable) destinationView.getDrawable()).getBitmap(),
                sameInstance(defaultBitmap));
    }

    public void testItShouldNotSetTheBitmapToNull() {
        ImageView destinationView = mock(ImageView.class);

        doCallRealMethod().when(destinationView).setImageDrawable(any(Drawable.class));
        doCallRealMethod().when(destinationView).getDrawable();

        AsyncImageLoader.loadBitmapOn(
                nullBitmapFetchingMethod,
                defaultBitmap,
                destinationView);

        //waitForMiliseconds(500);

        verify(destinationView, never()).setImageDrawable(null);
        verify(destinationView, never()).setImageBitmap(null);
    }

    // Test tools

    private void waitForMiliseconds(long time) {
        synchronized (this) {
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
