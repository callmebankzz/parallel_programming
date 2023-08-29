package edu.rice.comp322;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a serial version of computing pi that uses a threshold value Original Source Code:
 * http://research.cs.queensu.ca/home/cmpe212/Fall2011/Lab6/Lab6.java
 */
public class PiSerial {
    private final int nterms;

    /**
     * Constructor.
     */
    public PiSerial(final int nterms) {
        this.nterms = nterms;
    }

    /**
     * Compute Pi out to the specified number of terms.
     */
    public String calcPi() {
        BigDecimal sum = BigDecimal.ZERO;

        // Uses the BBP formula to estimate pi using BigDecimals
        // http://mathworld.wolfram.com/BBPFormula.html
        int k = 0;
        final BigDecimal tolerance = BigDecimal.ONE.movePointLeft(nterms);
        while (true) {
            final BigDecimal term = PiUtil.calculateBbpTerm(k);
            sum = sum.add(term);
            k++;

            // dynamically determine when to stop
            if (term.compareTo(tolerance) <= 0) {
                break;
            }
        }

        return sum.toPlainString();
    }
}
