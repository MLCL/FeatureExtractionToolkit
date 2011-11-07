/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

import uk.ac.susx.mlcl.util.IntSpan;
import uk.ac.susx.mlcl.featureextraction.features.FeatureFactory;
import uk.ac.susx.mlcl.featureextraction.features.FeatureFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * 
 * @author Simon Wibberley
 */
public class Sentence extends ArrayList<Token> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_TOKEN_SEPARATOR = "+";

    private static final boolean DEFAULT_SORTED_KEYS = false;

    private final String tokenSeparator;

    private final Collection<IndexToken<?>> keys;

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
//
//    public void setTokenSeparator(String ts) {
//        tokenSeparator = ts;
//    }

    public Collection<IndexToken<?>> getKeys() {
        return new ArrayList<IndexToken<?>>(keys);
    }

    public void addKey(IndexToken<?> key) {
        keys.add(key);
    }

    public void addAllKey(Collection<? extends IndexToken<?>> ks) {
        keys.addAll(ks);
    }

    public CharSequence getKeyString(IndexToken<?> key) {

        IntSpan span = key.getSpan();

        StringBuilder sb = new StringBuilder();

        for (int i = span.left; i <= span.right; ++i) {
            sb.append(get(i).getAnnotation(key.getKeyType()));
            if (i < span.right) {
                sb.append(tokenSeparator);
            }
        }

        return sb;

    }

    public void applyFeatureFactory(FeatureFactory featureFactory) {
        Collection<FeatureFunction> fns = featureFactory.getAllFeatures();

        for (IndexToken<?> key : keys) {
            CharSequence keyStr = getKeyString(key);
            //StringBuilder featStr = new StringBuilder();
            List<CharSequence> featureList = new ArrayList<CharSequence>();
            key.setKey(keyStr);
            //System.err.println(keyStr);

            for (FeatureFunction f : fns) {

                Collection<String> features = f.extractFeatures(this, key);
                featureList.addAll(features);
                /*
                for (String s : features) {
                
                System.err.println(s);
                //featStr.append(s);
                //featStr.append(featureSeparator);
                }
                 */

            }

            key.setFeatures(featureList);
            //System.err.print(featStr.toString());
            //System.err.println("\n---------------------------");
        }

    }
//
//    public void useShortestSegmentationPathKeys() {
//
//        Collection<IndexToken<?>> shortestPath = getShortestSegmentationPathKeys();
//
//        keys = new TreeSet<IndexToken<?>>(shortestPath);
//
//    }

    public Collection<IndexToken<?>> getShortestSegmentationPathKeys() {

        IndexToken<?>[] path = new IndexToken<?>[size()];
        int[] shortest = new int[size()];
        Arrays.fill(path, null);
        Arrays.fill(shortest, Integer.MAX_VALUE);

        Iterator<IndexToken<?>> itr = keys.iterator();

        if (!itr.hasNext()) {
            return null;
        }
        //IndexToken<?> key = itr.next();


        for (IndexToken<?> key : keys) {

            IntSpan span = key.getSpan();

//            int begin = span.left;
//            int end = span.right;
//			int len = end-begin;

            if (shortest[span.left] + 1 < shortest[span.right]) {
                shortest[span.right] = shortest[span.left] + 1;
                path[span.right] = key;
            }
        }



        /*
        for(IndexToken<?> key : path) {
        System.err.println(key);
        }
         */

        Collection<IndexToken<?>> shortestPath = getPath(path);

        /*
        for(IndexToken<?> key : shortestPath) {
        System.err.println(key);
        }
         */

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
        //System.err.println(begin + " : " + i);
        getPath(keys, begin, path);

    }

//    public static void main(String[] args) {
//
//        Sentence s = new Sentence(true);
//        s.addKey(new IndexToken<CharSequence>(new int[]{0, 1}, TokenAnnotation.class));
//        //s.addKey(new IndexToken<String>(new int[]{1,2}, TokenAnnotation.class));
//        s.addKey(new IndexToken<CharSequence>(new int[]{2, 3}, TokenAnnotation.class));
//        s.addKey(new IndexToken<CharSequence>(new int[]{3, 4}, TokenAnnotation.class));
//        s.addKey(new IndexToken<CharSequence>(new int[]{4, 5}, TokenAnnotation.class));
//        s.addKey(new IndexToken<CharSequence>(new int[]{5, 6}, TokenAnnotation.class));
//
//        //s.addKey(new IndexToken<String>(new int[]{2,4}, TokenAnnotation.class));
//        //s.addKey(new IndexToken<String>(new int[]{1,4}, TokenAnnotation.class));
//		/*
//        s.addKey(new IndexToken<String>(new int[]{0,1}, TokenAnnotation.class));
//        s.addKey(new IndexToken<String>(new int[]{1,2}, TokenAnnotation.class));
//        
//        s.addKey(new IndexToken<String>(new int[]{4,6}, TokenAnnotation.class));
//        
//        
//        s.addKey(new IndexToken<String>(new int[]{0,4}, TokenAnnotation.class));
//        s.addKey(new IndexToken<String>(new int[]{4,5}, TokenAnnotation.class));
//        s.addKey(new IndexToken<String>(new int[]{5,6}, TokenAnnotation.class));
//         */
//        s.addKey(new IndexToken<CharSequence>(new int[]{4, 6}, TokenAnnotation.class));
//
//        s.add(new Token());
//        s.add(new Token());
//        s.add(new Token());
//        s.add(new Token());
//        s.add(new Token());
//        s.add(new Token());
//
//        Collection<IndexToken<?>> path = s.getShortestSegmentationPathKeys();
//
//
//        for (IndexToken<?> key : path) {
//            System.err.println(key);
//        }
//
//
//
//    }
}
