package edu.rice.comp322;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Shams Imam (shams@rice.edu)
 */
public class StatisticsHelper {

    protected static final double tolerance = 0.20;

    private static List<Double> sanitize(final List<Double> rawList) {
        if (rawList.isEmpty()) {
            return new ArrayList<>(0);
        }

        Collections.sort(rawList);
        final int rawListSize = rawList.size();

        final List<Double> resultList = new ArrayList<>();
        final double median = rawList.get(rawListSize / 2);
        final double allowedMin = (1 - tolerance) * median;
        final double allowedMax = (1 + tolerance) * median;

        for (final double loopVal : rawList) {
            if (loopVal >= allowedMin && loopVal <= allowedMax) {
                resultList.add(loopVal);
            }
        }
        return resultList;
    }

    private static double arithmeticMean(final Collection<Double> execTimes) {

        double sum = 0;

        for (final double execTime : execTimes) {
            sum += execTime;
        }

        return (sum / execTimes.size());
    }

    /**
     * Print a summary of statistics on the provided execution times.
     */
    public static void processExecutionTimes(final String benchmarkName, final List<Double> rawExecTimes) {

        final String execTimeOutputFormat = "%23s %20s: %9.3f ms \n";
        final String argOutputFormat = "%25s = %-10s \n";

        Collections.sort(rawExecTimes);
        final List<Double> execTimes = sanitize(rawExecTimes);
        System.out.println("Execution - Summary: ");
        System.out.printf(argOutputFormat, "Total executions", rawExecTimes.size());
        System.out.printf(argOutputFormat, "Filtered executions", execTimes.size());
        System.out.printf(execTimeOutputFormat, benchmarkName, " Best Time", execTimes.get(0));
        System.out.printf(execTimeOutputFormat, benchmarkName, " Worst Time", execTimes.get(execTimes.size() - 1));
        System.out.printf(execTimeOutputFormat, benchmarkName, " Arith. Mean Time", arithmeticMean(execTimes));

        System.out.println();
    }


}
