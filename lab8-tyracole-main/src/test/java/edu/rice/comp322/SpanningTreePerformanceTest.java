package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.rice.hj.runtime.config.HjSystemProperty;
import static edu.rice.hj.Module0.launchHabaneroApp;

import edu.rice.comp322.spanningtree.*;

public class SpanningTreePerformanceTest extends TestCase {

    private final static int nNodes = 20000;
    private final static int nNeighbors = 3000;

    public void testSpanningTreeHJlib() {
        if (PerfTestUtils.getAutograderNcores() == 1) {
            return;
        }

        final String testLabel = PerfTestUtils.getTestLabel();

        final NodeFactorySuspend<SpanningTreeAtomicHjLib.Node> nodeFactory =
            new NodeFactorySuspend<SpanningTreeAtomicHjLib.Node>() {
                @Override
                    public SpanningTreeAtomicHjLib.Node create(final String s) {
                        return new SpanningTreeAtomicHjLib.Node(s);
                    }

                @Override
                    public SpanningTreeAtomicHjLib.Node[] createArray(final int n) {
                        return new SpanningTreeAtomicHjLib.Node[n];
                    }

                @Override
                    public SpanningTreeAtomicHjLib.Node doComputation(final SpanningTreeAtomicHjLib.Node[] nodes)
                            throws SuspendableException {
                        return SpanningTreeAtomicHjLib.doComputation(nodes);
                    }
            };

        kernelBody(nodeFactory, testLabel, nNodes, nNeighbors);
    }

    public void testSpanningTreeThreads() {
        if (PerfTestUtils.getAutograderNcores() == 1) {
            return;
        }

        final String testLabel = PerfTestUtils.getTestLabel();

        final NodeFactoryNoSuspend<SpanningTreeAtomicThreads.Node> nodeFactory =
            new NodeFactoryNoSuspend<SpanningTreeAtomicThreads.Node>() {
                @Override
                    public SpanningTreeAtomicThreads.Node create(final String s) {
                        return new SpanningTreeAtomicThreads.Node(s);
                    }

                @Override
                    public SpanningTreeAtomicThreads.Node[] createArray(final int n) {
                        return new SpanningTreeAtomicThreads.Node[n];
                    }

                @Override
                    public SpanningTreeAtomicThreads.Node doComputation(final SpanningTreeAtomicThreads.Node[] nodes) {
                        return SpanningTreeAtomicThreads.doComputation(nodes);
                    }
            };
        kernelBodyNoHJlib(nodeFactory, testLabel, nNodes, nNeighbors);
    }

    private class PerformanceTestContext<T> {
        public T[] nodes;
        public T rootNode;
    }

    private <T extends NodeWithNeighbors<T>, S extends NodeWithNeighbors<S>> void kernelBody(
            final NodeFactorySuspend<T> parNodeFactory, final String testLabel, final int nNodes, final int nNeighbors) {
        final PerformanceTestContext<T> parCtx = new PerformanceTestContext<T>();
        final PerformanceTestContext<S> seqCtx = new PerformanceTestContext<S>();

        launchHabaneroApp(() -> {
            PerfTestUtils.PerfTestResults timingInfo = PerfTestUtils.runPerfTest(testLabel,
                () -> {
                    // parallel setup
                    parCtx.nodes = generateGraph(parNodeFactory, nNodes, nNeighbors, 123456L);
                },
                () -> {
                    // parallel execution
                    parCtx.rootNode = parNodeFactory.doComputation(parCtx.nodes);
                },
                () -> {
                    // parallel cleanup
                    final int nNodesProcessed = nodesProcessed(parCtx.nodes, parCtx.rootNode);
                    assertEquals("Spanning Tree has wrong number of nodes! Expected " + parCtx.nodes.length + " but got " +
                        nNodesProcessed, parCtx.nodes.length, nNodesProcessed);
                },
                () -> {
                    // final checks
                }, 10, 1139, PerfTestUtils.getAutograderNcores() == -1 ? Runtime.getRuntime().availableProcessors() : PerfTestUtils.getAutograderNcores(), true);

            System.out.println(testLabel + ": the parallel implementation ran in " + timingInfo.parTime +
                    " ms, " + ((double)timingInfo.seqTime / (double)timingInfo.parTime) + "x faster than the " +
                    "sequential (" + timingInfo.seqTime + " ms)");
        });
    }

