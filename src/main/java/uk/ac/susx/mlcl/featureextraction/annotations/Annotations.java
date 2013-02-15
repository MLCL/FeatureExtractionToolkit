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
package uk.ac.susx.mlcl.featureextraction.annotations;

import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;
import uk.ac.susx.mlcl.util.IntSpan;
//import uk.ac.susx.mlcl.util.Tuple;

/**
 * Static utility class for handling Annotation objects.
 *
 * @author Simon Wibberley
 */
public class Annotations {

    private static final Logger LOG = Logger.getLogger(
            Annotations.class.getName());

    private Annotations() {
        // Static utility class is non-instantiable
    }

    public static <T> Annotation<T> createAnnotation(Class<? extends Annotation<T>> cls,
                                                     T value) {
        Annotation<T> a = null;
        try {
            a = cls.newInstance();
            a.setValue(value);
        } catch (InstantiationException e) {
            LOG.log(Level.SEVERE, null, e);
        } catch (IllegalAccessException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return a;
    }

    public static class CharAnnotation extends AbstractAnnotation<Character> {
    }

    public static class NgramSpanAnnotation extends AbstractAnnotation<List<IntSpan>> {

        @Override
        public void addToCollection(
                Collection<? super String> list, String prefix)
                throws OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }

    }

    public static class DependencyHeadListAnnotation extends AbstractDictionaryAnnotation<HashMap<String,ArrayList<String>>> {
    }

    public static class DependencyListAnnotation extends AbstractDictionaryAnnotation<HashMap<String,ArrayList<String>>> {
    }

    public static class DependencyAnnotation extends AbstractAnnotation<String>{
    }

    public static class NgramAnnotation extends AbstractListAnnotation<CharSequence> {
    }

    public static class ChunkSpanAnnotation extends AbstractSpanAnnotation {
    }

    public static class IndexAnnotation extends AbstractAnnotation<Integer> {
    }

    public static class SentenceLengthAnnotation extends AbstractAnnotation<Integer> {
    }

    public static class TokenAnnotation extends AbstractAnnotation<CharSequence> {
    }

    public static class OriginalTokenAnnotation extends AbstractAnnotation<CharSequence> {
    }

    public static class PoSAnnotation extends AbstractAnnotation<String> {
    }

    public static class ChunkAnnotation extends AbstractAnnotation<CharSequence> {
    }

    public static class ChunkTagAnnotation extends AbstractAnnotation<String> {
    }

    // NEW ADDITIONS *************************************************************

    public static class LeftNounAnnotation extends AbstractAnnotation<String> {
    }

    public static class RightNounAnnotation extends AbstractAnnotation<String> {
    }

    public static class LeftAdjectiveAnnotation extends AbstractAnnotation<String> {
    }

    public static class RightAdjectiveAnnotation extends AbstractAnnotation<String> {
    }

    public static class LeftVerbHeadAnnotation extends AbstractAnnotation<String> {
    }

    public static class RightVerbHeadAnnotation extends AbstractAnnotation<String> {
    }

    public static class LeftPrepositionAnnotation extends AbstractAnnotation<String> {
    }

    public static class RightPrepositionAnnotation extends AbstractAnnotation<String> {
    }

    public static class HeadNounAnnotation extends AbstractAnnotation<String> {
    }

    public static class OntologyEntryAnnotation extends AbstractAnnotation<String>{
    }

    public static class PoSTagLeftAnnotation extends AbstractAnnotation<String>{
    }

    public static class PoSTagRightAnnotation extends AbstractAnnotation<String>{
    }

    public static class DeterminerLeftAnnotation extends AbstractAnnotation<String>{
    }

    public static class NounGroupNounAnnotation extends AbstractAnnotation<String>{
    }
    //****************************************************************************
}
