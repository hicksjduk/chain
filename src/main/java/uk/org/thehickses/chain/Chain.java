package uk.org.thehickses.chain;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

public class Chain
{
    public static <T> SupplierChain<T> of(Supplier<T> func)
    {
        return func::get;
    }

    public static IntSupplierChain of(IntSupplier func)
    {
        return func::getAsInt;
    }

    public static <T, R> FunctionChain<T, R> of(Function<T, R> func)
    {
        return func::apply;
    }

    public static <T> IntFunctionChain<T> of(IntFunction<T> func)
    {
        return func::apply;
    }

    public static <T> ToIntFunctionChain<T> of(ToIntFunction<T> func)
    {
        return func::applyAsInt;
    }

    public static <T> UnaryOperatorChain<T> of(UnaryOperator<T> func)
    {
        return func::apply;
    }

    public static IntUnaryOperatorChain of(IntUnaryOperator func)
    {
        return func::applyAsInt;
    }

    private static <T> ConsumerChain<T> of(Consumer<T> func)
    {
        return func::accept;
    }

    private static IntConsumerChain of(IntConsumer func)
    {
        return func::accept;
    }

    private static RunnableChain of(Runnable func)
    {
        return func::run;
    }

    public static <T> Consumer<T> nullTolerant(Consumer<T> func)
    {
        return of(func).nullTolerant();
    }

    public static IntConsumer nullTolerant(IntConsumer func)
    {
        return of(func).nullTolerant();
    }

    public static Runnable nullTolerant(Runnable func)
    {
        return of(func).nullTolerant();
    }

    static <T> Supplier<T> nullTolerant(Supplier<T> func, T defaultIfNull)
    {
        return () ->
            {
                try
                {
                    return func.get();
                }
                catch (NullPointerException ex)
                {
                    return defaultIfNull;
                }
            };
    }

    static <T> IntSupplier nullTolerant(IntSupplier func, int defaultIfNull)
    {
        return () ->
            {
                try
                {
                    return func.getAsInt();
                }
                catch (NullPointerException ex)
                {
                    return defaultIfNull;
                }
            };
    }

    public static interface SupplierChain<T> extends Supplier<T>
    {
        default RunnableChain and(Consumer<? super T> func)
        {
            return () -> func.accept(get());
        }

        default <R> SupplierChain<R> and(Function<? super T, R> func)
        {
            return () -> func.apply(get());
        }

        default SupplierChain<T> and(UnaryOperator<T> func)
        {
            return () -> func.apply(get());
        }

        default IntSupplierChain and(ToIntFunction<? super T> func)
        {
            return () -> func.applyAsInt(get());
        }

        default Supplier<T> withDefault(T defaultIfNull)
        {
            return nullTolerant(this, defaultIfNull);
        }
    }

    public static interface IntSupplierChain extends IntSupplier
    {
        public default RunnableChain and(IntConsumer func)
        {
            return () -> func.accept(getAsInt());
        }

        public default <T> SupplierChain<T> and(IntFunction<T> func)
        {
            return () -> func.apply(getAsInt());
        }

        public default IntSupplierChain and(IntUnaryOperator func)
        {
            return () -> func.applyAsInt(getAsInt());
        }

        public default IntSupplier withDefault(int defaultIfNull)
        {
            return nullTolerant(this, defaultIfNull);
        }
    }

    public static interface FunctionChain<T, R> extends Function<T, R>
    {
        public default ConsumerChain<T> and(Consumer<? super R> func)
        {
            return arg -> func.accept(apply(arg));
        }

        public default <S> FunctionChain<T, S> and(Function<? super R, S> func)
        {
            return arg -> func.apply(apply(arg));
        }

        public default FunctionChain<T, R> and(UnaryOperator<R> func)
        {
            return arg -> func.apply(apply(arg));
        }

        public default ToIntFunctionChain<T> and(ToIntFunction<? super R> func)
        {
            return arg -> func.applyAsInt(apply(arg));
        }