    private <T extends NodeWithNeighbors<T>, S extends NodeWithNeighbors<S>> void kernelBodyNoHJlib(
            final NodeFactoryNoSuspend<T> parNodeFactory, final String testLabel, final int nNodes, final int nNeighbors) {
        final PerformanceTestContext<T> parCtx = new PerformanceTestContext<T>();
        final PerformanceTestContext<S> seqCtx = new PerformanceTestContext<S>();

        // launchHabaneroApp(() -> {
            PerfTestUtils.PerfTestResults timingInfo = PerfTestUtils.runPerfTestNoSuspend(testLabel,
                () -> {
                    // parallel setup
                    parCtx.nodes = generateGraph(parNodeFactory, nNodes, nNeighbors, 123456L);
                },
                () -> {
                    // parallel execution
                    parCtx.rootNode = parNodeFactory.doComputation(parCtx.nodes);
                },
                () -> {
                    // parallel cleanup
                    final int nNodesProcessed = nodesProcessed(parCtx.nodes, parCtx.rootNode);
                    assertEquals("Spanning Tree has wrong number of nodes! Expected " + parCtx.nodes.length + " but got " +
                        nNodesProcessed, parCtx.nodes.length, nNodesProcessed);
                },
                () -> {
                    // final checks
                }, 10, 1139, PerfTestUtils.getAutograderNcores() == -1 ? Runtime.getRuntime().availableProcessors() : PerfTestUtils.getAutograderNcores());

            System.out.println(testLabel + ": the parallel implementation ran in " + timingInfo.parTime +
                    " ms, " + ((double)timingInfo.seqTime / (double)timingInfo.parTime) + "x faster than the " +
                    "sequential (" + timingInfo.seqTime + " ms)");
        // });
    }

    public static <T extends NodeWithNeighbors<T>> T[] generateGraph(
            final NodeFactory<T> nodeFactory, final int numNodes,
            final int globalNumNeighbors, final long seed) {

        final Random rand = new Random(seed);
        @SuppressWarnings("unchecked")
        final T[] nodes = nodeFactory.createArray(numNodes);
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = nodeFactory.create(Integer.toString(i));
        }
        for (int i = 0; i < nodes.length; i++) {
            final int numNeighbors = ((globalNumNeighbors == -1) ? rand.nextInt(10) : globalNumNeighbors);
            @SuppressWarnings("unchecked")
            final T[] neighbors = nodeFactory.createArray(numNeighbors);
            for (int j = 0; j < neighbors.length; j++) {
                int neighborIndex = rand.nextInt(nodes.length);
                if (neighborIndex == i) {
                    neighborIndex = (neighborIndex + 1) % numNodes;
                }
                neighbors[j] = nodes[neighborIndex];
            }
            nodes[i].setNeighbors(neighbors);
        }

        return nodes;
    }

    public static <T extends NodeWithNeighbors<T>> int nodesProcessed(final T[] nodes, final T rootNode) {

        int counter = 0;
        for (final T node : nodes) {
            if (nodeProcessed(node, rootNode)) {
                counter++;
            }
        }
        return counter;
    }

    public static <T extends NodeWithNeighbors<T>> boolean nodeProcessed(final T targetNode, final T rootNode) {

        final Set<T> visitedNodes = new HashSet<>();

        T loopNode = targetNode;
        while (loopNode != null && !visitedNodes.contains(loopNode)) {
            visitedNodes.add(loopNode);
            loopNode = loopNode.parent();
        }

        return visitedNodes.contains(rootNode);
    }

    public interface NodeFactory<T extends NodeWithNeighbors<T>> {
        T create(String s);
        T[] createArray(int n);
    }

    public interface NodeFactorySuspend<T extends NodeWithNeighbors<T>> extends NodeFactory<T> {
        T doComputation(T[] nodes) throws SuspendableException;
    }

    public interface NodeFactoryNoSuspend<T extends NodeWithNeighbors<T>> extends NodeFactory<T> {
        T doComputation(T[] nodes);
    }
}
