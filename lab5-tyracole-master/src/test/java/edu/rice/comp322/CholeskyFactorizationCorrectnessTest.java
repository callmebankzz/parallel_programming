package edu.rice.comp322;

import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.runtime.util.Pair;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * Simple Unit test for Cholesky Factorization.
 *
 * @author Shams Imam (shams@rice.edu)
 */
public class CholeskyFactorizationCorrectnessTest extends TestCase {

    public void testCholeskyFactorization_par_500_10() throws IOException {
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_500_10() starts...");
        runCholeskyFactorization(new CholeskyFactorizationParallel(), 500, 10, 50.0);
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_500_10() ends.");
    }

    public void testCholeskyFactorization_par_500_50() throws IOException {
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_500_50() starts...");
        runCholeskyFactorization(new CholeskyFactorizationParallel(), 500, 50, 10.0);
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_500_50() ends.");
    }

    public void testCholeskyFactorization_par_500_100() throws IOException {
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_500_100() starts...");
        runCholeskyFactorization(new CholeskyFactorizationParallel(), 500, 100, 3.0);
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_500_100() ends.");
    }

    public void testCholeskyFactorization_par_500_125() throws IOException {
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_500_125() starts...");
        runCholeskyFactorization(new CholeskyFactorizationParallel(), 500, 125, 2.0);
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_500_125() ends.");
    }

    public void testCholeskyFactorization_par_1000_50() throws IOException {
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_1000_50() starts...");
        runCholeskyFactorization(new CholeskyFactorizationParallel(), 1000, 50, 30.0);
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_1000_50() ends.");
    }

    public void testCholeskyFactorization_par_1000_100() throws IOException {
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_1000_100() starts...");
        runCholeskyFactorization(new CholeskyFactorizationParallel(), 1000, 100, 10.0);
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_1000_100() ends.");
    }

    public void testCholeskyFactorization_par_1000_125() throws IOException {
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_1000_125() starts...");
        runCholeskyFactorization(new CholeskyFactorizationParallel(), 1000, 125, 5.0);
        System.out.println("CholeskyFactorizationCorrectnessAndMetricsTest.testCholeskyFactorization_par_1000_125() ends.");
    }
    
    private void runCholeskyFactorization(
        final CholeskyFactorization cf,
        final int arraySize,
        final int blockSize,
        final double minParallelism)
        throws IOException {

        final Pair<Boolean, HjMetrics> pair = CholeskyFactorization.runCholeskyFactorization(
            cf, new String[]{"-n", String.valueOf(arraySize), "-b", String.valueOf(blockSize)});
        assertTrue("Result validation failed!", pair.left);

        final HjMetrics metrics = pair.right;
        assertTrue("Parallelism must be at least " + minParallelism + " but was " + metrics.idealParallelism(), metrics.idealParallelism() >= minParallelism);

    }
}
