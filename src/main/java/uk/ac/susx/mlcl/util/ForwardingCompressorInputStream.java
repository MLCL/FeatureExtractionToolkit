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

import static com.google.common.base.Preconditions.*;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * ForwardingCompressorInputStream is an implementation of
 * {@link CompressorInputStream} that doesn't really decompress anything.
 * Instead it wraps the given InputStream and delegates all calls to it,
 * returning the result unaltered.
 *
 * The purpose of this class is to improve compression detection, so an input
 * stream can automatically be adapted to decompress it's contents if a known
 * compression algorithm is used. If compression is not detected then the stream
 * is simply adapted to match the {@link CompressorStreamFactory} interface.
 *
 * @author Hamish Morgan
 */
public class ForwardingCompressorInputStream extends CompressorInputStream {

    private final InputStream inputStream;

    /**
     * Construct a new instance, delegating to the given InputStream
     *
     * @param inputStream the InputStream to which all method invocations shall be forwarded.
     */
    public ForwardingCompressorInputStream(final InputStream inputStream) {
        this.inputStream = checkNotNull(inputStream, "inputStream");
    }

    @Override
    public int read() throws IOException {
        int n = inputStream.read();
        count(n);
        return n;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int n = inputStream.read(b);
        count(n);
        return n;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = inputStream.read(b, off, len);
        count(n);
        return n;
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readLimit) {
        inputStream.mark(readLimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }
}
