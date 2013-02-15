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
package uk.ac.susx.mlcl.featureextraction;

import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;
import uk.ac.susx.mlcl.util.IntSpan;
import uk.ac.susx.mlcl.featureextraction.featurefactory.FeatureFactory;
import uk.ac.susx.mlcl.featureextraction.featurefunction.FeatureFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;

/**
 * Class representing an individual sentence holding all relevant parameters and
 * methods for manipulation.
 *
 * XXX (Hamish): Serialization won't work because IndexToken objects are not serializable. In addition Serialization of
 *               this class can potentially fail when the collection holding keys is not serializable.
 *
 * @author Simon Wibberley
 */
public class Sentence extends ArrayList<Token> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_TOKEN_SEPARATOR = "+"; // Seperates token Strings if there is more than one in the Token object (i.e. a chunk represented as one Token)

    private static final boolean DEFAULT_SORTED_KEYS = false; 

    private final String tokenSeparator;

    private final Collection<IndexToken<?>> keys; // The keys within the sentence - representing indivual tokens or a collection of them (e.g. a chunk)

    private final boolean sortedKeys;

    public Sentence() {
        this(DEFAULT_TOKEN_SEPARATOR, DEFAULT_SORTED_KEYS);
    }

    public Sentence(String tokenSeparator) {
        this(tokenSeparator, DEFAULT_SORTED_KEYS);
    }

    public Sentence(boolean sortedKeys) {
        this(DEFAULT_TOKEN_SEPARATOR, sortedKeys);
    }

    public Sentence(String tokenSeparator, boolean sortedKeys) {
        super();
        this.tokenSeparator = tokenSeparator;
        this.sortedKeys = sortedKeys;
        if (sortedKeys) {
            keys = new TreeSet<IndexToken<?>>(IndexToken.END_ORDER);
        } else {
            keys = new ArrayList<IndexToken<?>>();
        }
    }

    public boolean isSortedKeys() {
        return sortedKeys;
    }

    public String getTokenSeparator() {
        return tokenSeparator;
    }

    /**
     * @return Returns all the keys within the sentence. 
     */
    public Collection<IndexToken<?>> getKeys() {
        return new ArrayList<IndexToken<?>>(keys);
    }

    public void addKey(IndexToken<?> key) {
        keys.add(key);
    }

    public void addAllKey(Collection<? extends IndexToken<?>> ks) {
        keys.addAll(ks);
    }
    
    /**
     * Returns the string representation of a key, will either be a single token
     * or a concatenation of tokens from a given span (i.e. chunk)
     * Change Log: Addition to allow tokens with a given PoS tag to be excluded
     * @param exPos PoS tag of tokens to exclude (if any)
     * @param key Key indicating span of sentence to return
     * @return String representation of a key
     */
    public CharSequence getKeyString(String exPos, IndexToken<?> key) {

        IntSpan span = key.getSpan();

        StringBuilder sb = new StringBuilder();

        for (int i = span.left; i <= span.right; ++i) {
            if(exPos == null || !exPos.equals(this.get(i).getAnnotation(Annotations.PoSAnnotation.class))){
                sb.append(this.get(i).getAnnotation((Class<? extends Annotation>)key.getKeyType()));
                if (i < span.right) {
                    sb.append(this.getTokenSeparator());
                }
            }
        }
        return sb;
    }

    public Collection<IndexToken<?>> getShortestSegmentationPathKeys() {

        IndexToken<?>[] path = new IndexToken<?>[size()];
        int[] shortest = new int[size()];
        Arrays.fill(path, null);
        Arrays.fill(shortest, Integer.MAX_VALUE);

        Iterator<IndexToken<?>> itr = keys.iterator();

        if (!itr.hasNext()) {
            return null;
        }

        for (IndexToken<?> key : keys) {

            IntSpan span = key.getSpan();

            if (shortest[span.left] + 1 < shortest[span.right]) {
                shortest[span.right] = shortest[span.left] + 1;
                path[span.right] = key;
            }
        }

        Collection<IndexToken<?>> shortestPath = getPath(path);


        return shortestPath;

    }

    private Collection<IndexToken<?>> getPath(IndexToken<?>[] keys) {

        Collection<IndexToken<?>> path = new ArrayList<IndexToken<?>>();
        getPath(keys, keys.length - 1, path);
        return path;

    }

    private void getPath(IndexToken<?>[] keys, int i,
                         Collection<IndexToken<?>> path) {
        if (i < 0) {
            return;
        }

        IndexToken<?> key = keys[i];
        if (key == null) {
            getPath(keys, i - 1, path);
            return;
        }

        path.add(key);
        int begin = key.getSpan().left - 1;
        getPath(keys, begin, path);

    }
}
