package edu.rice.comp322;

import junit.framework.TestCase;

import java.util.Random;

import static edu.rice.hj.Module0.launchHabaneroApp;

/**
 * Unit test for simple App.
 */
public class Lab9BaselineTest extends TestCase {

    private static final int DEFAULT_N = 1000;

    protected static DoubleLinkedList initializeList(final int n, final int seed) {
        final Random myRand = new Random(seed);
        var list = new DoubleLinkedList();
        for (int i = 0; i < n; i++) {
            list.insert(myRand.nextInt(n));
        }
        return list;
    }

    public void testEmptyList(){
        assertTrue((new DoubleLinkedList()).wellFormed());
    }

    public void testSimpleList(){
        var list1 = new DoubleLinkedList();
        list1.insert(5);
        list1.insert(3);
        list1.insert(4);
        var list2 = new DoubleLinkedList();
        list2.insert(3);
        list2.insert(4);
        list2.insert(5);
        assertTrue(list1.wellFormed());
        assertTrue(list2.wellFormed());
        assertEquals(list1, list2);
    }

    public void testSimpleListContains() {
        var list1 = new DoubleLinkedList();
        list1.insert(5);
        list1.insert(3);
        list1.insert(4);
        assertTrue(list1.contains(5));
        assertTrue(list1.contains(4));
        assertTrue(list1.contains(3));
        assertFalse(list1.contains(6));
    }

    public void testSimpleListRootRemoval() {
        var list1 = new DoubleLinkedList();
        list1.insert(5);
        list1.insert(3);
        list1.insert(4);
        list1.remove(3);
        assertTrue(list1.wellFormed());
        assertTrue(list1.contains(5));
        assertTrue(list1.contains(4));
        assertFalse(list1.contains(3));
    }

    public void testSimpleListRemoval() {
        var list1 = new DoubleLinkedList();
        list1.insert(5);
        list1.insert(3);
        list1.insert(4);
        list1.remove(4);
        assertTrue(list1.wellFormed());
        assertTrue(list1.contains(5));
        assertTrue(list1.contains(3));
        assertFalse(list1.contains(4));
    }

}
