package uk.org.thehickses.chain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

public class ChainTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void testSingleConsumer()
    {
        Consumer<String> c = mock(Consumer.class);
        Chain.of(c).accept("Hello");
        verify(c).accept("Hello");
        verifyNoMoreInteractions(c);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSingleSupplier()
    {
        Supplier<String> s = mock(Supplier.class);
        when(s.get()).thenReturn("Hello");
        assertThat(Chain.of(s).get()).isEqualTo("Hello");
        verify(s).get();
        verifyNoMoreInteractions(s);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSingleFunction()
    {
        Function<String, String> f = mock(Function.class);
        when(f.apply("Hello")).thenReturn("Goodbye");
        assertThat(Chain.of(f).apply("Hello")).isEqualTo("Goodbye");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(f);
    }
    
    @Test
    public void testSingleRunnable()
    {
        Runnable r = mock(Runnable.class);
        Chain.of(r).run();
        verify(r).run();
        verifyNoMoreInteractions(r);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierAndFunction()
    {
        Supplier<String> s = mock(Supplier.class);
        Function<CharSequence, String> f = mock(Function.class);
        when(s.get()).thenReturn("Hello");
        when(f.apply("Hello")).thenReturn("Goodbye");
        assertThat(Chain.of(s).and(f).get()).isEqualTo("Goodbye");
        verify(s).get();
        verify(f).apply("Hello");
        verifyNoMoreInteractions(s, f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierAndConsumer()
    {
        Supplier<String> s = mock(Supplier.class);
        Consumer<CharSequence> c = mock(Consumer.class);
        when(s.get()).thenReturn("Hello");
        Chain.of(s).and(c).run();
        verify(s).get();
        verify(c).accept("Hello");
        verifyNoMoreInteractions(s, c);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionAndConsumer()
    {
        Function<Boolean, String> f = mock(Function.class);
        Consumer<CharSequence> c = mock(Consumer.class);
        when(f.apply(true)).thenReturn("Hello");
        Chain.of(f).and(c).accept(true);
        verify(f).apply(true);
        verify(c).accept("Hello");
        verifyNoMoreInteractions(f, c);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionAndFunction()
    {
        Function<Boolean, String> f1 = mock(Function.class);
        Function<CharSequence, Integer> f2 = mock(Function.class);
        when(f1.apply(true)).thenReturn("Hello");
        when(f2.apply("Hello")).thenReturn(444);
        assertThat(Chain.of(f1).and(f2).apply(true)).isEqualTo(444);
        verify(f1).apply(true);
        verify(f2).apply("Hello");
        verifyNoMoreInteractions(f1, f2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierGetWithDefaultNonNullReturned()
    {
        Supplier<String> s = mock(Supplier.class);
        when(s.get()).thenReturn("Hej");
        assertThat(Chain.of(s).withDefault("Hello").get()).isEqualTo("Hej");
        verify(s).get();
        verifyNoMoreInteractions(s);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierGetWithDefaultNullReturned()
    {
        Supplier<String> s = mock(Supplier.class);
        when(s.get()).thenThrow(NullPointerException.class);
        assertThat(Chain.of(s).withDefault("Hello").get()).isEqualTo("Hello");
        verify(s).get();
        verifyNoMoreInteractions(s);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierGetWithDefaultExceptionThrown()
    {
        Supplier<String> s = mock(Supplier.class);
        when(s.get()).thenThrow(NullPointerException.class);
        assertThat(Chain.of(s).withDefault("Hello").get()).isEqualTo("Hello");
        verify(s).get();
        verifyNoMoreInteractions(s);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierGetWithDefaultMultiStepsExceptionThrownPartWayThrough()
    {
        Supplier<String> s = mock(Supplier.class);
        Function<String, String> f1 = mock(Function.class);
        Function<String, String> f2 = mock(Function.class);
        when(s.get()).thenReturn(null);
        when(f1.apply(null)).thenThrow(NullPointerException.class);
        assertThat(Chain.of(s).and(f1).and(f2).withDefault("Goodbye").get()).isEqualTo("Goodbye");
        verify(s).get();
        verify(f1).apply(null);
        verifyNoMoreInteractions(s, f1, f2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionApplyWithDefaultNullReturned()
    {
        Function<String, String> f = mock(Function.class);
        when(f.apply("Hello")).thenThrow(NullPointerException.class);
        assertThat(Chain.of(f).withDefault("Goodbye").apply("Hello")).isEqualTo("Goodbye");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionApplyWithDefaultNonNullReturned()
    {
        Function<String, String> f = mock(Function.class);
        when(f.apply("Hello")).thenReturn("Hej");
        assertThat(Chain.of(f).withDefault("Goodbye").apply("Hello")).isEqualTo("Hej");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionApplyWithDefaultExceptionThrown()
    {
        Function<String, String> f = mock(Function.class);
        when(f.apply("Hello")).thenThrow(NullPointerException.class);
        assertThat(Chain.of(f).withDefault("Goodbye").apply("Hello")).isEqualTo("Goodbye");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionApplyWithDefaultMultiStepsExceptionThrownPartWayThrough()
    {
        Function<String, String> f1 = mock(Function.class);
        Function<String, String> f2 = mock(Function.class);
        Function<String, String> f3 = mock(Function.class);
        when(f1.apply(null)).thenReturn(null);
        when(f2.apply(null)).thenThrow(NullPointerException.class);
        assertThat(Chain.of(f1).and(f2).and(f3).withDefault("Goodbye").apply(null))
                .isEqualTo("Goodbye");
        verify(f1).apply(null);
        verify(f2).apply(null);
        verifyNoMoreInteractions(f1, f2, f3);
    }
}
