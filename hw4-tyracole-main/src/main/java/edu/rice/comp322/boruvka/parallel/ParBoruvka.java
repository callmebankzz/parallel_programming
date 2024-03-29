package edu.rice.comp322.boruvka.parallel;

import edu.rice.comp322.AbstractBoruvka;
import edu.rice.comp322.boruvka.BoruvkaFactory;
import edu.rice.comp322.boruvka.Edge;
import edu.rice.comp322.boruvka.Loader;
import edu.rice.hj.api.SuspendableException;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class must be modified.
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public final class ParBoruvka extends AbstractBoruvka implements BoruvkaFactory<ParComponent, ParEdge> {

    protected final Queue<ParComponent> nodesLoaded = new LinkedList<>();

    public ParBoruvka() {
        super();
    }

    @Override
    public boolean usesHjLib() {
        return false;
    }

    @Override
    public void initialize(final String[] args) {
        Loader.parseArgs(args);
    }

    @Override
    public void preIteration(final int iterationIndex) {
        // Exclude reading file input from timing measurement
        nodesLoaded.clear();
        Loader.read(this, nodesLoaded);

        totalEdges = 0;
        totalWeight = 0;
    }

    @Override
    public void runIteration(int nthreads) throws SuspendableException {
        computeBoruvka(nodesLoaded);
    }

    private void computeBoruvka(final Queue<ParComponent> nodesLoaded) {

        ParComponent loopNode = null;
        while (!nodesLoaded.isEmpty()) {
            loopNode = nodesLoaded.poll();

            // Empty queue or the component is acquired by another thread.
            if (loopNode == null || !loopNode.lock.tryLock()) {
                continue;
            }

            // Already processed.
            if (loopNode.isDead) {
                loopNode.lock.unlock();
                continue;
            }

            // If the graph is processed, unlock.
            Edge<ParComponent> minEdge = loopNode.getMinEdge();
            if (minEdge == null) {
                loopNode.lock.unlock();
                break;
            }

            // Process current component.
            final ParComponent other = minEdge.getOther(loopNode);
            if (!other.lock.tryLock()) {
                loopNode.lock.unlock();
                nodesLoaded.add(loopNode);
                continue;
            }

            other.isDead = true;
            loopNode.merge(other, minEdge.weight());
            loopNode.lock.unlock();
            other.lock.unlock();

            nodesLoaded.add(loopNode);
        }
        // END OF EDGE CONTRACTION ALGORITHM
        if (loopNode != null) {
            totalEdges = loopNode.totalEdges();
            totalWeight = loopNode.totalWeight();
        }
    }
    @Override
    public ParComponent newComponent(final int nodeId) {
        return new ParComponent(nodeId);
    }

    @Override
    public ParEdge newEdge(final ParComponent from, final ParComponent to, final double weight) {
        return new ParEdge(from, to, weight);
    }
}


