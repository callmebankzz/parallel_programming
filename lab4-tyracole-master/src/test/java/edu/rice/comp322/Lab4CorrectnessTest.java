package edu.rice.comp322;

import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.api.HjSuspendingCallable;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.config.HjSystemProperty;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.util.Random;

import static edu.rice.comp322.ReciprocalArraySum.*;
import static edu.rice.hj.Module0.abstractMetrics;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.future;

/**
 * Unit test for simple App.
 */
public class Lab4CorrectnessTest extends TestCase {
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

    protected static TestResult doExecute(final String name,
                                      final HjSuspendingCallable<Double> body) throws SuspendableException {
        System.out.println("  *** Executing " + name);

        // Need to create a future so that we can collect abstract metrics computation
        var result = future(body).get();

        final HjMetrics actualMetrics = abstractMetrics();
        System.out.println("Abstract metrics:");
        System.out.println(" Total work = number of calls to doWork() = " + actualMetrics.totalWork());
        System.out.println(" Critical path length through future tasks = " + actualMetrics.criticalPathLength());
        System.out.println(" Ideal parallelism = total work / CPL = " + actualMetrics.idealParallelism());

        return new TestResult(result);
    }

    public void testReciprocalParallelism2Futures() {
        try {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism2Futures() starts...\n");

            final TestResult[] results = new TestResult[2];
            final double[] X = initializeArray(DEFAULT_N);
            HjSystemProperty.abstractMetrics.set(true);

            // sequential
            launchHabaneroApp(() -> {
                System.out.println("* running sequential version (ie, 1 future) *");
                results[0] = doExecute("seqArraySum", () -> seqArraySum(X));
            });

            try {
                // two futures
                launchHabaneroApp(() -> {
                    System.out.println("* running parallel version with 2 futures *");
                    results[1] = doExecute("parArraySum2Futures", () -> parArraySum2Futures(X));
                    printReciprocalStats(2, abstractMetrics(), results);
                });
            } catch (final RuntimeException re) {
                if (re.getCause() instanceof AssertionFailedError) {
                    throw (AssertionFailedError) re.getCause();
                } else {
                    throw re;
                }
            }

        } finally {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism2Futures() ends.\n");
        }
    }

    public void testReciprocalParallelism4Futures() {
        try {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism4Futures() starts...\n");

            final TestResult[] results = new TestResult[2];
            final double[] X = initializeArray(DEFAULT_N);
            HjSystemProperty.abstractMetrics.set(true);

            // sequential
            launchHabaneroApp(() -> {
                System.out.println("* running sequential version (ie, 1 future) *");
                results[0] = doExecute("seqArraySum", () -> seqArraySum(X));
            });

            try {
                // four futures
                launchHabaneroApp(() -> {
                    System.out.println("* running parallel version with 4 futures *");
                    results[1] = doExecute("parArraySum4Futures", () -> parArraySum4Futures(X));
                    printReciprocalStats(4, abstractMetrics(), results);
                });
            } catch (final RuntimeException re) {
                if (re.getCause() instanceof AssertionFailedError) {
                    throw (AssertionFailedError) re.getCause();
                } else {
                    throw re;
                }
            }

        } finally {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism4Futures() ends.\n");
        }
    }

    public void testReciprocalParallelism8Futures() {
        try {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism8Futures() starts...\n");

            final TestResult[] results = new TestResult[2];
            final double[] X = initializeArray(DEFAULT_N);
            HjSystemProperty.abstractMetrics.set(true);

            // sequential
            launchHabaneroApp(() -> {
                System.out.println("* running sequential version (ie, 1 future) *");
                results[0] = doExecute("seqArraySum", () -> seqArraySum(X));
            });

            try {
                // eight futures
                launchHabaneroApp(() -> {
                    System.out.println("* running parallel version with 8 futures *");
                    results[1] = doExecute("parArraySum8Futures", () -> parArraySum8Futures(X));
                    printReciprocalStats(8, abstractMetrics(), results);
                });
            } catch (final RuntimeException re) {
                if (re.getCause() instanceof AssertionFailedError) {
                    throw (AssertionFailedError) re.getCause();
                } else {
                    throw re;
                }
            }
        } finally {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism8Futures() ends.\n");
        }
    }

