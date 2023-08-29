package edu.rice.comp322.boruvka.parallel;

import edu.rice.comp322.boruvka.Component;
import edu.rice.comp322.boruvka.Edge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class may be modified.
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public final class ParComponent extends Component<ParComponent> {

    /**
     *  A unique identifier for this component in the graph that contains
     *  it.
     */
    public final int nodeId;
    public final ReentrantLock lock = new ReentrantLock();

    /**
     * List of edges attached to this component, sorted by weight from least
     * to greatest.
     */
    private List<Edge<ParComponent>> edges = new ArrayList<>();

    /**
     * The weight this component accounts for. A component gains weight when
     * it is merged with another component across an edge with a certain
     * weight.
     */
    private double totalWeight = 0;

    /**
     * Number of edges that have been collapsed to create this component.
     */
    private long totalEdges = 0;

    /**
     * Boolean that determines whether this component has already been collapsed
     * into another component.
     */
    public boolean isDead = false;

    /**
     * Constructor.
     *
     * @param setNodeId ID for this node.
     */
    public ParComponent(final int setNodeId) {
        super();
        this.nodeId = setNodeId;
    }

    /**
     * Returns the unique id of this component.
     */
    @Override
    public int nodeId() {
        return nodeId;
    }

    /**
     * Returns the total weight of this component.
     */
    @Override
    public double totalWeight() {
        return totalWeight;
    }

    /**
     * Returns the total number of edges in the edge list.
     */
    @Override
    public long totalEdges() {
        return totalEdges;
    }

    /**
     * Adds an edge to the edge list.
     *
     * Edge is inserted in weight order, from least to greatest.
     */
    public void addEdge(final Edge<ParComponent> e) {
        int i = 0;
        while (i < edges.size()) {
            if (e.weight() < edges.get(i).weight()) {
                break;
            }
            i++;
        }
        edges.add(i, e);
    }

    /**
     * Get the edge with minimum weight from the sorted edge list.
     *
     * @return Edge with the smallest weight attached to this component.
     */
    public Edge<ParComponent> getMinEdge() {
        if (edges.size() == 0) {
            return null;
        }
        return edges.get(0);
    }

    /**
     * Merge two components together, connected by an edge with weight
     * edgeWeight.
     *
     * @param other The other component to merge into this component.
     * @param edgeWeight Weight of the edge connecting these components.
     */
    public void merge(final ParComponent other, final double edgeWeight) {
        totalWeight += other.totalWeight + edgeWeight;
        totalEdges += other.totalEdges + 1;

        final List<Edge<ParComponent>> newEdges = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i + j < edges.size() + other.edges.size()) {
            // Get rid of inter-component edges
            while (i < edges.size()) {
                final Edge<ParComponent> e = edges.get(i);
                if ((e.fromComponent() != this
                        && e.fromComponent() != other)
                        || (e.toComponent() != this
                        && e.toComponent() != other)) {
                    break;
                }
                i++;
            }
            while (j < other.edges.size()) {
                final Edge<ParComponent> e = other.edges.get(j);
                if ((e.fromComponent() != this
                        && e.fromComponent() != other)
                        || (e.toComponent() != this
                        && e.toComponent() != other)) {
                    break;
                }
                j++;
            }

            if (j < other.edges.size() && (i >= edges.size()
                    || edges.get(i).weight()
                    > other.edges.get(j).weight())) {
                newEdges.add(other.edges.get(j++).replaceComponent(other,
                        this));
            } else if (i < edges.size()) {
                newEdges.add(edges.get(i++).replaceComponent(other, this));
            }
        }
        other.edges.clear();
        edges.clear();
        edges = newEdges;
    }

    /**
     * Test for equality based on node ID.
     *
     * @param o Object to compare against.
     * @return true if they are the same component in the graph.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Component)) {
            return false;
        }

        final Component component = (Component) o;
        return component.nodeId() == nodeId;
    }

    /**
     * Hash based on component node ID.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return nodeId;
    }
}
