package edu.rice.comp322.spanningtree;

import edu.rice.hj.api.SuspendableException;

import java.util.concurrent.atomic.AtomicReference;

import static edu.rice.hj.Module1.async;
import static edu.rice.hj.Module1.finish;

/**
 * This class solves the minimum spanning tree problem using HJ library.
 */
public class SpanningTreeAtomicHjLib {

    /**
     * Entrypoint for constructing a spanning tree out of the provided graph.
     *
     * @param nodes Nodes of the graph to construct a spanning tree over.
     * @return The root of the spanning tree constructed.
     */
    public static Node doComputation(final Node[] nodes) throws SuspendableException {
        final Node root = nodes[0];
        root.parent = new AtomicReference<Node>(root);
        finish(() -> {
            root.compute();
        });
        return root;
    }

    /**
     * Node class used in the graph/tree.
     */
    public static class Node implements NodeWithNeighbors<Node> {
        Node[] neighbors;
        public AtomicReference<Node> parent = new AtomicReference<Node>(null);
        String name;

        /**
         * Constructor.
         */
        public Node(final String setName) {
            neighbors = new Node[0];
            name = setName;
            parent = new AtomicReference<Node>(null);
        }

        public void setNeighbors(final Node[] n) {
            neighbors = n;
        }

        public Node parent() {
            return parent.get();
        }

        /* try setting the parent */
        boolean tryLabeling(final Node n) {
            return parent.compareAndSet(null, n);
        }

        /* label*/
        void compute() {
            // Recursively compute for each neighbour
            for (int i = 0; i < neighbors.length; i++) {
                final Node child = neighbors[i];
                if (child.tryLabeling(this)) {
                    async(() -> child.compute());
                }
            }
        }
    }
}
