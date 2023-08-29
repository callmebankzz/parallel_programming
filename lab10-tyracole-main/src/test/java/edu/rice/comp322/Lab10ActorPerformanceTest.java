package edu.rice.comp322;

import junit.framework.TestCase;
import java.math.BigDecimal;

import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.numWorkerThreads;

/**
 * Simple Unit test.
 */
public class Lab10ActorPerformanceTest extends TestCase {

    private final String PI = "3.141592653589793238462643383279502884197169399375105820974944592307816406286";

    public void testPiActor_10000() {
        final String testLabel = PerfTestUtils.getTestLabel();
        final int numWorkers = 24;
        final int nterms = 10000;
        final BigDecimal tolerance = BigDecimal.ONE.movePointLeft(nterms);

        PiUtil.setScale(nterms);

        launchHabaneroApp(() -> {
            final PiSerial[] seq = new PiSerial[1];
            final PiActor[] par = new PiActor[1];

            final String[] seqResult = new String[1];
            final String[] parResult = new String[1];

            final PerfTestUtils.PerfTestResults perfResults = PerfTestUtils.runPerfTest(testLabel,
                () -> {
                    // pre parallel
                    par[0] = new PiActor(numWorkers, tolerance);
                },
                () -> {
                    // parallel main
                    parResult[0] = par[0].calcPi();
                },
                () -> {
                    // post parallel
                },
                () -> {
                    // pre sequential
                    seq[0] = new PiSerial(nterms);
                },
                () -> {
                    // sequential main
                    seqResult[0] = seq[0].calcPi();
                },
                () -> {
                    // post sequential
                },
                () -> {
                    // final checks
                    StringBuilder sb = new StringBuilder();
                    sb.append("Pi computed by sequential and parallel versions do not match. Sequential computed:\n\n");
                    sb.append(seqResult[0] + "\n\n");
                    sb.append("Parallel computed:\n\n");
                    sb.append(parResult[0] + "\n");

                    assertEquals(sb.toString(), seqResult[0], parResult[0]);
                }, 2 /* # parallel runs */, 2 /* # seq runs */, numWorkerThreads());

            PerfTestUtils.printPerfInfo(perfResults, "parallel actor implementation", testLabel);
        });
    }

    private boolean matches(final String expected, final String actual, final double tolerance) {
        final double expDouble = Double.parseDouble(expected);
        final double actDouble = Double.parseDouble(actual);
        final double errorPercent = Math.abs((100 * (expDouble - actDouble)) / expDouble);
        if (errorPercent >= tolerance) {
            System.out.println("ERROR: Error percent of " + errorPercent + " greater than tolerable limit of " + tolerance);
        }
        return errorPercent < tolerance;
    }

}
