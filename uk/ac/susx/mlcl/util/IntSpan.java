/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
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
 * @author Hamish Morgan
 */
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

        protected final IntSpan readResolve() {
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
