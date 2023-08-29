package edu.rice.comp322;

import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.api.HjSuspendingProcedure;
import edu.rice.hj.runtime.config.HjSystemProperty;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Random;

import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.*;

/**
 * Unit test for user-specified sorting algorithm.
 * <p/>
 * DO NOT MODIFY THIS FILE.
 */
public class Homework3CorrectnessTest extends TestCase {

    protected static DataItem[] generateAlreadySortedInput(final int size) {

        final DataItem[] A = new DataItem[size];

        for (int i = 0; i < size; i++) {
            A[i] = new DataItem(i + 1);
        }

        return A;
    }

    protected static DataItem[] generateReverseSortedInput(final int size) {

        final DataItem[] A = new DataItem[size];

        for (int i = 0; i < size; i++) {
            A[i] = new DataItem(size - i);
        }

        return A;
    }

    protected static DataItem[] generateTriangleDataInput(final int size) {

        final DataItem[] A = new DataItem[size];

        final int mid = size / 2;
        for (int i = 0; i < size; i++) {
            final int data;
            if (i < mid) {
                data = i * 2;
            } else {
                data = ((size - i) * 2) + 1;
            }
            A[i] = new DataItem(data);
        }

        return A;
    }

    protected static DataItem[] generateRandomDataInput(final int size) {

        final DataItem[] A = new DataItem[size];

        final Random rand = new Random(size);
        for (int i = 0; i < size; i++) {
            A[i] = new DataItem(rand.nextInt(10 * size));
        }

        return A;
    }

    protected Sort retrieveSortInstance() {
        return Homework3.sortInstance();
    }

    protected final String name() {
        return getClass().getSimpleName();
    }

    public void testEmptyInput() {
        System.out.println(name() + ".testEmptyInput() starts...");

        final DataItem[] dataArray = generateAlreadySortedInput(0);
        runTestKernel(dataArray);

        System.out.println(name() + ".testEmptyInput() ends.");
    }

    public void testUnitLengthInput() {
        System.out.println(name() + ".testUnitLengthInput() starts...");

        final DataItem[] dataArray = generateAlreadySortedInput(1);
        runTestKernel(dataArray);

        System.out.println(name() + ".testUnitLengthInput() ends.");
    }

    public void testAlreadySortedInput1K() {
        System.out.println(name() + ".testAlreadySortedInput1K() starts...");

        final DataItem[] dataArray = generateAlreadySortedInput(1_024);
        runTestKernel(dataArray);

        System.out.println(name() + ".testAlreadySortedInput1K() ends.");
    }

    public void testReverseSortedInput1K() {
        System.out.println(name() + ".testReverseSortedInput1K() starts...");

        final DataItem[] dataArray = generateReverseSortedInput(1_024);
        runTestKernel(dataArray);

        System.out.println(name() + ".testReverseSortedInput1K() ends.");
    }

    public void testTriangleDataInput1K() {
        System.out.println(name() + ".testTriangleDataInput1K() starts...");

        final DataItem[] dataArray = generateTriangleDataInput(1_024);
        runTestKernel(dataArray);

        System.out.println(name() + ".testTriangleDataInput1K() ends.");
    }

    public void testRandomDataInput1K() {
        System.out.println(name() + ".testRandomDataInput1K() starts...");

        final DataItem[] dataArray = generateRandomDataInput(1_024);
        runTestKernel(dataArray);

        System.out.println(name() + ".testRandomDataInput1K() ends.");
    }

    public void testAlreadySortedInput2K() {
        System.out.println(name() + ".testAlreadySortedInput2K() starts...");

        final DataItem[] dataArray = generateAlreadySortedInput(2_048);
        runTestKernel(dataArray);

        System.out.println(name() + ".testAlreadySortedInput2K() ends.");
    }

    public void testReverseSortedInput2K() {
        System.out.println(name() + ".testReverseSortedInput2K() starts...");

        final DataItem[] dataArray = generateReverseSortedInput(2_048);
        runTestKernel(dataArray);

        System.out.println(name() + ".testReverseSortedInput2K() ends.");
    }

    public void testTriangleDataInput2K() {
        System.out.println(name() + ".testTriangleDataInput2K() starts...");

        final DataItem[] dataArray = generateTriangleDataInput(2_048);
        runTestKernel(dataArray);

        System.out.println(name() + ".testTriangleDataInput2K() ends.");
    }

    public void testRandomDataInput2K() {
        System.out.println(name() + ".testRandomDataInput2K() starts...");

        final DataItem[] dataArray = generateRandomDataInput(2_048);
        runTestKernel(dataArray);

        System.out.println(name() + ".testRandomDataInput2K() ends.");
    }

    public void testAlreadySortedInput4K() {
        System.out.println(name() + ".testAlreadySortedInput4K() starts...");

        final DataItem[] dataArray = generateAlreadySortedInput(4_096);
        runTestKernel(dataArray);

        System.out.println(name() + ".testAlreadySortedInput4K() ends.");
    }

    public void testReverseSortedInput4K() {
        System.out.println(name() + ".testReverseSortedInput4K() starts...");

        final DataItem[] dataArray = generateReverseSortedInput(4_096);
        runTestKernel(dataArray);

        System.out.println(name() + ".testReverseSortedInput4K() ends.");
    }

