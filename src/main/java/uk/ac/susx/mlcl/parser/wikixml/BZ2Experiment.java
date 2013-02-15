/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.parser.wikixml;

import com.google.common.io.Closer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.compress.compressors.CompressorStreamFactory;
import uk.ac.susx.mlcl.util.CompressorStreamFactory2;

/**
 * @author hiam20
 */
public class BZ2Experiment {

    public static void main(String[] args) throws IOException {

        final File dataDir = new File("/Volumes/LocalScratchHD/LocalHome/Data");
//        final File wikidump = new File(dataDir, "enwiki-20061130-pages-articles.xml.bz2");
//        final File wikidump = new File(dataDir, "enwiki-20130204-pages-articles-multistream.xml.bz2");
        final File wikidump = new File(dataDir, "enwiki-20130204-pages-articles-multistream-index.txt.bz2");

        final Charset charset = Charset.forName("UTF-8");
        final int limit = 10000;

        // Confugration that's unlikely to be altered.
        final int buffersize = 0xffff;
        final CompressorStreamFactory csFactory = CompressorStreamFactory2.builder()
                .setTransparentSignatureDetection(true)
                .setDecompressConcatenated(true)
                .build();


        final Closer closer = Closer.create();
        try {


            final Reader reader =
                    closer.register(new InputStreamReader(
                            closer.register(csFactory.createCompressorInputStream(
                                    closer.register(new BufferedInputStream(
                                            closer.register(new FileInputStream(wikidump)))))), charset));
            final CharBuffer buffer = CharBuffer.allocate(buffersize);
            int count = 0;
            while (count < limit && -1 != reader.read(buffer)) {
                buffer.flip();
                count += buffer.length();

                // Do stuff with the buffer
                System.out.print(buffer);


                buffer.clear();
            }

        } catch (Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }
}
