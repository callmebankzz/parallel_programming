package edu.rice.comp322;

import java.util.Random;

import static edu.rice.hj.Module1.async;

/**
 * Description available at http://en.wikipedia.org/wiki/Quicksort
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public class QuickSort extends Sort {

    @Override
    protected void seqSort(final DataItem[] dataArray) {
        recursiveQuickSort(dataArray, 0, dataArray.length - 1);
    }

    @Override
    protected void parSort(final DataItem[] dataArray) { parallelQuickSort(dataArray, 0, dataArray.length - 1); }
    /**
     * Method that sorts an array using the quicksort algorithm.
     *
     * @param inA - Array of comparable objects
     * @param inM - the lower bounds of the area to sort
     * @param inN - the upper bounds of the area to sort
     */
    private void recursiveQuickSort(final DataItem[] inA, final int inM, final int inN) {
        if (inM < inN) {
            final int[] p = partition(inA, inM, inN);
            int inI = p[0];
            int inJ = p[1];
            recursiveQuickSort(inA, inM, inI);
            recursiveQuickSort(inA, inJ, inN);
        }
    }

    /**
     * Method that sorts an array using the quicksort algorithm.
     *
     * @param inA - Array of comparable objects
     * @param inM - the lower bounds of the area to sort
     * @param inN - the upper bounds of the area to sort
     */
    private void parallelQuickSort(final DataItem[] inA, final int inM, final int inN) {
        if (inM < inN) {
            final int[] p = partition(inA, inM, inN);
            int inI = p[0];
            int inJ = p[1];
            async(() -> parallelQuickSort(inA, inM, inI));
            async(() -> parallelQuickSort(inA, inJ, inN));
        }
    }

    /**
     * Mutates an array such that between two points in the array, all points less then a pivot point are to the left of
     * that point, and all points greater then a pivot point are to the right of that point.
     *
     * @param inA - a comparable array
     * @param inM - the lower bounds of the area to partition
     * @param inN - the upper bounds of the area to partition
     * @return - A point consisting of two locations around the pivot point
     */
    @SuppressWarnings("unchecked")
    private int[] partition(final DataItem[] inA, final int inM, final int inN) {

        int inI;
        int storeIndex = inM;
        final Random rand = new Random(inM + inN);
        final int pivot = inM + rand.nextInt(inN - inM + 1);
        final DataItem pivotValue = inA[pivot];

        exchange(inA, pivot, inN);
        for (inI = inM; inI < inN; inI++) {
            if (inA[inI].compareTo(pivotValue) <= 0) {
                exchange(inA, inI, storeIndex);
                storeIndex++;
            }
        }
        exchange(inA, storeIndex, inN);

        if (storeIndex == inN) {
            return new int[]{storeIndex - 1, inN};
        } else if (storeIndex == inM) {
            return new int[]{inM, storeIndex + 1};
        }
        return new int[]{storeIndex - 1, storeIndex + 1};
    }
}
