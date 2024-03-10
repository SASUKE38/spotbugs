package edu.umd.cs.findbugs.ba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SourceFinderTest {

    @TempDir
    private static Path tempDir;

    private static File testFile1;
    private static Path testDir;

    /**
     * Creates a zip file in the temporary directory for testing.
     *
     * @param name The name of the zip file. Should not have an extension.
     * @return A reference to the created file.
     * @throws IOException If the file could not be created.
     */
    private static File createZipFile(String name) throws IOException {
        Path tempFile = tempDir.resolve(name + ".zip");
        File testFile = tempFile.toFile();
        testFile.createNewFile();
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(testFile));
        ZipEntry entry = new ZipEntry("entry");
        out.putNextEntry(entry);
        out.write(0);
        out.closeEntry();
        out.finish();
        out.close();
        return testFile;
    }

    /**
     * Creates two zip files and an empty folder that can be used for tests.
     * The content of the files is ignored; only their presence matters.
     */
    @BeforeAll
    public static void tempSetup() {
        try {
            testDir = tempDir.resolve("folder");
            testDir.toFile().mkdir();
            testFile1 = createZipFile("file1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void publicClassConstructionTest() {
        try {
            String path = testFile1.getCanonicalPath();
            ZipInputStream in = new ZipInputStream(new FileInputStream(path));
            SourceFinder.PublicInMemorySourceRepository r = new SourceFinder.PublicInMemorySourceRepository(in);
            Assertions.assertTrue(r.contains("entry"));
        } catch (Exception e) {
            Assertions.fail("Test generated an exception: " + e);
        }
    }
}
