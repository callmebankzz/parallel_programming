package edu.rice.comp322;

import edu.rice.hj.api.HjSuspendable;

import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.async;

/**
 * <p>edu.rice.comp322.HelloWorldError class.</p>
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public class HelloWorldError {

    /**
     * Constant <code>s="HelloWorld"</code>.
     */
    public static final String s = "HelloWorld";

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) {

        launchHabaneroApp(() -> {

            finish(() -> {
                // HjRunnable is a functional interface.
                // It has one unimplemented method: void run()
                // The statement below is using lambda expressions to create an instance of HjRunnable
                // Note we are avoiding the verbose syntax to create anonymous inner classes with lambdas.
                final HjSuspendable aRunnable = () -> {
                    System.out.println("First: " + s);
                };
                // We pass async an instance of HjRunnable to run asynchronously
                async(aRunnable);

                // Instead we can also directly inline the lambda expression into the async call.
                async(() -> {
                    // TODO: Uncomment and fix the error by changing 'ss' to 's' to gt rid of the compiler error
                    System.out.println("Second: " + s);
                });
            });

        });
    }
}
