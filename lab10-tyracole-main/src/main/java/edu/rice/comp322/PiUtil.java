package edu.rice.comp322;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A utility class for computing pi.
 */
public class PiUtil {

    private static final BigDecimal one = BigDecimal.ONE;
    private static final BigDecimal two = new BigDecimal(2);
    private static final BigDecimal four = new BigDecimal(4);
    private static final BigDecimal sixteen = new BigDecimal(16);

    private static int scale = -1;

    public static void setScale(final int newVal) {
        scale = newVal;
    }

    /**
     * Compute a single term of the Bailey-Borwein-Plouffe formula.
     */
    public static BigDecimal calculateBbpTerm(final int n) {
        assert scale >= 0;

        final RoundingMode roundMode = RoundingMode.HALF_EVEN;

        final int eightN = 8 * n;
        BigDecimal term = four.divide(new BigDecimal(eightN + 1), scale, roundMode);
        term = term.subtract(two.divide(new BigDecimal(eightN + 4), scale, roundMode));
        term = term.subtract(one.divide(new BigDecimal(eightN + 5), scale, roundMode));
        term = term.subtract(one.divide(new BigDecimal(eightN + 6), scale, roundMode));
        term = term.divide(sixteen.pow(n), scale, roundMode);
        return term;
    }

}
