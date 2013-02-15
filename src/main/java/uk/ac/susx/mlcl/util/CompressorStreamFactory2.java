/*
 * Copyright (c) 2013, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.util;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Strategy;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.tukaani.xz.LZMA2Options;

/**
 * Factory to auto-detect and instantiate various Compressor[In|Out]putStreams
 * from names or byte signatures.
 * <p/>
 * The standard {@link CompressorStreamFactory} class has been extended to allow
 * the factory to be configured with various options for the construction of
 * compressor streams.
 * <p/>
 * Instances of CompressorStreamFactory2 immutable, with few side effects, so
 * they should be entirely thread safe. The only cause for concern is with
 * respect to instantiated compressor stream, which can collide over shared
 * resources such as on the file system.
 *
 * @author Hamish Morgan
 */
@Immutable
@Nonnull
public class CompressorStreamFactory2 extends CompressorStreamFactory {

    // default for the various configuration fields
    private static final boolean DEFAULT_DECOMPRESS_CONCATENATED = false;
    private static final Pack200Strategy DEFAULT_PACK200_STRATEGY = Pack200Strategy.IN_MEMORY;
    private static final ImmutableMap<String, String> DEFAULT_PACK200_PROPS = ImmutableMap.of();
    private static final int DEFAULT_BZIP2_BLOCK_SIZE = BZip2CompressorOutputStream.MAX_BLOCKSIZE;
    private static final int DEFAULT_XZ_PRESET = LZMA2Options.PRESET_DEFAULT;
    private static final boolean DEFAULT_NAME_DETECTION_FALLBACK = false;
    private static final boolean DEFAULT_TRANSPARENT_SIGNATURE_DETECTION = false;
    //
    // configuration fields
    //
    /**
     * If true, decompress until the end of the input; if false, stop after the
     * first stream and leave the input position to point to the next byte after
     * the stream. This options applies to BZip2 , GZip, and XZ decompression
     * algorithms.
     */
    private final boolean decompressConcatenated;
    /**
     * The different modes the Pack200 streams can use to wrap input and output;
     * either in memory or using a temporary file.
     */
    private final Pack200Strategy pack200Strategy;
    /**
     * Hold various properties for the Pack200 algorithm. See {@link java.util.jar.Pack200
     * } for a list of supported properties.
     */
    private final ImmutableMap<String, String> pack200Props;
    /**
     * the blockSize as 100k units.
     */
    private final int bzip2BlockSize;
    /**
     * XZ compressor LZMA2 preset level.
     * <p/>
     * The presets 0-3 are fast presets with medium compression. The presets 4-6
     * are fairly slow presets with high compression. The default preset is 6.
     * <p/>
     * The presets 7-9 are like the preset 6 but use bigger dictionaries and
     * have higher compressor and decompressor memory requirements. Unless the
     * uncompressed size of the file exceeds 8 MiB, 16 MiB, or 32 MiB, it is
     * waste of memory to use the presets 7, 8, or 9, respectively.
     */
    private final int xzPreset;
    /**
     * Whether the factory should fall back to data signature detection, after
     * name detection has been attempted but fails. Otherwise a
     * CompressorException will be thrown.
     */
    private final boolean nameDetectionFallback;
    /**
     * Whether the factory should fall back to returning a dummy compressor
     * stream when signature detection fails. Otherwise and CompressorException
     * will be thrown. The dummy compressor simply delegates calls to the source
     * stream without alternation.
     */
    private final boolean transparentSignatureDetection;

    /**
     * Private constructor used by the builder.
     *
     * @param decompressConcatenated whether multi-stream archives should be concatenated
     * @param pack200Strategy temporary storage method used by Pack200 algorithm
     * @param pack200Props  additional properties to set on the Pack200 algorithm
     * @param bzip2BlockSize block-size multiple (of 100k) for BZip2 algorithm
     * @param xzPreset compression level for XZ algorithm
     * @param nameDetectionFallback  whether name detection should fall back to attempting signature detection
     * @param transparentSignatureDetection  whether signature detection should fall back to returning a no-op compressor.
     */
    private CompressorStreamFactory2(final boolean decompressConcatenated,
                                     final Pack200Strategy pack200Strategy,
                                     final ImmutableMap<String, String> pack200Props,
                                     final int bzip2BlockSize,
                                     final int xzPreset,
                                     final boolean nameDetectionFallback,
                                     final boolean transparentSignatureDetection) {
        this.decompressConcatenated = decompressConcatenated;
        this.pack200Strategy = checkNotNull(pack200Strategy, "pack200Strategy");
        this.pack200Props = checkNotNull(pack200Props, "pack200Props");
        this.bzip2BlockSize = bzip2BlockSize;
        this.xzPreset = xzPreset;
        this.nameDetectionFallback = nameDetectionFallback;
        this.transparentSignatureDetection = transparentSignatureDetection;
    }

