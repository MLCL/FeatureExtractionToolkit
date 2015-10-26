/*
 * Copyright (c) 2010-2013, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package uk.ac.susx.mlcl.util;

import com.google.common.io.*;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Strategy;
import org.junit.*;
import uk.ac.susx.mlcl.test.AbstractTest;

import java.io.*;

import static org.junit.Assert.assertTrue;

/**
 * Unit test (actually more like integration tests) for the {@link uk.ac.susx.mlcl.util.CompressorStreamFactory2 } class.
 *
 * @author Hamish Morgan
 */
public class CompressorStreamFactory2Test extends AbstractTest {

    // File name extensions
    private static final String GZIP_EXT = ".gz";
    private static final String BZIP2_EXT = ".bz2";
    private static final String JAR_EXT = ".jar";
    private static final String XZ_EXT = ".xz";
    private static final String PACK_EXT = ".pack";
    // 
    private static final File RESOURCE_PATH = new File("src/test/resources/uk/ac/susx/mlcl/util");
    private static final File OUTPUT_PATH = new File("target/test/output/uk/ac/susx/mlcl/util");
    //
    private static final File TEXT_FILE = new File(RESOURCE_PATH, "Wikipedia-Brighton.xml");
    private static final File BZIP2_FILE = new File(RESOURCE_PATH, TEXT_FILE.getName() + BZIP2_EXT);
    private static final File GZIP_FILE = new File(RESOURCE_PATH, TEXT_FILE.getName() + GZIP_EXT);
    private static final File XZ_FILE = new File(RESOURCE_PATH, TEXT_FILE.getName() + XZ_EXT);
    //
    // Pack200 only works on JARs so we need different test data
    private static final File CLASS_FILE = new File(RESOURCE_PATH, "DummyCompressorInputStream.class");
    private static final File JAR_FILE = new File(RESOURCE_PATH, CLASS_FILE.getName() + JAR_EXT);
    private static final File PACK_FILE = new File(RESOURCE_PATH, JAR_FILE.getName() + PACK_EXT);
//    private static final File PACK_GZIP_FILE = new File(RESOURCE_PATH, PACK_FILE.getName() + GZIP_EXT);

    @BeforeClass
    public static void setUpClass() throws IOException {
        if (!OUTPUT_PATH.exists() && !OUTPUT_PATH.mkdirs()) {
            throw new IOException("Failed to create output dir: " + OUTPUT_PATH);
        }
    }

    private static void assertArchivesEquals(File inputFile1, File inputFile2) throws IOException {

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder()
                .setTransparentSignatureDetection(true)
                .build();

        final Closer closer = Closer.create();
        try {
            final CompressorInputStream inputStream1 =
                    closer.register(instance.createCompressorInputStream(
                            closer.register(new BufferedInputStream(
                                    closer.register(new FileInputStream(inputFile1))))));
            final CompressorInputStream inputStream2 =
                    closer.register(instance.createCompressorInputStream(
                            closer.register(new BufferedInputStream(
                                    closer.register(new FileInputStream(inputFile2))))));

            assertTrue("The archive contents (after decompression) are not the same.",
                    ByteStreams.equal(
                            new InputSupplier<InputStream>() {
                                @Override
                                public InputStream getInput() {
                                    return inputStream1;
                                }
                            },
                            new InputSupplier<InputStream>() {
                                @Override
                                public InputStream getInput() {
                                    return inputStream2;
                                }
                            }
                    ));
        } catch (Throwable t) {
            throw closer.rethrow(t);
        } finally {
            Closeables.closeQuietly(closer);
        }
    }

    @Test
    public void testCompressForwarding() throws IOException {
        final File outputFile = new File(OUTPUT_PATH, TEXT_FILE.getName() + ".copy");

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder()
                .setTransparentSignatureDetection(true)
                .build();

        final Closer closer = Closer.create();
        try {
            final InputStream is = closer.register(new FileInputStream(TEXT_FILE));
            final OutputStream os = closer.register(new FileOutputStream(outputFile));
            final CompressorOutputStream cos = closer.register(instance.createForwardingCompressorOutputStream(os));
            ByteStreams.copy(is, cos);
        } finally {
            Closeables.closeQuietly(closer);
        }
        assertArchivesEquals(outputFile, TEXT_FILE);
    }

    @Test
    public void testCompressBZip2() throws IOException {
        final File outputFile = new File(OUTPUT_PATH, TEXT_FILE.getName() + BZIP2_EXT);

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder().build();

        final Closer closer = Closer.create();
        try {
            final InputStream is = closer.register(new FileInputStream(TEXT_FILE));
            final OutputStream os = closer.register(new FileOutputStream(outputFile));
            final CompressorOutputStream cos = closer.register(instance.createBZip2CompressorOutputStream(os));
            ByteStreams.copy(is, cos);
        } finally {
            Closeables.closeQuietly(closer);
        }
        assertArchivesEquals(outputFile, BZIP2_FILE);
    }