    public void testTriangleDataInput4K() {
        System.out.println(name() + ".testTriangleDataInput4K() starts...");

        final DataItem[] dataArray = generateTriangleDataInput(4_096);
        runTestKernel(dataArray);

        System.out.println(name() + ".testTriangleDataInput4K() ends.");
    }

    public void testRandomDataInput4K() {
        System.out.println(name() + ".testRandomDataInput4K() starts...");

        final DataItem[] dataArray = generateRandomDataInput(4_096);
        runTestKernel(dataArray);

        System.out.println(name() + ".testRandomDataInput4K() ends.");
    }

    protected static HjSuspendingProcedure<DataItem[]> sequentialProcedure(final Sort sortInstance) {
        return sortInstance::seqSort;
    }

    protected static HjSuspendingProcedure<DataItem[]> parallelProcedure(final Sort sortInstance) {
        return sortInstance::parSort;
    }

    /**
     * Checks that the array is sorted.
     *
     * @param inA The array to check
     * @return whether the array is sorted
     */
    @SuppressWarnings("unchecked")
    protected static boolean valid(final DataItem[] inA) {
        for (int i = 1; i < inA.length; i++) {
            if (inA[i].compareTo(inA[i - 1]) < 0) {
                return false;
            }
        }
        return true;
    }

    protected final void runTestKernel(final DataItem[] dataArray) {
        System.out.println(" Input array length = " + dataArray.length);

        final Sort sortInstance = retrieveSortInstance();

        final DataItem[] seqDataArray = Arrays.copyOf(dataArray, dataArray.length);
        final HjSuspendingProcedure<DataItem[]> seqSortFunction = sequentialProcedure(sortInstance);
        final HjMetrics seqMetrics = executeSortAndRetrieveMetrics(seqDataArray, sortInstance, seqSortFunction, "Sequential");

        final DataItem[] parDataArray = Arrays.copyOf(dataArray, dataArray.length);
        final HjSuspendingProcedure<DataItem[]> parSortFunction = parallelProcedure(sortInstance);
        final HjMetrics parMetrics = executeSortAndRetrieveMetrics(parDataArray, sortInstance, parSortFunction, "Parallel");

        final double speedUp = parMetrics.criticalPathLength() > 0 ?
                (1.0 * seqMetrics.criticalPathLength()) / parMetrics.criticalPathLength() :
                Double.NaN;
        System.out.println(" Speed-Up = " + speedUp);
        if (dataArray.length > 1) {
            assertTrue("Parallel CPL was 0", parMetrics.criticalPathLength() > 0);
            assertTrue("Sequential CPL was 0", seqMetrics.criticalPathLength() > 0);

            // Different sorting algorithms have different expected speedups.
            final double expectedAbstractSpeedup;
            if (sortInstance instanceof MergeSort) {
                expectedAbstractSpeedup = 1.6;
            } else if (sortInstance instanceof QuickSort) {
                expectedAbstractSpeedup = 2.7;
            } else {
                throw new UnsupportedOperationException("Unknown sort type " + sortInstance.getClass().getName() +
                        " found");
            }

            assertTrue("CPL (" + parMetrics.criticalPathLength() + ") is large relative to WORK (" +
                    parMetrics.totalWork() + "), your parallel solution has no/little parallelism",
                    speedUp > expectedAbstractSpeedup);
        }

        if (seqMetrics.totalWork() > 0) {
            final double workIncreaseRatio = (1.0 * parMetrics.totalWork()) / seqMetrics.totalWork();
            assertTrue("Parallel work should not increase by more than 2X, actual = " + workIncreaseRatio, workIncreaseRatio < 2.0);
        }

    }

    protected static HjMetrics runKernel(
        final HjSuspendingProcedure<DataItem[]> sortFunctionCall,
        final DataItem[] dataArray) {

        final HjMetrics[] actualMetrics = {null};

        launchHabaneroApp(
            () -> {

                finish(() -> {
                    // ensure we create at least one task so abstract metrics works properly
                    async(() -> {
                        sortFunctionCall.apply(dataArray);
                    });
                });
            },
            () -> {
                actualMetrics[0] = abstractMetrics();
            });

        return actualMetrics[0];
    }

    private HjMetrics executeSortAndRetrieveMetrics(
            final DataItem[] dataArray,
            final Sort sortInstance,
            final HjSuspendingProcedure<DataItem[]> sortFunctionCall,
            final String sortMode) {

        HjSystemProperty.enableAbstractMetricsFlags();
        HjSystemProperty.abstractMetrics.set(true);
        HjSystemProperty.speedUpGraph.set(false);
        HjSystemProperty.executionGraph.set(false);
        HjSystemProperty.showBuildVersion.set(false);

        final HjMetrics metrics = runKernel(sortFunctionCall, dataArray);

        assertTrue("Total work is 0!", metrics.totalWork() >= 0);
        assertTrue("CPL is 0!", metrics.criticalPathLength() >= 0);

        final boolean[] sortValid = {false};
        HjSystemProperty.resetConfigurations();
        launchHabaneroApp(() -> {
            sortValid[0] = valid(dataArray);
        });
        assertTrue(sortMode + " Sort for " + sortInstance.name() + " failed!", sortValid[0]);

        System.out.println(" " + sortInstance.name() + " " + sortMode + " metrics: WORK = " +
                                   metrics.totalWork() + ", CPL = " + metrics.criticalPathLength());

        return metrics;
    }
}
