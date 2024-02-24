package edu.umd.cs.findbugs.ba;


import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class URLClassPathTest {

    @TempDir
    private static Path tempDir;

    private static File testFile1;
    private static File testFile2;
    private static Path testDir;
    private URLClassPath urlCP;

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
            testFile2 = createZipFile("file2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new URLClassPath for each test.
     */
    @BeforeEach
    public void createURLClassPath() {
        urlCP = new URLClassPath();
    }

    /**
     * Closes each URLClassPath so temp resources can be deleted.
     */
    @AfterEach
    public void destroyURLClassPath() {
        urlCP.close();
    }

    // // Image 0

    /**
     * Tests for the known file protocol type.
     */
    @Test
    void fileProtocolTest() {
        Assertions.assertEquals("file", URLClassPath.getURLProtocol("file:test.zip"));
    }

    /**
     * Tests for an unknown protocol.
     */
    @Test
    void unknownProtocolTest() {
        Assertions.assertNull(URLClassPath.getURLProtocol("blah:test.zip"));
    }

    /**
     * Tests for no colon.
     */
    @Test
    void noColonProtocolTest() {
        Assertions.assertNull(URLClassPath.getURLProtocol("test.zip"));
    }

    /**
     * Tests getting an existing file extension.
     */
    @Test
    void getPresentFileExtension() {
        Assertions.assertEquals(".zip", URLClassPath.getFileExtension("file.zip"));
    }

    /**
     * Tests for attempting to get the extension
     * of a file with no extension.
     */
    @Test
    void getNullFileExtension() {
        Assertions.assertNull(URLClassPath.getFileExtension("file"));
    }

    /**
     * Tests for verifying if a string is an archive extension.
     */
    @Test
    void getPresentArchiveExtensionTest() {
        Assertions.assertTrue(URLClassPath.isArchiveExtension(".zip"));
    }

    /**
     * Tests for verifying if a string is not an archive extension.
     */
    @Test
    void getNotAnArchiveExtension() {
        Assertions.assertFalse(URLClassPath.isArchiveExtension(".zi"));
    }

    // // Image 1

    /*
     * Tests for various IOException causing arguments.
     */
    @Test
    void addInvalidURLTest() {
        Assertions.assertThrows(IOException.class, () -> urlCP.addURL("thing"));
    }

    @Test
    void addInvalidURLArchiveTest() {
        Assertions.assertThrows(IOException.class, () -> urlCP.addURL("thing.zip"));
    }

    @Test
    void addInvalidRemoteURLTest() {
        Assertions.assertThrows(IOException.class, () -> urlCP.addURL("http:thing"));
    }

    @Test
    void addInvalidDirTest() {
        Assertions.assertThrows(IOException.class, () -> urlCP.addURL("thing/"));
    }

    // // Image 2

    /**
     * Test for a single local zip.
     */
    @Test
    void addSingleZipURLTest() {
        try {
            String path = testFile1.getCanonicalPath();
            urlCP.addURL(path);
            Assertions.assertEquals(path, urlCP.getClassPath());
        } catch (IOException e) {
            Assertions.fail("Test generated an IOException: " + e);
        }
    }

    /**
     * Test for two local zips. Also covers two files for getClassPath.
     */
    @Test
    void addTwoZipURLsTest() {
        try {
            String path1 = testFile1.getCanonicalPath();
            String path2 = testFile2.getCanonicalPath();
            urlCP.addURL(path1);
            urlCP.addURL(path2);
            Assertions.assertEquals(path1 + File.pathSeparator + path2, urlCP.getClassPath());
        } catch (IOException e) {
            Assertions.fail("Test generated an IOException: " + e);
        }
    }

    /**
     * Test for local directory.
     */
    @Test
    void addSingleDirURLTest() {
        try {
            String path = testDir.toFile().getCanonicalPath();
            urlCP.addURL(path);
            Assertions.assertEquals(path, urlCP.getClassPath());
        } catch (IOException e) {
            Assertions.fail("Test generated an IOException: " + e);
        }
    }

    /**
     * Test for local directory with slash.
     */
    @Test
    void addSingleDirWithSlashURLTest() {
        try {
            String path = testDir.toFile().getCanonicalPath();
            urlCP.addURL(path + "/");
            Assertions.assertEquals(path + "/", urlCP.getClassPath());
        } catch (IOException e) {
            Assertions.fail("Test generated an IOException: " + e);
        }
    }

    /**
     * Test for remote slashed directory.
     */
    @Test
    void addSingleRemoteDirWithSlashURLTest() {
        try {
            String path = testDir.toFile().getCanonicalPath();
            urlCP.addURL("http:" + path + "/");
            Assertions.assertEquals("http:" + path + "/", urlCP.getClassPath());
        } catch (IOException e) {
            Assertions.fail("Test generated an IOException: " + e);
        }
    }

    /**
     * Test for remote zip.
     */
    @Test
    void addSingleRemoteZipURLTest() {
        try {
            String path = testFile1.getCanonicalPath();
            urlCP.addURL("http:" + path);
            Assertions.assertEquals("http:" + path, urlCP.getClassPath());
        } catch (IOException e) {
            Assertions.fail("Test generated an IOException: " + e);
        }
    }

    /**
     * Test for looking up an invalid entry.
     */
    @Test
    void lookupInvalidClass() {
        try {
            String path = testDir.toFile().getCanonicalPath();
            urlCP.addURL(path);
            Assertions.assertThrows(ClassNotFoundException.class, () -> urlCP.lookupClass("thing"));
        } catch (IOException e) {
            Assertions.fail("Test generated an IOException: " + e);
        }
    }
}
