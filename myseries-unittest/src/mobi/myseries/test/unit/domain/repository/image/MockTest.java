package mobi.myseries.test.unit.domain.repository.image;

import mobi.myseries.domain.repository.image.ImageRepository;
import junit.framework.TestCase;

import static org.mockito.Mockito.*;

public class MockTest extends TestCase {

    public void testA() {
        mock(ImageRepository.class);
    }
}
