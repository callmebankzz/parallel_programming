package edu.rice.comp322.solutions;

import edu.rice.comp322.provided.streams.models.Customer;
import edu.rice.comp322.provided.streams.models.Order;
import edu.rice.comp322.provided.streams.models.Product;
import edu.rice.comp322.provided.streams.provided.DiscountObject;
import edu.rice.comp322.provided.streams.repos.CustomerRepo;
import edu.rice.comp322.provided.streams.repos.OrderRepo;
import edu.rice.comp322.provided.streams.repos.ProductRepo;

import java.util.*;

import java.time.LocalDate;
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

    // TODO: Implement problem one using sequential streams
    public static Double problemOneSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var revenue = orderRepo.findAll().stream().filter(e -> e.getOrderDate().getMonthValue() == 2).map(
                e -> e.getProducts().stream().map(Product::getFullPrice).reduce(0.0, Double::sum)).reduce(
                        0.0, Double::sum);
        return revenue;
    }

    // TODO: Implement problem one using parallel streams
    public static Double problemOnePar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var revenue = orderRepo.findAll().parallelStream().filter(e -> e.getOrderDate().getMonthValue() == 2).map(
                e -> e.getProducts().stream().map(Product::getFullPrice).reduce(0.0, Double::sum)).reduce(
                0.0, Double::sum);

        return revenue;
    }

    // TODO: Implement problem two using sequential streams
    public static Set<Long> problemTwoSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var recent = orderRepo.findAll().stream().map(o -> Map.entry(o.getOrderDate(),
                o.getId())).sorted(Collections.reverseOrder(Map.Entry.comparingByKey())).collect(
                        Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue,
                                toSet()))).values().stream().flatMap(Set::stream).collect(Collectors.toSet());;


        return recent;
    }

    // TODO: Implement problem two using parallel streams
    public static Set<Long> problemTwoPar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var recent = orderRepo.findAll().parallelStream().map(o -> Map.entry(o.getOrderDate(),
                o.getId())).sorted(Collections.reverseOrder(Map.Entry.comparingByKey())).collect(
                Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue,
                        toSet()))).values().parallelStream().flatMap(Set::stream).collect(Collectors.toSet());;

        return recent;
    }

    // TODO: Implement problem three using sequential streams
    public static Long problemThreeSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem three using parallel streams
    public static Long problemThreePar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem four using sequential streams
    public static Double problemFourSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        var discounts = orderRepo.findAll().stream().filter(e -> e.getOrderDate().getMonthValue() == 3)
                .filter(e -> e.getOrderDate().getYear() == 2021);
        return null;
    }

    // TODO: Implement problem four using parallel streams
    public static Double problemFourPar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem five using sequential streams
    public static Map<Long, Double> problemFiveSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem five using parallel streams
    public static Map<Long, Double> problemFivePar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem six using sequential streams
    public static Map<String, Double> problemSixSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem six using parallel streams
    public static Map<String, Double> problemSixPar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem seven using sequential streams
    public static Map<Long, Set<Long>> problemSevenSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem seven using parallel streams
    public static Map<Long, Set<Long>> problemSevenPar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem eight using sequential streams
    public static Map<Long, Double> problemEightSeq(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }

    // TODO: Implement problem eight using parallel streams
    public static Map<Long, Double> problemEightPar(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        return null;
    }
}
