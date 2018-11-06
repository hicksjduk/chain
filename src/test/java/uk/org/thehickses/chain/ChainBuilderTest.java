package uk.org.thehickses.chain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

public class ChainBuilderTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void testSingleConsumer()
    {
        Consumer<String> c = mock(Consumer.class);
        ChainBuilder.chain(c).accept("Hello");
        verify(c).accept("Hello");
        verifyNoMoreInteractions(c);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSingleSupplier()
    {
        Supplier<String> s = mock(Supplier.class);
        when(s.get()).thenReturn("Hello");
        assertThat(ChainBuilder.chain(s).get()).isEqualTo("Hello");
        verify(s).get();
        verifyNoMoreInteractions(s);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSingleFunction()
    {
        Function<String, String> f = mock(Function.class);
        when(f.apply("Hello")).thenReturn("Goodbye");
        assertThat(ChainBuilder.chain(f).apply("Hello")).isEqualTo("Goodbye");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierAndFunction()
    {
        Supplier<String> s = mock(Supplier.class);
        Function<CharSequence, String> f = mock(Function.class);
        when(s.get()).thenReturn("Hello");
        when(f.apply("Hello")).thenReturn("Goodbye");
        assertThat(ChainBuilder.chain(s).and(f).get()).isEqualTo("Goodbye");
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
        ChainBuilder.chain(s).and(c).run();
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
        ChainBuilder.chain(f).and(c).accept(true);
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
        assertThat(ChainBuilder.chain(f1).and(f2).apply(true)).isEqualTo(444);
        verify(f1).apply(true);
        verify(f2).apply("Hello");
        verifyNoMoreInteractions(f1, f2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConsumerAndConsumer()
    {
        Consumer<String> c1 = mock(Consumer.class);
        Consumer<CharSequence> c2 = mock(Consumer.class);
        ChainBuilder.chain(c1).and(c2).accept("Hello");
        verify(c1).accept("Hello");
        verify(c2).accept("Hello");
        verifyNoMoreInteractions(c1, c2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConsumerAndFunction()
    {
        Consumer<String> c = mock(Consumer.class);
        Function<CharSequence, Boolean> f = mock(Function.class);
        when(f.apply("Hello")).thenReturn(false);
        assertThat(ChainBuilder.chain(c).and(f).apply("Hello")).isEqualTo(false);
        verify(c).accept("Hello");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(c, f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierGetWithDefaultNonNullReturned()
    {
        Supplier<String> s = mock(Supplier.class);
        when(s.get()).thenReturn("Hej");
        assertThat(ChainBuilder.chain(s).getWithDefault("Hello")).isEqualTo("Hej");
        verify(s).get();
        verifyNoMoreInteractions(s);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierGetWithDefaultNullReturned()
    {
        Supplier<String> s = mock(Supplier.class);
        when(s.get()).thenReturn(null);
        assertThat(ChainBuilder.chain(s).getWithDefault("Hello")).isEqualTo("Hello");
        verify(s).get();
        verifyNoMoreInteractions(s);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSupplierGetWithDefaultExceptionThrown()
    {
        Supplier<String> s = mock(Supplier.class);
        when(s.get()).thenThrow(NullPointerException.class);
        assertThat(ChainBuilder.chain(s).getWithDefault("Hello")).isEqualTo("Hello");
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
        assertThat(ChainBuilder.chain(s).and(f1).and(f2).getWithDefault("Goodbye"))
                .isEqualTo("Goodbye");
        verify(s).get();
        verify(f1).apply(null);
        verifyNoMoreInteractions(s, f1, f2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionApplyWithDefaultNullReturned()
    {
        Function<String, String> f = mock(Function.class);
        when(f.apply("Hello")).thenReturn(null);
        assertThat(ChainBuilder.chain(f).applyWithDefault("Hello", "Goodbye")).isEqualTo("Goodbye");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionApplyWithDefaultNonNullReturned()
    {
        Function<String, String> f = mock(Function.class);
        when(f.apply("Hello")).thenReturn("Hej");
        assertThat(ChainBuilder.chain(f).applyWithDefault("Hello", "Goodbye")).isEqualTo("Hej");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionApplyWithDefaultExceptionThrown()
    {
        Function<String, String> f = mock(Function.class);
        when(f.apply("Hello")).thenThrow(NullPointerException.class);
        assertThat(ChainBuilder.chain(f).applyWithDefault("Hello", "Goodbye")).isEqualTo("Goodbye");
        verify(f).apply("Hello");
        verifyNoMoreInteractions(f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunctionApplyWithDefaultMultiStepsExceptionThrownPartWayThrough()
    {
        Consumer<String> c = mock(Consumer.class);
        Function<String, String> f1 = mock(Function.class);
        Function<String, String> f2 = mock(Function.class);
        when(f1.apply(null)).thenThrow(NullPointerException.class);
        assertThat(ChainBuilder.chain(c).and(f1).and(f2).applyWithDefault(null, "Goodbye"))
                .isEqualTo("Goodbye");
        verify(c).accept(null);
        verify(f1).apply(null);
        verifyNoMoreInteractions(c, f1, f2);
    }
}
