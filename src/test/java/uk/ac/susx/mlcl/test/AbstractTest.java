/*
 * Copyright (c) 2010, Hamish Morgan.
 * All Rights Reserved.
 */
package uk.ac.susx.mlcl.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * AbstractTest is a useful superclass the unit test classes. Currently it's main function it that it will automatically
 * print the test class and method name, for each test that is run. In addition it provides a number of static utility
 * methods.
 *
 * @author Hamish Morgan
 */
public abstract class AbstractTest {

    protected AbstractTest() {

    }

    /**
     *
     */
    @Rule()
    public final TestName testName = new TestName();

    @Before()
    public final void _printTestMethod() throws SecurityException, NoSuchMethodException {
        System.out.println(MessageFormat.format(
                "Running test: {0}#{1}",
                this.getClass().getName(), testName.getMethodName()));
    }

    /**
     * Attempt to create a copy of the given object using object serialization (if possible.)
     *
     * @param obj
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    protected static <T> T cloneWithSerialization(final T obj) {

        ObjectOutputStream objectsOut = null;
        ObjectInputStream ois = null;
        try {
            try {
                final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                objectsOut = new ObjectOutputStream(bytesOut);

                objectsOut.writeObject(obj);
                objectsOut.flush();

                final byte[] bytes = bytesOut.toByteArray();

                ois = new ObjectInputStream(new ByteArrayInputStream(bytes));

                return (T) ois.readObject();
            } finally {
                if (objectsOut != null) {
                    objectsOut.close();
                }
                if (ois != null) {
                    ois.close();
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new AssertionError(ex);
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * Attempt to create a copy of the given object using Object.clone().
     *
     * @param obj
     * @param <T>
     * @return
     */
    protected static <T> T clone(T obj) {
        try {

            assertTrue("doesn't implement Cloneable", obj instanceof Cloneable);

            final Method cloneMethod = obj.getClass().getMethod("clone");

            assertTrue("clone() is not public", Modifier.isPublic(cloneMethod.getModifiers()));
            assertFalse("clone() is abstract", Modifier.isAbstract(cloneMethod.getModifiers()));
            assertFalse("clone() is static", Modifier.isStatic(cloneMethod.getModifiers()));

            final Object result = cloneMethod.invoke(obj);

            assertEquals("cloned instance class different", result.getClass(), obj.getClass());
            assertEquals("cloned object not equal to original", obj, result);

            return (T) result;

        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            throw new AssertionError(e);
        }

    }

    protected static void assertExhaustedIterator(Iterator<?> it) {
        try {
            it.next();
            fail("Expected iterator to be exhausted by next() succeeded.");
        } catch (NoSuchElementException e) {
            // this is supposed to happen
            assertNotNull(e);
        }
    }

    /**
     * Creates a new pseudo-random number generator object, displaying the seed used so it can be re-instantiated
     * if a particular test needs to be repeated.
     *
     * @return  the new Random instance.
     */
    protected static Random newRandom() {
        Random rand = new Random();
        final int seed = rand.nextInt();
        System.out.println(" > random seed = " + seed);
        rand = new Random(seed);
        return rand;
    }
}