    /**
     * Construct a new CompressorStreamFactory2 instance with configuration
     * defaults equivalent to {@link CompressorStreamFactory}.
     * <p/>
     * To create a differently configured factory use the builder by calling
     * {@link CompressorStreamFactory2#builder()}.
     */
    public CompressorStreamFactory2() {
        this(DEFAULT_DECOMPRESS_CONCATENATED,
                DEFAULT_PACK200_STRATEGY,
                DEFAULT_PACK200_PROPS,
                DEFAULT_BZIP2_BLOCK_SIZE,
                DEFAULT_XZ_PRESET,
                DEFAULT_NAME_DETECTION_FALLBACK,
                DEFAULT_TRANSPARENT_SIGNATURE_DETECTION);
    }

    /**
     * Create a new builder for customized CompressorStreamFactory2 instances.
     *
     * @return new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get whether or not the whole file should be decompressed, or just the
     * first stream.
     * <p/>
     * This options applies to BZip2 , GZip, and XZ decompression algorithms.
     *
     * @return If true, decompress until the end of the input; if false, stop
     *         after the first stream and leave the input position to point to the next
     *         byte after the stream.
     */
    public boolean isDecompressConcatenated() {
        return decompressConcatenated;
    }

    /**
     * Get which mode the Pack200 streams will use to wrap input and output;
     * either in memory or using a temporary file.
     *
     * @return strategy used.
     */
    public Pack200Strategy getPack200Strategy() {
        return pack200Strategy;
    }

    /**
     * Get properties for the Pack200 algorithm. See {@link java.util.jar.Pack200
     * }
     * for a list of supported properties.
     *
     * @return properties
     */
    public ImmutableMap<String, String> getPack200Props() {
        return pack200Props;
    }

    /**
     * Get the block-size for the BZip2 compression algorithm.
     *
     * @return blockSize as 100k units.
     */
    public int getBzip2BlockSize() {
        return bzip2BlockSize;
    }

    /**
     * Get the XZ algorithm (LZMA2) compression level.
     * <p/>
     * The presets 0-3 are fast presets with medium compression. The presets 4-6
     * are fairly slow presets with high compression. The default preset is 6.
     * <p/>
     * The presets 7-9 are like the preset 6 but use bigger dictionaries and
     * have higher compressor and decompressor memory requirements. Unless the
     * uncompressed size of the file exceeds 8 MiB, 16 MiB, or 32 MiB, it is
     * waste of memory to use the presets 7, 8, or 9, respectively.
     *
     * @return compression level; in range 0 to 9 (inclusive)
     * @see LZMA2Options
     */
    public int getXzPreset() {
        return xzPreset;
    }

    /**
     * Get whether the factory should fall back to data signature detection,
     * after name detection has been attempted but fails. Otherwise a
     * CompressorException will be thrown.
     *
     * @return true if name detection will fall back to attempting signature detection
     */
    public boolean isNameDetectionFallback() {
        return nameDetectionFallback;
    }

    /**
     * Get whether the factory should fall back to returning a dummy compressor
     * stream when signature detection fails. Otherwise and CompressorException
     * will be thrown. The dummy compressor simply delegates calls to the source
     * stream without alternation.
     *
     * @return true if signature detection will fallback to returning a no-op compressor, false otherwise
     */
    public boolean isTransparentSignatureDetection() {
        return transparentSignatureDetection;
    }

