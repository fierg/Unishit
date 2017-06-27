package de.unitrier.st.PII.ueb03;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ListTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private static final double DELTA = 0.00001;
    private List list;
    private static File import_file, export_file;

    @BeforeClass
    public static void setUpClass() {
        import_file = new File("src/de/unitrier/st/PII/ueb03/data/data_import.csv");
        assertTrue(import_file.exists());
        export_file = new File("src/de/unitrier/st/PII/ueb03/data/data_export.csv");
        if (!export_file.delete()) {
            System.err.println("Error while deleting file: " + export_file.getAbsolutePath());
        }
        assertFalse(export_file.exists());
    }

    @Before
    public void setUp() {
        list = new List();
    }

    @Test
    public void testReadException() {
        File wrong_path = new File("src/de/unitrier/st/PII/ueb03/data/not_existing_file.csv");
        list.readFromFile(wrong_path); // only message on stderr
    }

    @Test
    public void testReadFromFile() {
        list.readFromFile(import_file);
        assertArrayEquals(
                new double[]{0.17, 0.22, 0.38, 0.77, 0.34, 0.35, 0.11, 0.32, 0.66, 0.54, 0.29, 0.93, 0.16, 0.43, 0.85,
                0.72, 0.15, 0.78, 0.72, 0.36, 0.94, 0.15, 0.45, 0.78, 0.24,0.75, 0.15},
                list.asArray(),
                DELTA
        );
    }

    @Test
    public void testWriteException() throws IOException {
        expectedException.expect(IOException.class);
        list.writeToFile(import_file);
    }

    @Test
    public void testWriteToFile() {
        list.append(new double[]{0.17, 0.22, 0.38, 0.77, 0.34, 0.35, 0.11, 0.32, 0.66, 0.54, 0.29, 0.93, 0.16,
                0.43, 0.85, 0.72, 0.15, 0.78, 0.72, 0.36, 0.94, 0.15, 0.45, 0.78, 0.24, 0.75, 0.15});
        try {
            list.writeToFile(export_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.empty();
        list.readFromFile(export_file);
        assertArrayEquals(
                new double[]{0.17, 0.22, 0.38, 0.77, 0.34, 0.35, 0.11, 0.32, 0.66, 0.54, 0.29, 0.93, 0.16, 0.43, 0.85,
                        0.72, 0.15, 0.78, 0.72, 0.36, 0.94, 0.15, 0.45, 0.78, 0.24,0.75, 0.15},
                list.asArray(),
                DELTA
        );
    }
}
