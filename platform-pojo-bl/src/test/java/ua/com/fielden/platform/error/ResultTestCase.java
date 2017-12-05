package ua.com.fielden.platform.error;

import static org.junit.Assert.*;
import static ua.com.fielden.platform.error.Result.asRuntime;
import static ua.com.fielden.platform.error.Result.failure;
import static ua.com.fielden.platform.error.Result.successful;
import static ua.com.fielden.platform.error.Result.throwRuntime;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ResultTestCase {

    @Test
    public void asRuntime_does_not_wrap_exception_of_runtime_type() {
        final Result runtimeFailure = failure("exception");
        assertSame(runtimeFailure, asRuntime(runtimeFailure));
    }

    @Test
    public void asRuntime_wraps_non_runtime_exception_into_Result() {
        assertTrue(asRuntime(new Exception("exception")) instanceof RuntimeException);
        assertTrue(asRuntime(new Exception("exception")) instanceof Result);
    }

    @Test
    public void ifFailure_is_applicable_only_for_Results_that_represent_failures() {
        final AtomicInteger integer = new AtomicInteger(0);
        failure("exception").ifFailure(ex -> integer.incrementAndGet());
        successful("success").ifFailure(ex -> integer.incrementAndGet());
        failure("another exception").ifFailure(ex -> integer.incrementAndGet());
        assertEquals(2, integer.get());
    }

    @Test
    public void ifFailure_can_be_used_for_thorwing_runtime_exceptions() {
        try {
            failure(new Exception("exception")).ifFailure(ex -> {throw asRuntime(ex);});
            fail("exception was expected");
        } catch (final Exception ex) {
            assertTrue(ex instanceof Result);
            assertEquals("exception", ex.getMessage());
        }
    }

    @Test
    public void throwRuntime_can_be_conveniently_used_in_ifFailurefor_thorwing_runtime_exceptions() {
        try {
            failure(new Exception("exception")).ifFailure(ex -> throwRuntime(ex));
            fail("exception was expected");
        } catch (final Exception ex) {
            assertTrue(ex instanceof Result);
            assertEquals("exception", ex.getMessage());
        }
    }
}