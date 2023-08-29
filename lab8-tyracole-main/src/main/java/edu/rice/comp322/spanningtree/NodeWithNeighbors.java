package edu.rice.comp322.spanningtree;

/**
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public interface NodeWithNeighbors<T> {
    public void setNeighbors(T[] n);

    public T parent();
}
