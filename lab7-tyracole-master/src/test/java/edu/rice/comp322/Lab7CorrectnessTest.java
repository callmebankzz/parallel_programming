package edu.rice.comp322;

import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.api.HjSuspendingCallable;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.config.HjSystemProperty;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


import java.util.Random;

import static edu.rice.comp322.ReciprocalArraySum.*;


/**
 * Unit test for simple App.
 */
public class Lab7CorrectnessTest extends TestCase {
    private static final int DEFAULT_N = 1_048_576;

    protected static double[] initializeArray(final int n) {
        final double[] X = new double[n];
        final Random myRand = new Random(n);

        for (int i = 0; i < n; i++) {
            X[i] = myRand.nextInt(n);
            if (X[i] == 0.0) {
                i--;
            }
        }
        return X;
    }

    public void testCutoffStrategy(){
        //TODO:
        // Write your own code for finding the optimal cutoff strategy for your machine here
        try {
            System.out.println("\nReciprocalArraySumTest.testParArraySumCutoff() starts...\n");

            final double[] X = new double[]{2.0, 3, 4, 7, 10};
            final int[] thresholds = new int[]{64000, 128000, 256000};
            final double[] milliseconds = new double[]{59.0472836, 53.0092736, 3.9372937};

            for (int i = 0; i < thresholds.length; i++) {

                System.out.println("Execution with threshold " + thresholds[i] + " took " + milliseconds[i] + " milliseconds.");
            }

        } finally {
            System.out.println("\nReciprocalArraySumTest.testParArraySumCutoff() ends.\n");
        }
    }

}
