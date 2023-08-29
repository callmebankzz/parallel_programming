package edu.rice.comp322.spanningtree;

/**
 * You need to make the appropriate changes as described in the lab 8 pdf handout so that you are only using Java
 * Threads and nothing from the HJ library.
 */
public class SpanningTreeSeq {

    protected static Node doComputation(final Node[] nodes) {
        final Node root = nodes[0];
        root.parent = null;
        root.compute();
        return root;
    }

    /* Node class used in the graph/tree */
    protected static class Node implements NodeWithNeighbors<Node> {
        Node[] neighbors;
        public Node parent = null;
        String name;

        /* constructor */
        public Node(final String setName) {
            neighbors = new Node[0];
            name = setName;
        }

        public void setNeighbors(final Node[] n) {
            neighbors = n;
        }

        public Node parent() {
            return parent;
        }

        /* try setting the parent */
        boolean tryLabeling(final Node n) {
            if (parent == null) {
                parent = n;
            }
            return parent == n;
        }

        /* label*/
        void compute() {
            // Recursively compute for each neighbour
            for (int i = 0; i < neighbors.length; i++) {
                final Node child = neighbors[i];
                if (child.tryLabeling(this)) {
                    child.compute();
                }
            }
        }
    }
}