    /**
     * Create an compressor input stream from an input stream, auto-detecting the
     * compressor type from the first few bytes of the stream. The InputStream
     * must support marks, like BufferedInputStream.
     *
     * @param inputStream the input stream
     * @return the compressor input stream
     * @throws CompressorException      if the compressor name is not known
     * @throws IllegalArgumentException if the stream is null or does not
     *                                  support mark
     * @since Commons Compress 1.1
     */
    @Override
    public CompressorInputStream createCompressorInputStream(
            final InputStream inputStream)
            throws CompressorException {
        checkNotNull(inputStream, "inputStream");
        checkArgument(inputStream.markSupported(), "Mark is not supported.");

        final byte[] signature = new byte[12];
        inputStream.mark(signature.length);
        try {
            int signatureLength = inputStream.read(signature);
            inputStream.reset();
            if (BZip2CompressorInputStream.matches(signature, signatureLength)) {
                return createBZip2CompressorInputStream(inputStream);
            } else if (GzipCompressorInputStream.matches(signature, signatureLength)) {
                return createGZipCompressorInputStream(inputStream);
            } else if (XZCompressorInputStream.matches(signature, signatureLength)) {
                return createXZCompressorInputStream(inputStream);
            } else if (Pack200CompressorInputStream.matches(signature, signatureLength)) {
                return createPack200CompressorInputStream(inputStream);
            } else if (transparentSignatureDetection) {
                return createForwardingCompressorInputStream(inputStream);
            }
        } catch (IOException e) {
            throw new CompressorException("Failed to detect Compressor from InputStream.", e);
        }

        throw new CompressorException("No Compressor found for the stream signature.");
    }

    @Override
    public CompressorInputStream createCompressorInputStream(
            final String fileName, final InputStream inputStream)
            throws CompressorException {
        checkNotNull(inputStream, "inputStream");
        checkNotNull(fileName, "fileName");

        try {
            if (GZIP.equalsIgnoreCase(fileName)) {
                return createGZipCompressorInputStream(inputStream);
            } else if (BZIP2.equalsIgnoreCase(fileName)) {
                return createBZip2CompressorInputStream(inputStream);
            } else if (XZ.equalsIgnoreCase(fileName)) {
                return createXZCompressorInputStream(inputStream);
            } else if (PACK200.equalsIgnoreCase(fileName)) {
                return createPack200CompressorInputStream(inputStream);
            } else if (nameDetectionFallback) {
                return createCompressorInputStream(inputStream);
            }
        } catch (IOException e) {
            throw new CompressorException("Could not create CompressorInputStream.", e);
        }
        throw new CompressorException("Compressor: " + fileName + " not found.");
    }

    /**
     * Create an compressor output stream from an compressor name and an input
     * stream.
     *
     * @param name         the compressor name, i.e. "gz", "bzip2", "xz", or "pack200"
     * @param outputStream the output stream
     * @return the compressor output stream
     * @throws CompressorException      if the archiver name is not known
     * @throws IllegalArgumentException if the archiver name or stream is null
     */
    @Override
    public CompressorOutputStream createCompressorOutputStream(
            final String name, final OutputStream outputStream)
            throws CompressorException {
        checkNotNull(outputStream, "outputStream");
        checkNotNull(name, "fileName");
        try {
            if (GZIP.equalsIgnoreCase(name)) {
                return createGZipCompressorOutputStream(outputStream);
            } else if (BZIP2.equalsIgnoreCase(name)) {
                return createBZip2CompressorOutputStream(outputStream);
            } else if (XZ.equalsIgnoreCase(name)) {
                return createXZCompressorOutputStream(outputStream);
            } else if (PACK200.equalsIgnoreCase(name)) {
                return createPack200CompressorOutputStream(outputStream);
            } else if (nameDetectionFallback && transparentSignatureDetection) {
                return createForwardingCompressorOutputStream(outputStream);
            }
        } catch (IOException e) {
            throw new CompressorException("Could not create CompressorOutputStream", e);
        }
        throw new CompressorException("Compressor: " + name + " not found.");
    }

    public GzipCompressorInputStream createGZipCompressorInputStream(
            final InputStream inputStream)
            throws IOException {
        return new GzipCompressorInputStream(checkNotNull(inputStream, "inputStream"),
                decompressConcatenated);
    }

    public BZip2CompressorInputStream createBZip2CompressorInputStream(
            final InputStream inputStream)
            throws IOException {
        return new BZip2CompressorInputStream(checkNotNull(inputStream, "inputStream"),
                decompressConcatenated);
    }