        public default Function<T, R> withDefault(R defaultIfNull)
        {
            return arg -> nullTolerant(() -> apply(arg), defaultIfNull).get();
        }
    }

    public static interface IntFunctionChain<T> extends IntFunction<T>
    {
        public default IntConsumerChain and(Consumer<T> func)
        {
            return arg -> func.accept(apply(arg));
        }

        public default <R> IntFunctionChain<R> and(Function<? super T, R> func)
        {
            return arg -> func.apply(apply(arg));
        }

        public default IntFunctionChain<T> and(UnaryOperator<T> func)
        {
            return arg -> func.apply(apply(arg));
        }

        public default IntUnaryOperatorChain and(ToIntFunction<? super T> func)
        {
            return arg -> func.applyAsInt(apply(arg));
        }

        public default IntFunction<T> withDefault(T defaultIfNull)
        {
            return arg -> nullTolerant(() -> apply(arg), defaultIfNull).get();
        }
    }

    public static interface UnaryOperatorChain<T> extends UnaryOperator<T>
    {
        public default ConsumerChain<T> and(Consumer<? super T> func)
        {
            return arg -> func.accept(apply(arg));
        }

        public default <R> FunctionChain<T, R> and(Function<? super T, R> func)
        {
            return arg -> func.apply(apply(arg));
        }

        public default UnaryOperatorChain<T> and(UnaryOperator<T> func)
        {
            return arg -> func.apply(apply(arg));
        }

        public default ToIntFunctionChain<T> and(ToIntFunction<T> func)
        {
            return arg -> func.applyAsInt(apply(arg));
        }

        public default UnaryOperator<T> withDefault(T defaultIfNull)
        {
            return arg -> nullTolerant(() -> apply(arg), defaultIfNull).get();
        }
    }

    public static interface IntUnaryOperatorChain extends IntUnaryOperator
    {
        public default IntConsumerChain and(IntConsumer func)
        {
            return arg -> func.accept(applyAsInt(arg));
        }

        public default <T> IntFunctionChain<T> and(IntFunction<T> func)
        {
            return arg -> func.apply(applyAsInt(arg));
        }

        public default IntUnaryOperatorChain and(IntUnaryOperator func)
        {
            return arg -> func.applyAsInt(applyAsInt(arg));
        }

        public default IntUnaryOperator withDefault(int defaultIfNull)
        {
            return arg -> nullTolerant(() -> applyAsInt(arg), defaultIfNull).getAsInt();
        }
    }

    public static interface ToIntFunctionChain<T> extends ToIntFunction<T>
    {
        public default ConsumerChain<T> and(IntConsumer func)
        {
            return arg -> func.accept(applyAsInt(arg));
        }

        public default <R> FunctionChain<T, R> and(IntFunction<R> func)
        {
            return arg -> func.apply(applyAsInt(arg));
        }

        public default ToIntFunctionChain<T> and(IntUnaryOperator func)
        {
            return arg -> func.applyAsInt(applyAsInt(arg));
        }

        public default ToIntFunction<T> withDefault(int defaultIfNull)
        {
            return arg -> nullTolerant(() -> applyAsInt(arg), defaultIfNull).getAsInt();
        }
    }

    public static interface ConsumerChain<T> extends Consumer<T>
    {
        public default Consumer<T> nullTolerant()
        {
            return arg ->
                {
                    try
                    {
                        accept(arg);
                    }
                    catch (NullPointerException ex)
                    {}
                };
        }
    }

    public static interface IntConsumerChain extends IntConsumer
    {
        public default IntConsumer nullTolerant()
        {
            return arg ->
                {
                    try
                    {
                        accept(arg);
                    }
                    catch (NullPointerException ex)
                    {}
                };
        }
    }

    public static interface RunnableChain extends Runnable
    {
        public default Runnable nullTolerant()
        {
            return () ->
                {
                    try
                    {
                        run();
                    }
                    catch (NullPointerException ex)
                    {}
                };
        }
    }
}
