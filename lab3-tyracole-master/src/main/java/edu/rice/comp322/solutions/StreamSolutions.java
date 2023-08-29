package edu.rice.comp322.solutions;

import edu.rice.comp322.provided.streams.models.Product;
import edu.rice.comp322.provided.streams.repos.CustomerRepo;
import edu.rice.comp322.provided.streams.repos.OrderRepo;
import edu.rice.comp322.provided.streams.repos.ProductRepo;

import java.util.*;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * A class containing all of the students implemented solutions to the stream problems.
 */
public class StreamSolutions {

    /**
     * Use this function with the appropriate streams solution test to ensure repos load correctly.
     */
    public static List<Long> problemZeroSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        List<Long> counts = new ArrayList<>();
        counts.add(customerRepo.findAll().stream().count());
        counts.add(orderRepo.findAll().stream().count());
        counts.add(productRepo.findAll().stream().count());

        return counts;
    }

    /**
     * Use this function with the appropriate streams solution test to ensure repos load correctly.
     */
    public static List<Long> problemZeroPar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        List<Long> counts = new ArrayList<>();
        counts.add(customerRepo.findAll().parallelStream().count());
        counts.add(orderRepo.findAll().parallelStream().count());
        counts.add(productRepo.findAll().parallelStream().count());

        return counts;
    }

    /**
     * Problem One: Find the prices of the top 3 highest value orders in the month of April
     */
    // TODO: Implement problem one using sequential streams
    public static Set<Double> problemOneSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var orders = orderRepo.findAll().stream().filter(e -> e.getOrderDate().getMonthValue() == 4).map(
                e -> e.getProducts().stream().map(prod -> prod.getFullPrice()).reduce(0.0, (a, b) -> a+b)).sorted(Comparator.reverseOrder()).limit(3).collect(toSet());
        return orders;
    }

    /**
     * Problem One: Find the prices of the top 3 highest value orders in the month of April
     */
    // TODO: Implement problem one using parallel streams
    public static Set<Double> problemOnePar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var orders = orderRepo.findAll().parallelStream().filter(e -> e.getOrderDate().getMonthValue() == 4).map(
                e -> e.getProducts().stream().map(prod -> prod.getFullPrice()).reduce(0.0, (a, b) -> a+b)).sorted(Comparator.reverseOrder()).limit(3).collect(toSet());
        return orders;
    }

    /**
     *  Problem Two: Create a mapping between customer IDs and their order IDs whose status is "PENDING"
     */
    // TODO: Implement problem two using sequential streams
    public static Map<Long, Set<Long>>  problemTwoSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var mapping = orderRepo.findAll().stream().filter(e -> e.getStatus().equals("PENDING")).map(e -> Map.entry(e.getCustomer().getId(),
                e.getId())).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, toSet())));
        return mapping;
    }

    /**
     *  Problem Two: Create a mapping between customer IDs and the order IDs whose status is "PENDING"
     */
    // TODO: Implement problem two using parallel streams
    public static Map<Long, Set<Long>>  problemTwoPar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var mapping = orderRepo.findAll().parallelStream().filter(e -> e.getStatus().equals("PENDING")).map(e -> Map.entry(e.getCustomer().getId(),
                e.getId())).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, toSet())));
        return mapping;
    }
}
