package edu.rice.comp322;

import junit.framework.TestCase;

import static edu.rice.hj.Module0.*;
import static edu.rice.hj.Module1.async;

import edu.rice.hj.runtime.config.HjSystemProperty;

public class PascalsTriangleCorrectnessTest extends TestCase {
    private void testDriver(final int n, final int k,
            final int expectedVal, final double expectedWork,
            final double expectedCPL, final double expectedIdealParallelism) {
        HjSystemProperty.abstractMetrics.setProperty(true);

        final int[] result = new int[1];
        final double[] metrics = new double[3];
        launchHabaneroApp(() -> {
            // Note abstract metrics only works inside a async-finish block
            finish(() -> {
                async(() -> {
                    result[0] = PascalsTriangle.choose(n, k);
                });
            });
        }, () -> {
            metrics[0] = abstractMetrics().totalWork();
            metrics[1] = abstractMetrics().criticalPathLength();
            metrics[2] = abstractMetrics().idealParallelism();
        });

        System.out.println("Computing the value for coordinate (" + n + ", " + k
                + ") in a Pascal's Triangle completed with value = " +
                result[0] + ", WORK = " + metrics[0] + ", CPL = " + metrics[1] +
                ", IDEAL_PARALLELISM = " + metrics[2]);

        final TestResults finalResults = new TestResults(result[0], metrics[0],
                metrics[1], metrics[2]);
        assertEquals("Expected the value at (" + n + ", " + k + ") to be " +
                expectedVal + " but got " + finalResults.getVal(), expectedVal,
                finalResults.getVal());
        assertEquals("Expected WORK to be " + expectedWork + " to compute " +
                "the value at coordinate (" + n + ", " + k + ") but got " +
                finalResults.getWork(), expectedWork, finalResults.getWork());
        assertEquals("Expected CPL to be " + expectedCPL + " to compute " +
                "the value at coordinate (" + n + ", " + k + ") but got " +
                finalResults.getCPL(), expectedCPL, finalResults.getCPL());
        assertEquals("Expected ideal parallelism to be " +
                expectedIdealParallelism + " to compute the value at " +
                "coordinate (" + n + ", " + k + ") but got " +
                finalResults.getIdealParallelism(), expectedIdealParallelism,
                finalResults.getIdealParallelism());
    }

    public void testPascals2_1() {
        testDriver(2, 1, 2, 3.0, 2.0, 1.5);
    }

    public void testPascals6_4() {
        testDriver(6, 4, 15, 29.0, 6.0, 29.0 / 6.0);
    }

    public void testPascals10_8() {
        testDriver(10, 8, 45, 89.0, 10.0, 8.9);
    }

    static class TestResults {
        private final int val;
        private final double WORK;
        private final double CPL;
        private final double IDEAL_PARALLELISM;

        public TestResults(final int val, final double WORK, final double CPL,
                final double IDEAL_PARALLELISM) {
            this.val = val;
            this.WORK = WORK;
            this.CPL = CPL;
            this.IDEAL_PARALLELISM = IDEAL_PARALLELISM;
        }

        public int getVal() { return val; }
        public double getWork() { return WORK; }
        public double getCPL() { return CPL; }
        public double getIdealParallelism() { return IDEAL_PARALLELISM; }
    }
}
