package edu.rice.comp322;

import edu.rice.hj.api.HjFuture;
import edu.rice.hj.api.SuspendableException;

import java.util.Arrays;

import static edu.rice.hj.Module0.doWork;
import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module1.future;

/**
 * edu.rice.comp322.ReciprocalArraySum --- Computing the sum of reciprocals of array elements with 2-way parallelism.
 * <br />
 * The goal of this example program is to create an array of n random int's, and compute the sum of their reciprocals in
 * two ways: 1) Sequentially in method seqArraySum() 2) In parallel using 2, 4 and 8 future tasks, and 3) in parallel,
 * creating maximum parallelism available in the problem.
 * The actual profitability of the parallelism depends on the size of the array and the overhead of async creation,
 * and on the available resources on your machine. Instead, we are collecting abstract performance metrics, which will
 * tell us what the parallelism WOULD be if you were to execute this code on an ideal parallel machine.
 * <br />
 * Your assignment is to use 2, 4, and 8-way parallelism in methods parArraySum2Futures(), parArraySum4Futures() and parArraySum8Futures()
 * to obtain the ideal speedup over seqArraySum(). Also, you need to write parArraySumMaxParallel() method
 * that exploits maximum parallelism available in the problem.
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
     * TODO: Compute the sum of the reciprocals of the array elements in parallel using two futures.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double parArraySum2Futures(final double[] inX) throws SuspendableException {
        
        var lower = future(() -> {
            var sum1 = 0.0;
                // Compute sum of lower half of array
                for (int i = 0; i < inX.length / 2; i++) {
                    sum1 += 1 / inX[i];
                    doWork(1);
                }
                return sum1;
        });

        var upper = future(() -> {
            var sum2 = 0.0;
            // Compute sum of upper half of array
            for (int i = inX.length / 2; i < inX.length; i++) {
                sum2 += 1 / inX[i];
                doWork(1);
            }
            return sum2;
        });

        // Combine sum1 and sum2
        var sum = lower.get() + upper.get();
        doWork(1);

        return sum;
    }

    /**
     * TODO: Compute the sum of the reciprocals of the array elements in parallel using four futures.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double parArraySum4Futures(final double[] inX) throws SuspendableException {

        var first = future(() -> {
            var sum1 = 0.0;
            // Compute sum of lower half of array
            for (int i = 0; i < inX.length / 4; i++) {
                sum1 += 1 / inX[i];
                doWork(1);
            }
            return sum1;
        });

        var second = future(() -> {
            var sum2 = 0.0;
            // Compute sum of upper half of array
            for (int i = inX.length / 4; i < inX.length / 2; i++) {
                sum2 += 1 / inX[i];
                doWork(1);
            }
            return sum2;
        });

        var third = future(() -> {
            var sum3 = 0.0;
            // Compute sum of upper half of array
            for (int i = inX.length / 2; i < (inX.length - inX.length / 4); i++) {
                sum3 += 1 / inX[i];
                doWork(1);
            }
            return sum3;
        });

        var fourth = future(() -> {
            var sum4 = 0.0;
            // Compute sum of upper half of array
            for (int i = (inX.length - inX.length / 4); i < inX.length; i++) {
                sum4 += 1 / inX[i];
                doWork(1);
            }
            return sum4;
        });

        // Combine sums
        var sum = first.get() + second.get() + third.get() + fourth.get();
        doWork(1);

        return sum;
    }

    /**
     * TODO: Compute the sum of the reciprocals of the array elements in parallel using eight futures.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double parArraySum8Futures(final double[] inX) throws SuspendableException {

        var first = future(() -> {
            var sum1 = 0.0;
            // Compute sum of lower half of array
            for (int i = 0; i < inX.length / 8; i++) {
                sum1 += 1 / inX[i];
                doWork(1);
            }
            return sum1;
        });

        var second = future(() -> {
            var sum2 = 0.0;
            // Compute sum of upper half of array
            for (int i = inX.length / 8; i < inX.length / 4; i++) {
                sum2 += 1 / inX[i];
                doWork(1);
            }
            return sum2;
        });

        var third = future(() -> {
            var sum3 = 0.0;
            // Compute sum of upper half of array
            for (int i = inX.length / 4; i < ((inX.length / 2) - (inX.length / 8)); i++) {
                sum3 += 1 / inX[i];
                doWork(1);
            }
            return sum3;
        });

        var fourth = future(() -> {
            var sum4 = 0.0;
            // Compute sum of upper half of array
            for (int i = ((inX.length / 2) - (inX.length / 8)); i < inX.length / 2; i++) {
                sum4 += 1 / inX[i];
                doWork(1);
            }
            return sum4;
        });


        var fifth = future(() -> {
            var sum5 = 0.0;
            // Compute sum of lower half of array
            for (int i = inX.length / 2; i < ((inX.length / 2) + (inX.length / 8)); i++) {
                sum5 += 1 / inX[i];
                doWork(1);
            }
            return sum5;
        });

        var sixth = future(() -> {
            var sum6 = 0.0;
            // Compute sum of upper half of array
            for (int i = ((inX.length / 2) + (inX.length / 8)); i < ((inX.length / 2) + (inX.length / 4)); i++) {
                sum6 += 1 / inX[i];
                doWork(1);
            }
            return sum6;
        });

        var seventh = future(() -> {
            var sum7 = 0.0;
            // Compute sum of upper half of array
            for (int i = ((inX.length / 2) + (inX.length / 4)); i < (inX.length - inX.length / 8); i++) {
                sum7 += 1 / inX[i];
                doWork(1);
            }
            return sum7;
        });

        var eighth = future(() -> {
            var sum8 = 0.0;
            // Compute sum of upper half of array
            for (int i = (inX.length - inX.length / 8); i < inX.length; i++) {
                sum8 += 1 / inX[i];
                doWork(1);
            }
            return sum8;
        });

        // Combine sums
        var sum = first.get() + second.get() + third.get() + fourth.get() + fifth.get() + sixth.get() + seventh.get() + eighth.get();
        doWork(1);

        return sum;

    }

    /**
     * TODO: Compute the sum of the reciprocals of the array elements by creating maximum parallelism.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double parArraySumMaxParallel(final double[] inX) throws SuspendableException {
        if (inX.length == 1) {
            return inX[0];
        }
        else if (inX.length == 2) {
            return parArraySum2Futures(inX);
        }
        else {
            var sum1 = future(() -> {
                return parArraySumMaxParallel(Arrays.copyOfRange(inX, 0, inX.length / 2));
            });

            var sum2 = future(() -> {
                return parArraySumMaxParallel(Arrays.copyOfRange(inX, inX.length / 2, inX.length));
            });

            var result1 = sum1.get();
            var result2 = sum2.get();
            var result = result1 + result2;
            doWork(1);
            return result;
        }
    }
}
