/*
 * Copyright (c) 2013, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.util;

import static com.google.common.base.Preconditions.*;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * ForwardingCompressorOutputStream is an implementation of {@link CompressorOutputStream} that doesn't
 * really compress anything. Instead it wraps the given OutputStream and delegates all calls to it.
 *
 * The purpose of this class is to improve compression detection, so an output stream can
 * automatically be adapted to compress data if a known compression algorithm is used. If
 * compression is not detected then the stream is simply adapted to match the
 * {@link CompressorStreamFactory} interface.
 *
 * @author Hamish Morgan
 */
public class ForwardingCompressorOutputStream extends CompressorOutputStream {

    private final OutputStream delegate;

    /**
     * Construct a new ForwardingCompressorOutputStream delegating all calls to the given OutputStream
     *
     * @param delegate the OutputStream to which all method invocations shall be forwarded.
     */
    public ForwardingCompressorOutputStream(final OutputStream delegate) {
        this.delegate = checkNotNull(delegate, "delegate");
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
