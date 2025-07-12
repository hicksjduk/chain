package uk.org.thehickses.chain;

import static java.util.Objects.*;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * A utility class for chaining together functional interfaces, in all combinations that make sense. Also provides
 * null-tolerant execution of those interfaces that return a value, where a supplied default value is returned if the
 * execution results in a {@link NullPointerException} or a null result.
 *
 * @author Jeremy Hicks
 */
public class Chain
{
    /**
     * Creates a chain containing the specified function.
     * 
     * @param function
     *            the function.
     * @return the chain.
     */
    public static <T, R> FunctionChain<T, R> of(Function<T, R> function)
    {
        requireNonNull(function);
        return function::apply;
    }

    /**
     * Creates a chain containing the specified supplier.
     * 
     * @param supplier
     *            the supplier.
     * @return the chain.
     */
    public static <T> SupplierChain<T> of(Supplier<T> supplier)
    {
        requireNonNull(supplier);
        return supplier::get;
    }

    /**
     * Creates a chain containing the specified consumer.
     * 
     * @param consumer
     *            the consumer.
     * @return the chain.
     */
    public static <T> ConsumerChain<T> of(Consumer<T> consumer)
    {
        requireNonNull(consumer);
        return consumer::accept;
    }

    /**
     * Creates a chain containing the specified runnable.
     * 
     * @param runnable
     *            the runnable.
     * @return the chain.
     */
    public static RunnableChain of(Runnable runnable)
    {
        requireNonNull(runnable);
        return runnable::run;
    }

    private static <T> Optional<T> getNullTolerant(Supplier<T> supplier)
    {
        try
        {
            return Optional.ofNullable(supplier.get());
        }
        catch (NullPointerException ex)
        {
            return Optional.empty();
        }
    }

    private Chain()
    {
    }

    /**
     * A chainable function.
     *
     * @param <T>
     *            the type of the argument to the function.
     * @param <R>
     *            the type of the result of the function.
     */
    @FunctionalInterface
    public static interface FunctionChain<T, R> extends Function<T, R>
    {
        /**
         * Chains together this function with the specified function.
         * 
         * @param function
         *            the function.
         * @return a chainable function that returns the result of applying the specified function to the result of this
         *         function.
         */
        default <V> FunctionChain<T, V> and(Function<? super R, V> function)
        {
            requireNonNull(function);
            return andThen(function)::apply;
        }

        /**
         * Chains together this function with the specified consumer.
         * 
         * @param consumer
         *            the consumer.
         * @return a chainable consumer that applies this function to the argument, and passes the result to the
         *         specified consumer.
         */
        default ConsumerChain<T> and(Consumer<? super R> consumer)
        {
            requireNonNull(consumer);
            return arg -> consumer.accept(apply(arg));
        }

        /**
         * Adds null tolerance to this function, returning the supplied default value if the application of the function
         * throws a {@link NullPointerException}, or returns a null result.
         * 
         * @param defaultIfNull
         *            the default to use.
         * @return the null-tolerant function.
         */
        default FunctionChain<T, R> withDefault(R defaultIfNull)
        {
            return arg -> getNullTolerant(() -> apply(arg)).orElse(defaultIfNull);
        }
    }

    /**
     * A chainable supplier.
     *
     * @param <T>
     *            the type of the result of the supplier.
     */
    @FunctionalInterface
    public static interface SupplierChain<T> extends Supplier<T>
    {
        /**
         * Chains together this supplier with the specified consumer.
         * 
         * @param consumer
         *            the consumer.
         * @return a chainable runnable that passes the result of the supplier to the consumer.
         */
        default RunnableChain and(Consumer<? super T> consumer)
        {
            requireNonNull(consumer);
            return () -> consumer.accept(get());
        }

        /**
         * Chains together this supplier with the specified function.
         * 
         * @param function
         *            the function.
         * @return a chainable supplier that returns the result of applying the function to the result of the supplier.
         */
        default <R> SupplierChain<R> and(Function<? super T, R> function)
        {
            requireNonNull(function);
            return () -> function.apply(get());
        }

