package br.edu.ufcg.aweseries.test.unit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import br.edu.ufcg.aweseries.data.FileRepository;
import junit.framework.TestCase;

//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.CoreMatchers.*;

public class FileRepositoryTest extends TestCase {

    private static String TEST_FILE_NAME = "file_name";
    private static String TEST_FILE_CONTENT = "File content.";

    private static InputStream testFileContent() {
        return new ByteArrayInputStream(TEST_FILE_CONTENT.getBytes());
    }

    private FileRepository repository;

    public void setUp() {
        this.repository = new FileRepository();
    }

    public void tearDown() {
        this.repository = null;
    }

    public void testSaveNullName() {
        try {
            this.repository.save(TEST_FILE_NAME, null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testSaveNullContent() {
        try {
            this.repository.save(null, testFileContent());
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void failingtestConsistentRetrievalOfSavedFile() {
        // TODO
    }
}
