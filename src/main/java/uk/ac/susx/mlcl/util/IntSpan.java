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

import java.util.Comparator;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import static java.lang.Math.*;

/**
 * Am IntSpan object represents a bound on a subsequence of items, such as 
 * characters in a string. An IntSpan specifies the offsets of the first (left most)
 * and last (right most) items inclusively.
 * 
 * IntSpan objects are immutable, and their fields are final --- hence the rather 
 * complicated serialization code.
 * 
 * @author Hamish Morgan &lt;hamish.morgan@sussex.ac.uk&gt;
å */
public final class IntSpan implements Serializable, Comparable<IntSpan> {

    private static final long serialVersionUID = 1L;

    public final int left;

    public final int right;

    public IntSpan(final int left, final int right) {
        if (left > right)
            throw new IllegalArgumentException("right < left");
        this.left = left;
        this.right = right;
    }

    public IntSpan(final IntSpan other) {
        this(other.left, other.right);
    }

    public int length() {
        return right - left + 1;
    }

    public boolean isEmpty() {
        return left == right;
    }

    public boolean intersects(final int index) {
        return left <= index && right >= index;
    }

    public boolean intersects(final IntSpan other) {
        return intersects(other.left, other.right);
    }

    public boolean intersects(final int left, final int right) {
        return !(this.right < left || this.left > right);
    }

    public IntSpan add(final IntSpan other) {
        return add(other.left, other.right);
    }

    public IntSpan add(final int left, final int right) {
        return new IntSpan(this.left + left, this.right + right);
    }

    public IntSpan sub(final IntSpan other) {
        return sub(other.left, other.right);
    }

    public IntSpan sub(final int left, final int right) {
        return new IntSpan(this.left - left, this.right - right);
    }

    public IntSpan clamp(final IntSpan bounds) {
        return clamp(bounds.left, bounds.right);
    }

    public IntSpan clamp(final int min, final int max) {
        return new IntSpan(max(left, min), min(right, max));
    }

    public IntSpan intersection(int left, int right) {
        return new IntSpan(max(this.left, left), min(this.right, right));
    }

    public IntSpan intersection(IntSpan other) {
        return intersection(other.left, other.right);
    }

    public IntSpan union(int left, int right) {
        return new IntSpan(min(this.left, left), max(this.right, right));
    }

    public IntSpan union(IntSpan other) {
        return union(other.left, other.right);
    }

    @Override
    public String toString() {
        return "(" + left + ',' + right + ')';
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return equals((IntSpan) obj);
    }

    public boolean equals(final IntSpan other) {
        return this.left == other.left && this.right == other.right;
    }
    
    /**
     * Used to check for features neither being in the same span or being equal to 
     * the item at other
     * @param other The location of the other item 
     * @return true if intersects or equals
     */
    public boolean intersectsOrequals(int other){
        return (this.intersects(other) && !unitSpan())
                            || (this.left == other && unitSpan());
    }
    
    /**
     * @return True if spans only one item
     */
    public boolean unitSpan(){
        return left == right;
    }

    @Override
    public int hashCode() {
        return 97 * this.left + this.right;
    }

    @Override
    protected IntSpan clone() {
        return new IntSpan(this);
    }

    protected final Object writeReplace() {
        return new SpanSerializer(this);
    }

    @Override
    public int compareTo(final IntSpan other) {
        return LEFT_FIRST_COMPARATOR.compare(this, other);
    }

    private static final class SpanSerializer implements Externalizable {

        private static final long serialVersionUID = 1;

        private IntSpan span;

        public SpanSerializer() {
        }

        public SpanSerializer(final IntSpan span) {
            this.span = span;
        }

        @Override
        public final void writeExternal(final ObjectOutput out)
                throws IOException {
            out.writeInt(span.left);
            out.writeInt(span.right);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final void readExternal(final ObjectInput in)
                throws IOException, ClassNotFoundException {
            int left = in.readInt();
            int right = in.readInt();
            this.span = new IntSpan(left, right);
        }

        protected final Object readResolve() {
            return span;
        }

    }

    public static final Comparator<IntSpan> LEFT_FIRST_COMPARATOR =
            new Comparator<IntSpan>() {

                @Override
                public int compare(final IntSpan a, final IntSpan b) {
                    final int leftComparison = a.left - b.left;
                    return leftComparison != 0 ? leftComparison
                           : a.right - b.right;
                }

            };

    public static final Comparator<IntSpan> RIGHT_FIRST_COMPARATOR =
            new Comparator<IntSpan>() {

                @Override
                public int compare(final IntSpan a, final IntSpan b) {
                    final int rightComparison = a.right - b.right;
                    return rightComparison != 0 ? rightComparison
                           : a.left - b.left;
                }

            };

}
