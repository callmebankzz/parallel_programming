package edu.rice.comp322.provided.trees;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * A very simple functional tree implementation
 * A tree can either be Empty, or a Node.
 */
public interface Tree<T> {

    /**
     * Create a new empty tree of the given parameter type.
     */
    @SuppressWarnings("unchecked")
    static <T> Tree<T> empty() {
        return (Tree<T>) Tree.Empty.SINGLETON;
    }

    /**
     * Builder for making a tree node.
     */
    static <T> Tree<T> makeNode(T value, GList<Tree<T>> children) {
        return new Node<>(value, children);
    }

    /**
     * Getter for value.
     */
    T value();

    /**
     * Getter for the children.
     */
    GList<Tree<T>> children();

    /**
     * Return true if this is an empty tree.
     */
    boolean isEmpty();

    /** Returns a new tree equal to the old tree with the function applied to each value. */
    <R> Tree<R> map(Function<T, R> f);

    /** Returns a new tree equal to all the nodes in the old tree satisfying the predicate. */
    Tree<T> filter(Predicate<T> p);

    <U> U fold(U zero,  BiFunction<U, T, U> operator);


    /**
     * Class Node implements a non-empty tree.
     * A Node has a value, and a GList of children (which could be empty).
     */
    class Node<T> implements Tree<T> {
        private final T value;
        private final GList<Tree<T>> children;

        public Node(T value, GList<Tree<T>> children) {
            this.value = value;
            this.children = children;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public GList<Tree<T>> children() {
            return children;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        /**
         * It should return a new tree equal to the old tree with the function applied to each value.
         * @param f   the function for mapping every value in the tree
         * @param <R> the type of the values in the new tree
         * @return the tree with the exact same structure as the original one, but with f applied to each value
         */
        @Override
        public <R> Tree<R> map(Function<T, R> f) {
            return Tree.makeNode(f.apply(value()), children().map(tree -> tree.map(f)));
        }

        /**
         * It should return a new tree that only contains the nodes whose values satisfy the predicate.
         * @param p   the predicate for determining if the node should stay in the tree
         */
        @Override
        public Tree<T> filter(Predicate<T> p) {
            var newChildren = children().map(child -> child.filter(p)).filter(child -> !child.isEmpty());
            if (p.test(value())) {
                return makeNode(value(), newChildren);
            } else if (newChildren.isEmpty()) {
                return empty();
            } else {
                return makeTreeHelper(newChildren);
            }
        }

        /**
         * @TODO Implement this function to complete problem 3 of section one of hw 1
         *
         * @param zero The base value to start the fold operation with. Should be of type U.
         * @param operator The function for how to combine a new tree's value into the accumulated
         *                 value.
         * @param <U> The return type as well as the type of the zero value.
         * @return a value of type U representing the folding of the entire tree.
         */
        @Override
        public <U> U fold(U zero, BiFunction<U, T, U> operator) {
            Tree<T> currentTree = this;

            while (!currentTree.isEmpty()) {
                zero = operator.apply(zero, currentTree.value());
                currentTree = makeTreeHelper(currentTree.children());
            }
            return zero;
        }

        /**
         * A helper function for making a tree out of a GList of trees. It picks the root of the first
         * tree as the root of the result, then adds the remaining trees to the children of the first tree
         */
        private Tree<T> makeTreeHelper(GList<Tree<T>> treeList) {
            if (treeList.isEmpty()) {
                return empty();
            } else {
                var firstChild = treeList.head();
                return makeNode(firstChild.value(), firstChild.children().appendAll(treeList.tail()));
            }
        }

    }

    /**
     * The empty tree.
     */
    class Empty<T> implements Tree<T> {
        /**
         * The constructor is private.
         */
        private Empty() {}

        /**
         * There is only one empty tree.
         */
        private static final Tree<?> SINGLETON = new Empty<>();

        /**
         * The map for an empty tree is simple: just return an empty tree.
         */
        @Override
        public <R> Tree<R> map(Function<T, R> f) {
            return empty();
        }

        /**
         * The filter for an empty tree is simple: just return an empty tree.
         */
        @Override
        public Tree<T> filter(Predicate<T> p) {
            return empty();
        }

        @Override
        public <U> U fold(U zero, BiFunction<U, T, U> operator) {
            return zero;
        }

        @Override
        public T value() {
            throw new NoSuchElementException("can't get a value from an empty tree");
        }

        @Override
        public GList<Tree<T>> children() {
            throw new NoSuchElementException("can't get children from an empty tree");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

    }
}