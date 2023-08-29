package edu.rice.comp322.spanningtree;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * You need to make the appropriate changes as described in the lab 9 pdf handout so that 
 * you are only using Java Threads, and nothing from the HJ library.
 */
public class SpanningTreeAtomicThreads {

    // TODO: Add a ConcurrentLinkedQueue to track all the Threads.
    static ConcurrentLinkedQueue<Thread> threads = new ConcurrentLinkedQueue<>();
    // TODO: Add a cutoff depth, since we only want to create new threads for computations
    // TODO: high up in the tree. Try a depth of 1-5 and see how that affects performance.
    private static int cutoff = 5;

    /**
     * Entrypoint for constructing a spanning tree out of the provided graph.
     *
     * @param nodes Nodes of the graph to construct a spanning tree over.
     * @return The root of the spanning tree constructed.
     */
    public static Node doComputation(final Node[] nodes) {
        final Node root = nodes[0];
        root.parent = new AtomicReference<Node>(root);
        // TODO: Pass the current depth to compute. You will need to change the method
        // TODO: signature.
        root.compute(0);

        // TODO: Poll for threads from the queue, while it is not empty.
        // TODO: Join each thread you get from the queue.
        while(!(threads.isEmpty())) {
            Thread newThr = threads.poll();
            if(!(newThr == null)){
                try {
                    newThr.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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

        /* label */
        void compute(int depth) {
            // Recursively compute for each neighbour
            for (int i = 0; i < neighbors.length; i++) {
                final Node child = neighbors[i];
                if (child.tryLabeling(this)) {
                    // TODO: Use your current depth to decide whether or not you want to
                    // TODO: create a new thread for the child computation.
                    if (depth < cutoff){
                        int dep = depth + 1;
                        Thread thr = new Thread(() -> child.compute(dep));
                        threads.add(thr);
                        thr.start();
                    } else {
                        child.compute(depth+1);
                    }
                    // TODO: Remember to add the thread to the queue and start it.
                    //threads.add(thr);
                    //thr.start();
                    //
                }
            }
        }
    }
}
