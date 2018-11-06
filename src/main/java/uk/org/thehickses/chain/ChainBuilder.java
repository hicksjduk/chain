package uk.org.thehickses.chain;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChainBuilder
{
    public static <T, U> FunctionChain<T, U> chain(Function<T, U> function)
    {
        return function::apply;
    }

    public static <T> SupplierChain<T> chain(Supplier<T> supplier)
    {
        return supplier::get;
    }

    public static <T> ConsumerChain<T> chain(Consumer<T> consumer)
    {
        return consumer::accept;
    }

    private static <T> Optional<T> nullTolerant(Supplier<T> supplier)
    {
        try
        {
            return Optional.of(supplier.get());
        }
        catch (NullPointerException ex)
        {
            return Optional.empty();
        }
    }

    private ChainBuilder()
    {
    }

    @FunctionalInterface
    public static interface FunctionChain<T, U> extends Function<T, U>
    {
        default <V> FunctionChain<T, V> and(Function<? super U, V> function)
        {
            return andThen(function)::apply;
        }

        default Consumer<T> and(Consumer<? super U> consumer)
        {
            return arg -> consumer.accept(apply(arg));
        }

        default U applyWithDefault(T arg, U defaultIfNull)
        {
            return nullTolerant(() -> apply(arg)).orElse(defaultIfNull);
        }
    }

    @FunctionalInterface
    public static interface SupplierChain<T> extends Supplier<T>
    {
        default Runnable and(Consumer<? super T> consumer)
        {
            return () -> consumer.accept(get());
        }

        default <U> SupplierChain<U> and(Function<? super T, U> function)
        {
            return () -> function.apply(get());
        }

        default T getWithDefault(T defaultIfNull)
        {
            return nullTolerant(this).orElse(defaultIfNull);
        }
    }

    @FunctionalInterface
    public static interface ConsumerChain<T> extends Consumer<T>
    {
        default ConsumerChain<T> and(Consumer<? super T> otherConsumer)
        {
            return andThen(otherConsumer)::accept;
        }

        default <U> FunctionChain<T, U> and(Function<? super T, U> function)
        {
            return arg -> {
                accept(arg);
                return function.apply(arg);
            };
        }
    }
}