    public void testReciprocalMaxParallelism() {
        try {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelismNFutures() starts...\n");

            final TestResult[] results = new TestResult[2];
            final double[] X = initializeArray(DEFAULT_N);
            HjSystemProperty.abstractMetrics.set(true);

            // sequential
            launchHabaneroApp(() -> {
                System.out.println("* running sequential version (ie, 1 future) *");
                results[0] = doExecute("seqArraySum", () -> seqArraySum(X));
            });

            try {
                // N futures
                launchHabaneroApp(() -> {
                    System.out.println("* running parallel version with maximum parallelism *");
                    results[1] = doExecute("parArraySumNFutures", () -> parArraySumMaxParallel(X));
                    printMaxParallelStats(abstractMetrics(), results);
                });
            } catch (final RuntimeException re) {
                if (re.getCause() instanceof AssertionFailedError) {
                    throw (AssertionFailedError) re.getCause();
                } else {
                    throw re;
                }
            }
        } finally {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelismNFutures() ends.\n");
        }
    }

    private void printReciprocalStats(final int nFutures, final HjMetrics abstractMetrics,
                                      final TestResult[] results) {
        // ensure the parallel code achieves ideal parallelism
        System.out.println ("Expected ideal parallelism: >= " + nFutures*0.99);
        System.out.println ("Achieved parallelism: " + abstractMetrics.idealParallelism());
        System.out.println ("Expected CPL: <= " + (DEFAULT_N/nFutures + nFutures - 1));
        System.out.println ("Achieved CPL: " + abstractMetrics.criticalPathLength());
        if(abstractMetrics.idealParallelism() < nFutures*0.99) {
            System.out.println ("Not achieving ideal parallelism!");
            assert(false);
        }

        final TestResult seqRes = results[0];
        final TestResult parRes = results[1];

        // check for correctness
        final String seqResult = String.format("%8.5f", seqRes.result);
        final String parResult = String.format("%8.5f", parRes.result);
        assertEquals("Parallel implementation produces an incorrect result! Expected = " + seqResult + ", actual = " + parResult, 0, parResult.compareTo(seqResult));
    }

    private void printMaxParallelStats(final HjMetrics abstractMetrics,
                                      final TestResult[] results) {
        // ensure the parallel code achieves ideal parallelism
        var idealCPL = Math.ceil(Math.log(DEFAULT_N)/Math.log(2));
        var idealPar = abstractMetrics().totalWork() / idealCPL * 0.99;
        System.out.println ("Total work: " + abstractMetrics.totalWork());
        System.out.println ("Expected ideal parallelism: >= " + idealPar);
        System.out.println ("Achieved parallelism: " + abstractMetrics.idealParallelism());
        System.out.println ("Expected CPL: <= " + idealCPL*1.01);
        System.out.println ("Achieved CPL: " + abstractMetrics.criticalPathLength());
        if(abstractMetrics.idealParallelism() < idealPar) {
            System.out.println ("Not achieving ideal parallelism!");
            assert(false);
        }

        // print actual metrics
        final TestResult seqRes = results[0];
        final TestResult parRes = results[1];

        // check for correctness
        final String seqResult = String.format("%8.5f", seqRes.result);
        final String parResult = String.format("%8.5f", parRes.result);
        assertEquals("Parallel implementation produces an incorrect result! Expected = " + seqResult + ", actual = " + parResult, 0, parResult.compareTo(seqResult));
    }

    private static class TestResult {
        public final double result;

        public TestResult(final double result) {
            this.result = result;
        }
    }
}
