package edu.rice.comp322.solutions;

import edu.rice.comp322.provided.trees.GList;
import edu.rice.comp322.provided.trees.Tree;

import java.util.function.BiFunction;

public class TreeSolutions {

    // @TODO Implement tree sum
    public static Integer problemOne(Tree<Integer> tree) {
        if (tree.isEmpty()){
            return 0;
        } else if (tree.children().isEmpty()) {
            return tree.value();
        } else {
            return tree.value() + problemOne(Tree.makeNode(tree.children().head().value(),
                    tree.children().head().children().appendAll(tree.children().tail())));
        }
    }

    // @TODO Implement tree sum using higher order list functions
    public static Integer problemTwo(Tree<Integer> tree) {
        return tree.value() + tree.children().filter(t -> !t.isEmpty()).map(TreeSolutions::problemTwo).foldRight(0, Integer::sum);
    }

    /*
     * Problem 3's solution should be written in the Tree.java class at line 118.
     */

    // @TODO Calculate the sum of the elements of the tree using tree fold
    public static Integer problemFour(Tree<Integer> tree) {
        return tree.fold(0, Integer::sum);
    }

}
