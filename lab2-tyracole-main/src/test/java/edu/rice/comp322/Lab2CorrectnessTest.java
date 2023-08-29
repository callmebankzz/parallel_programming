package edu.rice.comp322;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static edu.rice.comp322.Tree.makeNode;


public class Lab2CorrectnessTest {

    @Test
    public void testMap() {
        var n1 = makeNode("Hello", GList.empty());
        var n2 = makeNode("Rice", GList.empty());
        var n3 = makeNode("University", GList.empty());
        var t1 = makeNode("First Tree", GList.of(n1, n2, n3));
        var t2 = makeNode("Second Tree", GList.of(t1, n1, n2));
        var t3 = makeNode("Third Tree", GList.of(t1, t2, n3));

        var resultTree = t3.map(String::length);
        // make sure the length of the root of the tree ("Third TreeSolution") is 10
        assertEquals(10, resultTree.value());
        // make sure the length of the first child ("First TreeSolution") is 10
        assertEquals(10, resultTree.children().head().value());
        // make sure the length of the second child ("Second TreeSolution") is 11
        assertEquals(11, resultTree.children().tail().head().value());
        // make sure the length of the third child ("University") is 10
        assertEquals(10, resultTree.children().tail().tail().head().value());
        // make sure the length of the first grandchild ("Hello") is 5
        assertEquals(5, resultTree.children().head().children().head().value());
    }

    @Test
    public void testFilter() {
        var n1 = makeNode("Hello", GList.empty());
        var n2 = makeNode("Rice", GList.empty());
        var n3 = makeNode("University", GList.empty());
        var t1 = makeNode("First Tree", GList.of(n1, n2, n3));
        var t2 = makeNode("Second Tree", GList.of(t1, n1, n2));
        var t3 = makeNode("Third Tree", GList.of(t1, t2, n3));

        // Keep only the nodes from t3 whose values are strings with length less than 4.
        // This should result in an empty tree
        assertTrue(t3.filter(s -> s.length() < 4).isEmpty());

        // Keep only the nodes from t3 whose value is "Third Tree". This should result in a
        // tree with a single node
        var r1 = t3.filter(s -> s.equals("Third Tree"));
        assertEquals("Third Tree", r1.value());
        assertTrue(r1.children().isEmpty());

        // Keep only the nodes from t3 whose values are strings with length less than 6.
        // This should only leave "Hello" and "Rice" nodes, with "Hello" as the root
        var r2 = t3.filter(s -> s.length() < 6);
        // The root of the tree should be "Hello"
        assertEquals("Hello", r2.value());
        // There should be two children
        assertEquals(2, r2.children().length());
        // The children should be "Rice" and "Hello"
        assertEquals("Rice", r2.children().head().value());
        assertEquals("Hello", r2.children().tail().head().value());
        // The second child should have 3 children
        assertEquals(3, r2.children().tail().head().children().length());
        // And they should be "Rice", "Hello", "Rice". Note the shorter way of testing that by comparing
        // two lists.
        assertEquals(GList.of("Rice", "Hello", "Rice"), r2.children().tail().head().children().map(Tree::value));

    }

}
