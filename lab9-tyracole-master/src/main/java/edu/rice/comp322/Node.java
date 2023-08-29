package edu.rice.comp322;

import lombok.Getter;
import lombok.Setter;

import static edu.rice.hj.Module2.isolated;
import static edu.rice.hj.Module2.isolatedWithReturn;

/**
 * A class that represents a node in a doubly linked list.
 */
public class Node {
    int value;
    /**
     * Links to the previous and the next element in the list.
     */
    @Getter @Setter private Node prev, next;

    /**
     * Constructor with only the value. Construct a linked list with a single element.
     * @param value
     */
    Node(int value) {
        this.value = value;
        prev = this;
        next = this;
    }

    /**
     * Constructor with all three fields. Construct an element of a linked list.
     * @param value
     * @param prev
     * @param next
     */
    Node(int value, Node prev, Node next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }

}
