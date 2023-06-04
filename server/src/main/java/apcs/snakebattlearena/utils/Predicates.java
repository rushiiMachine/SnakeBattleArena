package apcs.snakebattlearena.utils;

import java.util.function.Predicate;

public class Predicates {
    /**
     * Port of <code>Predicate#not(Predicate)</code> from the Java 9+ API.
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }
}
