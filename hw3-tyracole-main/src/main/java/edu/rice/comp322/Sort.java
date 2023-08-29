package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;

/**
 * Abstract class to represent a sort algorithm. It has two methods {@code seqSort} and {@code parSort} to perform the
 * sort. The abstract method {@code name} is used to identify a sort algorithm.
 * <p/>
 * DO NOT MODIFY THIS FILE.
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public abstract class Sort {

    protected abstract void seqSort(DataItem[] dataArray);

    protected void parSort(final DataItem[] dataArray) throws SuspendableException {
        // by default parSort is the same as seqSort!
        seqSort(dataArray);
    }

    protected final String name() {
        return getClass().getSimpleName();
    }

    protected final <T extends DataItem> boolean lt(final T a, final T b) {
        return a.compareTo(b) < 0;
    }

    protected final <T extends DataItem> boolean le(final T a, final T b) {
        return a.compareTo(b) <= 0;
    }

    protected final <T extends DataItem> boolean gt(final T a, final T b) {
        return a.compareTo(b) > 0;
    }

    protected final <T extends DataItem> boolean ge(final T a, final T b) {
        return a.compareTo(b) >= 0;
    }

    protected final <T extends DataItem> boolean eq(final T a, final T b) {
        return a.compareTo(b) == 0;
    }

    protected final void exchange(final DataItem[] dataArray, final int idx1, final int idx2) {
        final DataItem temp = dataArray[idx1];
        dataArray[idx1] = dataArray[idx2];
        dataArray[idx2] = temp;

    }

}
