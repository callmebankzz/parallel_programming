package edu.rice.comp322;

import junit.framework.TestCase;
import java.math.BigDecimal;

import static edu.rice.hj.Module0.launchHabaneroApp;

/**
 * Simple Unit test.
 */
public class Lab10ActorCorrectnessTest extends TestCase {

    private final String PI = "3.141592653589793238462643383279502884197169399375105820974944592307816406286";

    public void testPiActor_1000() {
        final String lbl = PerfTestUtils.getTestLabel();
        final int numWorkers = 24;
        final int nterms = 1000;
        final BigDecimal tolerance = BigDecimal.ONE.movePointLeft(nterms);

        PiUtil.setScale(nterms);

        System.out.println("Starting test " + lbl);
        launchHabaneroApp(() -> {
            String pi = new PiActor(numWorkers, tolerance).calcPi();

            assertTrue("1. Bad pi: " + pi, pi.length() > 20);
            assertTrue("2. Bad pi: " + pi, matches(PI, pi.substring(0, 20), 1e5));
        });
        System.out.println("Completed test " + lbl);
    }

    private boolean matches(final String expected, final String actual, final double tolerance) {

        final double expDouble = Double.parseDouble(expected);
        final double actDouble = Double.parseDouble(actual);
        final double errorPercent = Math.abs((100 * (expDouble - actDouble)) / expDouble);
        if (errorPercent >= tolerance) {
            System.out.println("ERROR: Error percent of " + errorPercent + " greater than tolerable limit of " +
                    tolerance);
        }
        return errorPercent < tolerance;
    }

}
