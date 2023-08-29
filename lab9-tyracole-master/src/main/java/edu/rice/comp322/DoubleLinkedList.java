package edu.rice.comp322;

import static edu.rice.hj.Module2.*;

/**
 * A double linked list.
 */
public class DoubleLinkedList {
    Node root;
    /**
     * Check if the double linked list is well-formed. All the links need to be pointing at the correct elements,
     * all the elements need to be in sorted order.
     * @return true if the list is well-formed.
     */
    public boolean wellFormed() {
        if (root == null) return true;
        Node current = root;
        Node next = current.getNext();
        while (next != root) {
            if (current.value > next.value) return false;
            if (current.getNext() != next || next.getPrev() != current) return false;
            current = next;
            next = next.getNext();
        }
        // Finally, check if the last element is properly linked with the first one
        return (current.getNext() == next && next.getPrev() == current);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleLinkedList that = (DoubleLinkedList) o;
        if (this.root == null) return (that.root == null);
        if (this.root.value != that.root.value) return false;
        Node thisCurrent = this.root.getNext();
        Node thatCurrent = that.root.getNext();
        while(thisCurrent != this.root) {
            if (thisCurrent.value != thatCurrent.value) return false;
            thisCurrent = thisCurrent.getNext();
            thatCurrent = thatCurrent.getNext();
        }
        return true;
    }


    @Override
    public String toString() {
        if (root == null) return "[]";
        Node current = root.getNext();
        StringBuilder result = new StringBuilder("[");
        result.append(root.value);
        while (current != root) {
            result.append(", ").append(current.value);
            current = current.getNext();
        }
        return result + "]";
    }

    /**
     * A single-threaded implementation of list insertion. This is NOT thread safe!
     * @param value the new value going into the list
     */
    public void insert(int value) {
        Node element = new Node(value);
        // If the list is empty, then the element becomes the new list
        if (root == null) {
            // Make sure the element is a proper double-linked list
            element.setNext(element);
            element.setPrev(element);
            root = element;
            return;
        }
        // If the element we are inserting is before the first one
        if (element.value < root.value) {
            element.setPrev(root.getPrev());
            element.setNext(root);
            root.setPrev(element);
            element.getPrev().setNext(element);
            root = element;
        } else {
            Node current = root.getNext();
            while (current != root && current.value <= element.value) current = current.getNext();
            element.setNext(current);
            element.setPrev(current.getPrev());
            element.getPrev().setNext(element);
            current.setPrev(element);
        }
    }

    /**
     * A single-threaded implementation of list removal. This is NOT thread safe!
     * @param value the value to remove from the list. Duplicates are not removed, only the
     *              first occurrence of the value.
     */
    public void remove (int value) {
        // If the list is empty, or the element we are removing is smaller than the first
        // element of the list, then there is nothing to remove
        if (root == null || value < root.value) return;
        // If we need to remove the root
        if (value == root.value) {
            // If the root element is the only element, then the result is an empty list
            if (root.getNext() == root) {
                root = null;
                return;
            }
            // Otherwise, return the list starting from the second element
            Node newRoot = root.getNext();
            root.getPrev().setNext(newRoot);
            newRoot.setPrev(root.getPrev());
            root = newRoot;
            return;
        }
        Node current = root.getNext();
        while (current != root && current.value < value) current = current.getNext();
        // We found an element with the given value. Remove it
        if (current.value == value) {
            current.getNext().setPrev(current.getPrev());
            current.getPrev().setNext(current.getNext());
        }
    }

    /**
     * Check if the list contains the given value. This is NOT thread safe!
     * @param value the value to check for.
     * @return true if the list contains the element, false otherwise.
     */
    public boolean contains(int value) {
        // If the value is smaller than the root element, we know it cannot be in the list
        if (value < root.value) return false;
        if (value == root.value) return true;
        Node current = root.getNext();
        while (current != root && value > current.value) current = current.getNext();
        return current.value == value;
    }

    /**
     * Concurrent implementation of the double-linked list insertion.
     * TODO: Implement this!
     */
    public void concurrentInsert(int value) {
        insert(value);
    }



    /**
     * Concurrent implementation of the double-linked list deletion.
     * TODO: Implement this!
     */
    public void concurrentRemove(int value) {
        remove(value);
    }

    /**
     * Concurrent implementation of the double-linked list checking for containment.
     * TODO: Implement this!
     */
    public boolean concurrentContains(int value) {
        return contains(value);
    }

}
