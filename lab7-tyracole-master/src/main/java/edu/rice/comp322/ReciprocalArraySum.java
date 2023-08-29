package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.rice.hj.Module0.doWork;
import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module1.async;
import static edu.rice.hj.Module1.future;

/**
 * edu.rice.comp322.ReciprocalArraySum --- Computing the sum of reciprocals of array elements with 2-way parallelism.
 * <br />
 * The goal of this example program is to create an array of n random int's, and compute the sum of their reciprocals in
 * two ways: 1) Sequentially in method seqArraySum() 2) In parallel using finish/async, recursion, and a cutoff strategy for threshold
 * creating maximum parallelism available in the problem.
 * The actual profitability of the parallelism depends on the size of the array and the overhead of async creation,
 * and on the available resources on your machine.
 */
public final class ReciprocalArraySum {

    /**
     * Disallow instance creation of utility class.
     */
    private ReciprocalArraySum() {
        super();
    }

    /**
     * Sequentially compute the sum of the reciprocals of the array elements.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double seqArraySum(final double[] inX) throws SuspendableException {

        // Create a future and then do a get on it immediately so that we can collect abstract metrics
        return future(() -> {
            // Compute the sum of the reciprocals of the array elements
            var sum = 0.0;
            for (var x : inX) {
                sum += 1 / x;
                // Call doWork() here to keep track abstractly of how much work is being done
                doWork(1);
            }
            return sum;
        }).get();

    }

    /**
     *
     * @param inX       the input array.
     * @param threshold the cutoff threshold
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double parArraySumCutoff(final double[] inX, final int start, final int end,
                                              final int threshold) throws SuspendableException {
        if (end - start == threshold) {
            double sum = 0.0;
            for (int i = start; i < end; i++) {
                doWork(1);
                sum = sum + 1 / inX[i];
            }
            return sum;
        } else {
            var bottom = future(() -> parArraySumCutoff(inX, start, (end + start) / 2, threshold));
            var top = future(() -> parArraySumCutoff(inX, (end + start) / 2, end, threshold));
            double bVal = bottom.get();
            double tVal = top.get();
            doWork(1);
            return bVal + tVal;
        }
    }
}