    public XZCompressorInputStream createXZCompressorInputStream(
            final InputStream inputStream)
            throws IOException {
        return new XZCompressorInputStream(checkNotNull(inputStream, "inputStream"),
                decompressConcatenated);
    }

    public Pack200CompressorInputStream createPack200CompressorInputStream(
            final InputStream inputStream)
            throws IOException {
        return new Pack200CompressorInputStream(checkNotNull(inputStream, "inputStream"),
                pack200Strategy, pack200Props);
    }

    public Pack200CompressorInputStream createPack200CompressorInputStream(
            final File inputFile)
            throws IOException {
        return new Pack200CompressorInputStream(checkNotNull(inputFile, "inputFile"),
                pack200Strategy, pack200Props);
    }

    public ForwardingCompressorInputStream createForwardingCompressorInputStream(
            final InputStream inputStream) {
        return new ForwardingCompressorInputStream(checkNotNull(inputStream, "inputStream"));
    }

    public Pack200CompressorOutputStream createPack200CompressorOutputStream(
            OutputStream outputStream)
            throws IOException {
        return new Pack200CompressorOutputStream(checkNotNull(outputStream, "outputStream"),
                pack200Strategy, pack200Props);
    }

    public GzipCompressorOutputStream createGZipCompressorOutputStream(
            OutputStream outputStream)
            throws IOException {
        return new GzipCompressorOutputStream(checkNotNull(outputStream, "outputStream"));
    }

    public BZip2CompressorOutputStream createBZip2CompressorOutputStream(
            OutputStream outputStream)
            throws IOException {
        return new BZip2CompressorOutputStream(checkNotNull(outputStream, "outputStream"),
                bzip2BlockSize);
    }

    public XZCompressorOutputStream createXZCompressorOutputStream(
            OutputStream outputStream)
            throws IOException {
        return new XZCompressorOutputStream(checkNotNull(outputStream, "outputStream"),
                xzPreset);
    }

    public ForwardingCompressorOutputStream createForwardingCompressorOutputStream(
            OutputStream outputStream) {
        return new ForwardingCompressorOutputStream(checkNotNull(outputStream, "outputStream"));
    }

    /**
     * Builder class for creating non-default CompressorStreamFactory2
     * instances.
     */
    @NotThreadSafe
    @Nonnull
    public static class Builder {

        private boolean decompressConcatenated = DEFAULT_DECOMPRESS_CONCATENATED;
        private Pack200Strategy pack200Strategy = DEFAULT_PACK200_STRATEGY;
        private final ImmutableMap.Builder<String, String> pack200Props = ImmutableMap.builder();
        private int bzip2BlockSize = DEFAULT_BZIP2_BLOCK_SIZE;
        private int xzPreset = DEFAULT_XZ_PRESET;
        private boolean nameDetectionFallback = DEFAULT_NAME_DETECTION_FALLBACK;
        private boolean transparentSignatureDetection = DEFAULT_TRANSPARENT_SIGNATURE_DETECTION;

        /**
         * Instantiated using {@link CompressorStreamFactory2#builder() }
         */
        private Builder() {
        }

        /**
         * Set whether or not the whole file should be decompressed, or just the
         * first stream.
         * <p/>
         * This options applies to BZip2 , GZip, and XZ decompression
         * algorithms.
         *
         * @param decompressConcatenated If true, decompress until the end of
         *                               the input; if false, stop after the first stream and leave the input
         *                               position to point to the next byte after the stream.
         */
        public Builder setDecompressConcatenated(boolean decompressConcatenated) {
            this.decompressConcatenated = decompressConcatenated;
            return this;
        }

        /**
         * Set which modes the Pack200 streams can use to wrap input and output;
         * either in memory or using a temporary file.
         *
         * @param pack200Strategy strategy to use.
         */
        public Builder setPack200Strategy(Pack200Strategy pack200Strategy) {
            this.pack200Strategy = checkNotNull(pack200Strategy, "pack200Strategy");
            return this;
        }

        /**
         * Set a properties for the Pack200 algorithm. See {@link java.util.jar.Pack200
         * } for a list of supported properties.
         *
         * @param key   property name
         * @param value property value
         */
        public Builder putPack200Props(String key, String value) {
            pack200Props.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
            return this;
        }

