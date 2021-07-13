package helpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Assertions {

    private Assertions(){}

    public static void assertThat(Supplier<? extends RuntimeException> error, boolean assertion){
        if (!assertion) throw error.get();
    }

    public static <T> void assertContains(Supplier<? extends RuntimeException> error, Collection<T> collection, T... elem) {
        if (!collection.containsAll(Arrays.asList(elem)))
            throw error.get();
    }

}