        /**
         * Adds null tolerance to this supplier, returning the supplied default value if the supplier throws a
         * {@link NullPointerException}, or returns a null result.
         * 
         * @param defaultIfNull
         *            the default to use.
         * @return the null-tolerant supplier.
         */
        default SupplierChain<T> withDefault(T defaultIfNull)
        {
            return () -> getNullTolerant(this).orElse(defaultIfNull);
        }
    }

    /**
     * A chainable consumer.
     *
     * @param <T>
     *            the type of the argument to the consumer.
     */
    @FunctionalInterface
    public static interface ConsumerChain<T> extends Consumer<T>
    {
        /**
         * Chains together this consumer with the specified consumer.
         * 
         * @param consumer
         *            the consumer.
         * @return a chainable consumer that passes the input argument to this consumer, then to the other consumer.
         */
        default ConsumerChain<T> and(Consumer<? super T> consumer)
        {
            requireNonNull(consumer);
            return andThen(consumer)::accept;
        }

        /**
         * Chains together this consumer with the specified function.
         * 
         * @param function
         *            the function.
         * @return a chainable function that passes the input argument to the consumer, then applies the function to the
         *         same argument and returns the result.
         */
        default <R> FunctionChain<T, R> and(Function<? super T, R> function)
        {
            requireNonNull(function);
            return arg ->
                {
                    accept(arg);
                    return function.apply(arg);
                };
        }
    }

    /**
     * A chainable runnable.
     */
    @FunctionalInterface
    public static interface RunnableChain extends Runnable
    {
        /**
         * Chains together this runnable with the specified runnable.
         * 
         * @param runnable
         *            the runnable.
         * @return a chainable runnable that invokes this runnable, then the other runnable.
         */
        default RunnableChain and(Runnable runnable)
        {
            requireNonNull(runnable);
            return () ->
                {
                    run();
                    runnable.run();
                };
        }

        /**
         * Chains together this consumer with the specified supplier.
         * 
         * @param supplier
         *            the supplier.
         * @return a chainable supplier that runs this runnable, then invokes the supplier and returns the result.
         */
        default <R> SupplierChain<R> and(Supplier<R> supplier)
        {
            requireNonNull(supplier);
            return () ->
                {
                    run();
                    return supplier.get();
                };
        }
    }

    /**
     * A chainable function.
     *
     * @param <T>
     *            the type of the argument to the function.
     * @param <R>
     *            the type of the result of the function.
     */
    @FunctionalInterface
    public static interface ToIntFunctionChain<T> extends ToIntFunction<T>
    {
        /**
         * Chains together this function with the specified function.
         * 
         * @param function
         *            the function.
         * @return a chainable function that returns the result of applying the specified function to the result of this
         *         function.
         */
        default <V> FunctionChain<T, V> and(IntFunction<V> function)
        {
            requireNonNull(function);
            return arg -> function.apply(this.applyAsInt(arg));
        }

        /**
         * Chains together this function with the specified function.
         * 
         * @param function
         *            the function.
         * @return a chainable function that returns the result of applying the specified function to the result of this
         *         function.
         */
        default ToIntFunctionChain<T> and(IntUnaryOperator function)
        {
            requireNonNull(function);
            return arg -> function.applyAsInt(this.applyAsInt(arg));
        }

        /**
         * Chains together this function with the specified consumer.
         * 
         * @param consumer
         *            the consumer.
         * @return a chainable consumer that applies this function to the argument, and passes the result to the
         *         specified consumer.
         */
        default ConsumerChain<T> and(IntConsumer consumer)
        {
            requireNonNull(consumer);
            return arg -> consumer.accept(applyAsInt(arg));
        }

        /**
         * Adds null tolerance to this function, returning the supplied default value if the application of the function
         * throws a {@link NullPointerException}, or returns a null result.
         * 
         * @param defaultIfNull
         *            the default to use.
         * @return the null-tolerant function.
         */
        default ToIntFunctionChain<T> withDefault(int defaultIfNull)
        {
            return arg -> getNullTolerant(() -> applyAsInt(arg)).orElse(defaultIfNull);
        }
    }

    /**
     * Creates a chain containing the specified function.
     * 
     * @param function
     *            the function.
     * @return the chain.
     */
    public static <T> ToIntFunctionChain<T> of(ToIntFunction<T> function)
    {
        requireNonNull(function);
        return function::applyAsInt;
    }
}