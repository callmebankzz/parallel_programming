package edu.rice.comp322;

import junit.framework.TestCase;

import java.util.Random;

import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.async;
import static edu.rice.hj.Module2.isolated;

/**
 * Unit test for simple App.
 */
public class Lab9CorrectnessTest extends TestCase {

    private static final int DEFAULT_N = 1000;
    private static final int MAX_RAND = 8000;
    private static final int NUM_TASKS = 8;

    protected static DoubleLinkedList initializeList(final int n, final int seed) {
        final Random myRand = new Random(seed);
        var list = new DoubleLinkedList();
        for (int i = 0; i < n; i++) {
            list.insert(myRand.nextInt(n));
        }
        return list;
    }

    public void testListInsertions(){
        launchHabaneroApp(() -> {
            DoubleLinkedList list = new DoubleLinkedList();
            DoubleLinkedList concurrentList = new DoubleLinkedList();
            for (int i = 0; i < NUM_TASKS; i++) {
                Random rand = new Random(i);
                for (int j = 0; j < DEFAULT_N; j++) {
                    var val = rand.nextInt(MAX_RAND);
                    list.insert(val);
                }
            }
            finish(() -> {
                for (int i = 0; i < NUM_TASKS; i++) {
                    final int ii = i;
                    async(() -> {
                        Random rand = new Random(ii);
                        for (int j = 0; j < DEFAULT_N; j++) {
                            var val = rand.nextInt(MAX_RAND);
                            concurrentList.concurrentInsert(val);
                        }
                    });
                }
            });
            assertTrue(concurrentList.wellFormed());
            assertEquals(list, concurrentList);
        });

    }

    public void testListDeletions(){
        launchHabaneroApp(() -> {
            DoubleLinkedList list = initializeList(NUM_TASKS * DEFAULT_N, 4);
            DoubleLinkedList concurrentList = initializeList(NUM_TASKS * DEFAULT_N, 4);
            for (int i = 0; i < NUM_TASKS; i++) {
                Random rand = new Random(i);
                for (int j = 0; j < DEFAULT_N; j++) {
                    var val = rand.nextInt(4);
                    list.remove(val);
                }
            }
            finish(() -> {
                for (int i = 0; i < NUM_TASKS; i++) {
                    final int ii = i;
                    async(() -> {
                        Random rand = new Random(ii);
                        for (int j = 0; j < DEFAULT_N; j++) {
                            var val = rand.nextInt(4);
                            concurrentList.concurrentRemove(val);
                        }
                    });
                }
            });
            assertTrue(concurrentList.wellFormed());
            assertEquals(list, concurrentList);
        });

    }

    public void testListPerformance(){
        launchHabaneroApp(() -> {
            DoubleLinkedList list = initializeList(NUM_TASKS * DEFAULT_N, MAX_RAND);
            DoubleLinkedList concurrentList = initializeList(NUM_TASKS * DEFAULT_N, MAX_RAND);
            var start = System.currentTimeMillis();
            for (int i = 0; i < NUM_TASKS; i++) {
                Random rand = new Random(i);
                for (int j = 0; j < DEFAULT_N * NUM_TASKS; j++) {
                    var val = rand.nextInt(MAX_RAND);
                    var prob = ((double) rand.nextInt(MAX_RAND)) / MAX_RAND;
                    if (prob < 0.1) {
                        list.insert(val);
                    } else if (prob < 0.2) {
                        list.remove(val);
                    } else {
                        list.contains(val);
                    }
                }
            }
            var seqTime = System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            finish(() -> {
                for (int i = 0; i < NUM_TASKS; i++) {
                    final int ii = i;
                    async(() -> {
                        Random rand = new Random(ii);
                        for (int j = 0; j < DEFAULT_N * NUM_TASKS; j++) {
                            var val = rand.nextInt(MAX_RAND);
                            var prob = ((double) rand.nextInt(MAX_RAND)) / MAX_RAND;
                            if (prob < 0.001) {
                                concurrentList.concurrentInsert(val);
                            } else if (prob < 0.002) {
                                concurrentList.concurrentRemove(val);
                            } else {
                                concurrentList.concurrentContains(val);
                            }
                        }
                    });
                }
            });
            var parTime = System.currentTimeMillis() - start;
            double speedup = ((double)seqTime)/parTime;
            System.out.println("Sequential execution time: " + seqTime + " milliseconds.");
            System.out.println("Parallel execution time: " + parTime + " milliseconds.");
            System.out.println("Parallel speedup: " + speedup + "x");
            assertTrue(concurrentList.wellFormed());
            assertTrue(speedup > 1.3);
        });

    }


}
