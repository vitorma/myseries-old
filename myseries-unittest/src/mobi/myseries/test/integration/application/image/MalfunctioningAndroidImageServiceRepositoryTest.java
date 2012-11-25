package mobi.myseries.test.integration.application.image;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import mobi.myseries.application.image.AndroidImageServiceRepository;
import mobi.myseries.application.image.ImageServiceRepository;
import android.content.Context;

public class MalfunctioningAndroidImageServiceRepositoryTest extends MalfunctioningImageServiceRepositoryTest {

    @Override
    protected ImageServiceRepository newFailingImageServiceRepository() {
        Context contextWithoutExternalStorage =  mock(Context.class);
        when(contextWithoutExternalStorage.getExternalFilesDir(null)).thenReturn(null);

        return new AndroidImageServiceRepository(contextWithoutExternalStorage);
    }
}
