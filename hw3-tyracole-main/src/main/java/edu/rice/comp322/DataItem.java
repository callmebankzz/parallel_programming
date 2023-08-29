package edu.rice.comp322;

import static edu.rice.hj.Module0.doWork;

/**
 * A comparable object.
 * <p/>
 * DO NOT MODIFY THIS FILE.
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public final class DataItem implements Comparable<DataItem> {
    private final int data;

    /**
     * Constructor for a dataItem.
     *
     * @param data - an integer
     */
    protected DataItem(final int data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataItem) {
            DataItem other = (DataItem)obj;
            return this.compareTo(other) == 0;
        }
        return false;
    }

    @Override
    public final int compareTo(final DataItem other) {
        // Every comparison is a unit of work
        doWork(1);
        return this.data - other.data;
    }
}