    @Test
    public void testCompressGZip() throws IOException {
        final File outputFile = new File(OUTPUT_PATH, TEXT_FILE.getName() + GZIP_EXT);

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder().build();

        final Closer closer = Closer.create();
        try {
            final InputStream is = closer.register(new FileInputStream(TEXT_FILE));
            final OutputStream os = closer.register(new FileOutputStream(outputFile));
            final CompressorOutputStream cos = closer.register(instance.createGZipCompressorOutputStream(os));
            ByteStreams.copy(is, cos);
        } finally {
            Closeables.closeQuietly(closer);
        }
        assertArchivesEquals(outputFile, GZIP_FILE);
    }

    @Test
    public void testCompressXZ() throws IOException {
        final File outputFile = new File(OUTPUT_PATH, TEXT_FILE.getName() + XZ_EXT);

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder().build();

        final Closer closer = Closer.create();
        try {
            final InputStream is = closer.register(new FileInputStream(TEXT_FILE));
            final OutputStream os = closer.register(new FileOutputStream(outputFile));
            final CompressorOutputStream cos = closer.register(instance.createXZCompressorOutputStream(os));
            ByteStreams.copy(is, cos);
        } finally {
            Closeables.closeQuietly(closer);
        }
        assertArchivesEquals(outputFile, XZ_FILE);
    }

    @Test
    public void testCompressPack200_TempFile() throws IOException {
        final File outputFile = new File(OUTPUT_PATH, JAR_FILE.getName() + PACK_EXT);

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder()
                .setPack200Strategy(Pack200Strategy.TEMP_FILE)
                .build();

        final Closer closer = Closer.create();
        try {
            final InputStream is = closer.register(new FileInputStream(JAR_FILE));
            final OutputStream os = closer.register(new FileOutputStream(outputFile));
            final Pack200CompressorOutputStream cos = closer.register(instance.createPack200CompressorOutputStream(os));
            ByteStreams.copy(is, cos);
            cos.finish(); //!!
        } finally {
            Closeables.closeQuietly(closer);
        }
        // Archives will not necessarily be byte equal after decompression.
        // TODO: Compare the contained class (probably using URLClassloader and reflection??)
        //
        // assertArchivesEquals(outputFile, PACK_FILE);
    }


    @Test
    public void testCompressPack200_InMemory() throws IOException {
        final File outputFile = new File(OUTPUT_PATH, JAR_FILE.getName() + PACK_EXT);

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder()
                .setPack200Strategy(Pack200Strategy.IN_MEMORY)
                .build();

        final Closer closer = Closer.create();
        try {
            final InputStream is = closer.register(new FileInputStream(JAR_FILE));
            final OutputStream os = closer.register(new FileOutputStream(outputFile));
            final Pack200CompressorOutputStream cos = closer.register(instance.createPack200CompressorOutputStream(os));
            ByteStreams.copy(is, cos);
            cos.finish(); //!!
        } finally {
            Closeables.closeQuietly(closer);
        }
        // Archives will not necessarily be byte equal after decompression.
        // TODO: Compare the contained class (probably using URLClassloader and reflection??)
        //
        // assertArchivesEquals(outputFile, PACK_FILE);
    }

    @Test
    public void testDecompressForwarding() throws IOException, CompressorException {
        final File outputFile = new File(OUTPUT_PATH, TEXT_FILE.getName() + ".decompressed");

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder()
                .setTransparentSignatureDetection(true)
                .build();

        final Closer closer = Closer.create();
        try {
            final CompressorInputStream cis =
                    closer.register(instance.createCompressorInputStream(
                            closer.register(new BufferedInputStream(
                                    closer.register(new FileInputStream(TEXT_FILE))))));

            final OutputStream os = closer.register(new BufferedOutputStream(
                    closer.register(new FileOutputStream(outputFile))));

            ByteStreams.copy(cis, os);
        } finally {
            Closeables.closeQuietly(closer);
        }

        assertTrue("Output files differ.", Files.equal(outputFile, TEXT_FILE));
    }

    @Test
    public void testDecompressBZip2() throws IOException, CompressorException {
        final File outputFile = new File(OUTPUT_PATH, BZIP2_FILE.getName() + ".decompressed");

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder().build();

        final Closer closer = Closer.create();
        try {
            final CompressorInputStream cis =
                    closer.register(instance.createCompressorInputStream(
                            closer.register(new BufferedInputStream(
                                    closer.register(new FileInputStream(BZIP2_FILE))))));

            final OutputStream os = closer.register(new BufferedOutputStream(
                    closer.register(new FileOutputStream(outputFile))));

            ByteStreams.copy(cis, os);
        } finally {
            Closeables.closeQuietly(closer);
        }

        assertTrue("Output files differ.", Files.equal(outputFile, TEXT_FILE));
    }


