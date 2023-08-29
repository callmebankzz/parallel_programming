package edu.rice.comp322.streams;

import java.text.DecimalFormat;
import java.util.*;

import edu.rice.comp322.provided.streams.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import edu.rice.comp322.provided.streams.repos.CustomerRepo;
import edu.rice.comp322.provided.streams.repos.OrderRepo;
import edu.rice.comp322.provided.streams.repos.ProductRepo;

import org.junit.jupiter.api.Assertions;

import org.springframework.boot.test.context.SpringBootTest;
import javax.transaction.Transactional;

import static edu.rice.comp322.solutions.StreamSolutions.*;

@SpringBootTest(classes = DemoApplication.class)
@Slf4j
public class StreamSolutionsTest {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    /**
     * 0) Test problem. To ensure that the repositories are loading properly simply run
     * the test.
     */
    @Transactional
    @Test
    public void problem0() {
        /*
         * Sequential
         */
        log.info("problem 0: verifying data loads correctly ");
        long startTime = System.currentTimeMillis();
        List<Long> result = problemZeroSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem 0 sequential execution time: %1$d ms", (endTime - startTime)));
        Assertions.assertEquals(Arrays.asList(11L, 50L, 30L), result);

        /*
         * Parallel
         */
        log.info("problem 0: verifying data loads correctly ");
        startTime = System.currentTimeMillis();
        List<Long> parResult = problemZeroPar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem 0 parallel execution time: %1$d ms", (endTime - startTime)));
        Assertions.assertEquals(Arrays.asList(11L, 50L, 30L), parResult);
    }



    /**
     * 1) Get the 3 most expensive orders in April
     */
    @Transactional
    @Test
    public void problem1() {


        DecimalFormat round = new DecimalFormat("#.##");

        Set<Double> answer = new HashSet<>(Arrays.asList(533.06, 526.53, 661.45));

        /*
         * Sequential
         */
        log.info("problem1 seq. - Get the 3 most expensive orders in April");
        long startTime = System.currentTimeMillis();
        Set<Double> result = problemOneSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem1 - sequential execution time: %1$d ms", (endTime - startTime)));
        Set<Double> roundedResult = new TreeSet<>();
        result.forEach(e -> roundedResult.add(Double.valueOf(round.format(e))));
        log.info("Values of the 3 most expensive orders in April: " + roundedResult);

        Assertions.assertEquals(answer, roundedResult);

        /*
         * Parallel
         */
        log.info("problem1 par. - Get the 3 most expensive orders in April");
        startTime = System.currentTimeMillis();
        Set<Double> parResult = problemOnePar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem1 - parallel execution time: %1$d ms", (endTime - startTime)));
        Set<Double> roundedResultPar = new TreeSet<>();
        parResult.forEach(e -> roundedResultPar.add(Double.valueOf(round.format(e))));
        log.info("Values of the 3 most expensive orders in April: " + roundedResultPar);

        Assertions.assertEquals(answer, roundedResultPar);

    }


    /**
     * 2) Create a mapping between customer IDs and their order IDs that have a status "PENDING"
     */
    @Transactional
    @Test
    public void problem2() {

        // This is the expected mapping that your solution should compute
        Map<Long, Set<Long>> answer = new HashMap<>();
        answer.put(1L, Set.of(33L, 29L));
        answer.put(2L, Set.of(47L));
        answer.put(3L, Set.of(4L, 40L));
        answer.put(4L, Set.of(23L, 14L));
        answer.put(5L, Set.of(12L));
        answer.put(8L, Set.of(7L));
        answer.put(9L, Set.of(18L));

        /*
         * Sequential
         */
        log.info("problem2 seq. - Create customer ID and order ID mapping");
        long startTime = System.currentTimeMillis();
        Map<Long, Set<Long>> result = problemTwoSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem2 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());

        Assertions.assertEquals(answer, result);

        /*
         * Parallel
         */
        log.info("problem2 par. - Create customer ID and order ID mapping");
        startTime = System.currentTimeMillis();
        result = problemTwoPar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem2 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
        Assertions.assertEquals(answer, result);
    }

}