        /**
         * Set a properties for the Pack200 algorithm. See {@link java.util.jar.Pack200
         * } for a list of supported properties.
         *
         * @param prop property key/value pair
         */
        public Builder putPack200Props(Map.Entry<String, String> prop) {
            pack200Props.put(checkNotNull(prop, "prop"));
            return this;
        }

        /**
         * Set a bunch of properties for the Pack200 algorithm. See {@link java.util.jar.Pack200
         * }
         * for a list of supported properties.
         *
         * @param props properties to set
         */
        public Builder putAllPack200Props(Map<String, String> props) {
            pack200Props.putAll(checkNotNull(props, "props"));
            return this;
        }

        /**
         * Set the block-size for the BZip2 compression algorithm.
         *
         * @param bzip2BlockSize the blockSize as 100k units.
         */
        public Builder setBzip2BlockSize(int bzip2BlockSize) {
            checkArgument(bzip2BlockSize <= BZip2CompressorOutputStream.MAX_BLOCKSIZE,
                    "Argument bzip2BlockSize (%s) is greater than maximum value %s",
                    bzip2BlockSize, BZip2CompressorOutputStream.MAX_BLOCKSIZE);
            checkArgument(bzip2BlockSize >= BZip2CompressorOutputStream.MIN_BLOCKSIZE,
                    "Argument bzip2BlockSize (%s) is less than minimum value %s",
                    bzip2BlockSize, BZip2CompressorOutputStream.MIN_BLOCKSIZE);
            this.bzip2BlockSize = bzip2BlockSize;
            return this;
        }

        /**
         * Set the XZ algorithm (LZMA2) compression level.
         * <p/>
         * The presets 0-3 are fast presets with medium compression. The presets
         * 4-6 are fairly slow presets with high compression. The default preset
         * is 6.
         * <p/>
         * The presets 7-9 are like the preset 6 but use bigger dictionaries and
         * have higher compressor and decompressor memory requirements. Unless
         * the uncompressed size of the file exceeds 8 MiB, 16 MiB, or 32 MiB,
         * it is waste of memory to use the presets 7, 8, or 9, respectively.
         *
         * @param xzPreset compression level; in range 0 to 9 (inclusive)
         * @see LZMA2Options
         */
        public Builder setXzPreset(int xzPreset) {
            checkArgument(xzPreset <= LZMA2Options.PRESET_MAX,
                    "Argument xzPreset (%s) is greater than maximum value %s",
                    xzPreset, LZMA2Options.PRESET_MAX);
            checkArgument(xzPreset >= LZMA2Options.PRESET_MIN,
                    "Argument xzPreset (%s) is less than maximum value %s",
                    xzPreset, LZMA2Options.PRESET_MIN);
            this.xzPreset = xzPreset;
            return this;
        }

        /**
         * Set whether the factory should fall back to data signature detection,
         * after file name detection has been attempted but fails. Otherwise a
         * CompressorException will be thrown.
         *
         * @param nameDetectionFallback whether name detection should fall back to attempting signature detection
         */
        public Builder setNameDetectionFallback(boolean nameDetectionFallback) {
            this.nameDetectionFallback = nameDetectionFallback;
            return this;
        }

        /**
         * Set whether the factory should fall back to returning a dummy
         * compressor stream when signature detection fails. Otherwise and
         * CompressorException will be thrown. The dummy compressor simply
         * delegates calls to the source stream without alternation.
         *
         * @param transparentSignatureDetection whether signature detection should fall back to returning a
         *                                      forwarding (no-op) compressor.
         */
        public Builder setTransparentSignatureDetection(boolean transparentSignatureDetection) {
            this.transparentSignatureDetection = transparentSignatureDetection;
            return this;
        }

        /**
         * Construct a new CompressorStreamFactory2 instance using the current
         * builder configuration.
         * <p/>
         * Note that this method can be called repeatedly (optionally making
         * additional mutations to the builder in between) to create multiple
         * independent CompressorStreamFactory2 instances.
         *
         * @return new CompressorStreamFactory2 instance
         */
        public CompressorStreamFactory2 build() {
            return new CompressorStreamFactory2(decompressConcatenated,
                    pack200Strategy,
                    pack200Props.build(),
                    bzip2BlockSize,
                    xzPreset,
                    nameDetectionFallback,
                    transparentSignatureDetection);
        }
    }
}