    @Test
    public void testDecompressBZip2_Concatenate() throws IOException, CompressorException {
        final File outputFile = new File(OUTPUT_PATH, BZIP2_FILE.getName() + ".decompressed");

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder()
                .setDecompressConcatenated(true).build();

        final Closer closer = Closer.create();
        try {
            final CompressorInputStream cis =
                    closer.register(instance.createCompressorInputStream(
                            closer.register(new BufferedInputStream(
                                    closer.register(new FileInputStream(BZIP2_FILE))))));

            final OutputStream os = closer.register(new BufferedOutputStream(
                    closer.register(new FileOutputStream(outputFile))));

            ByteStreams.copy(cis, os);
        } finally {
            Closeables.closeQuietly(closer);
        }

        assertTrue("Output files differ.", Files.equal(outputFile, TEXT_FILE));
    }


    @Test
    public void testDecompressGZip() throws IOException, CompressorException {
        final File outputFile = new File(OUTPUT_PATH, GZIP_FILE.getName() + ".decompressed");

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder().build();

        final Closer closer = Closer.create();
        try {
            final CompressorInputStream cis =
                    closer.register(instance.createCompressorInputStream(
                            closer.register(new BufferedInputStream(
                                    closer.register(new FileInputStream(GZIP_FILE))))));

            final OutputStream os = closer.register(new BufferedOutputStream(
                    closer.register(new FileOutputStream(outputFile))));

            ByteStreams.copy(cis, os);
        } finally {
            Closeables.closeQuietly(closer);
        }

        assertTrue("Output files differ.", Files.equal(outputFile, TEXT_FILE));
    }

    @Test
    public void testDecompressXZ() throws IOException, CompressorException {
        final File outputFile = new File(OUTPUT_PATH, XZ_FILE.getName() + ".decompressed");

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder().build();

        final Closer closer = Closer.create();
        try {
            final CompressorInputStream cis =
                    closer.register(instance.createCompressorInputStream(
                            closer.register(new BufferedInputStream(
                                    closer.register(new FileInputStream(XZ_FILE))))));

            final OutputStream os = closer.register(new BufferedOutputStream(
                    closer.register(new FileOutputStream(outputFile))));

            ByteStreams.copy(cis, os);
        } finally {
            Closeables.closeQuietly(closer);
        }

        assertTrue("Output files differ.", Files.equal(outputFile, TEXT_FILE));
    }

    @Test
    public void testDecompressPack200() throws IOException, CompressorException {
        final File outputFile = new File(OUTPUT_PATH, PACK_FILE.getName() + ".decompressed");

        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder().build();

        final Closer closer = Closer.create();
        try {
            final CompressorInputStream cis =
                    closer.register(instance.createCompressorInputStream(
                            closer.register(new BufferedInputStream(
                                    closer.register(new FileInputStream(PACK_FILE))))));

            final OutputStream os = closer.register(new BufferedOutputStream(
                    closer.register(new FileOutputStream(outputFile))));

            ByteStreams.copy(cis, os);
        } finally {
            Closeables.closeQuietly(closer);
        }
        // Archives will not necessarily be byte equal after decompression.
        // TODO: Compare the contained class (probably using URLClassloader and reflection??)
        //
        // assertTrue("Output files differ.", Files.equal(outputFile, JAR_FILE));
    }

    /**
     * Test the auto-detection and transparent decompression works for various file formats.
     *
     * @throws IOException
     * @throws CompressorException
     */
    @Test
    public void testAutoDecompress() throws IOException, CompressorException {
        final CompressorStreamFactory2 instance = CompressorStreamFactory2.builder()
                .setTransparentSignatureDetection(true)
                .build();

        final File[] inputFiles = {TEXT_FILE, BZIP2_FILE, GZIP_FILE, XZ_FILE};


        final Closer closer = Closer.create();
        try {
            InputStream[] inputStreams = new InputStream[inputFiles.length];

            for (int i = 0; i < inputFiles.length; i++) {
                inputStreams[i] =
                        closer.register(instance.createCompressorInputStream(
                                closer.register(new BufferedInputStream(
                                        closer.register(new FileInputStream(inputFiles[i]))))));
            }

            boolean finished = false;
            while (!finished) {

                int[] bytes = new int[inputFiles.length];
                for (int i = 0; i < inputFiles.length; i++) {
                    bytes[i] = inputStreams[i].read();
                }
                for (int i = 1; i < bytes.length; i++) {
                    if (bytes[i - 1] != bytes[i])
                        Assert.fail("Streams did not match.");
                }

                if (bytes[0] == -1)
                    finished = true;
            }


        } finally {
            Closeables.closeQuietly(closer);
        }
    }
}
