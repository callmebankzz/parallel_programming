package edu.rice.comp322;

/**
 * Description available at http://en.wikipedia.org/wiki/Merge_sort
 * <p/>
 * Adapted from: http://home.westman.wave.ca/~rhenry/sort/src/MergeSortAlgorithm.java
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public class MergeSort extends Sort {

    @Override
    protected void seqSort(final DataItem[] dataArray) {
        mergeSort(dataArray, 0, dataArray.length - 1);
    }

    private void mergeSort(final DataItem[] a, final int lo0, final int hi0) {
        int lo = lo0;
        final int hi = hi0;
        if (lo >= hi) {
            return;
        }
        final int mid = (lo + hi) / 2;

        // Partition the list into two lists and sort them recursively
        mergeSort(a, lo, mid);
        mergeSort(a, mid + 1, hi);

        // Merge the two sorted lists
        int endLow = mid;
        int startHi = mid + 1;
        while ((lo <= endLow) && (startHi <= hi)) {
            if (lt(a[lo], a[startHi])) {
                lo++;
            } else {
                // The next element comes from the second list, move the a[startHi] element into the next position and
                // shuffle all the other elements up.
                final DataItem T = a[startHi];
                for (int k = startHi - 1; k >= lo; k--) {
                    a[k + 1] = a[k];
                }
                a[lo] = T;
                lo++;
                endLow++;
                startHi++;
            }
        }
    }
}
