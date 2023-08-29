package edu.rice.comp322;

import edu.rice.hj.api.HjFuture;
import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.config.HjSystemProperty;
import edu.rice.hj.runtime.metrics.AbstractMetricsManager;

import static edu.rice.hj.Module1.*;

/**
 * Pascal's Triangle --- Computes (n C k) using futures.
 * The purpose of this example is to illustrate abstract metrics while using
 * futures. C(n, k) = C(n - 1, k - 1) + C(n - 1, k)
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 * @author Vivek Sarkar (vsarkar@rice.edu)
 * @author Mack Joyner (mjoyner@rice.edu)
 */
public class PascalsTriangle {

    /**
     * Compute the value in a Pascal's Triangle at coordinate (n, k)
     * recursively.
     *
     * <p>TODO Modify chooseHelper to execute its child chooseHelper calls in parallel
     * asynchronously using async and finish.</p>
     *
     * @param n Row coordinate for this value
     * @param k Column coordinate for this value
     * @return Value at (n, k)
     */
    private static Integer chooseHelper(final int n, final int k) throws SuspendableException {
        if (k == 0 || k == n) {
            // Handle the base initialization case
            doWork(1);
            return 1;
        } else {
            final Integer[] left = new Integer[1];
            final Integer[] right = new Integer[1];
            finish(() -> {
                async(() -> {
                        left[0] = chooseHelper(n - 1, k - 1);});
                async(() -> {
                        right[0] = chooseHelper(n - 1, k);});
                    });
            doWork(1);
            return left[0] + right[0];
        }
    }

    /**
     * Compute the value for the element in a Pascal's Triangle at coordinate
     * (N, K) using futures.
     *
     * @param targetN The row coordinate in the triangle. The first row is row
     *                zero.
     * @param targetK The column coordinate in the triangle. The first element
     *          in each row is at offset zero.
     * @return The value stored at coordinate (N, K)
     */
    public static int choose(final int targetN, final int targetK)
            throws SuspendableException {

        // Calculate the result recursively
        final int result = chooseHelper(targetN, targetK);

        return result;
    }
}
