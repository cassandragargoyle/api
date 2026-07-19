/**
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.telemetry;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TelemetryProvider
 *
 * @author Zdenek
 * @since 2026-03-18
 */
public class TelemetryProviderTest
{
	@Test
	public void testNoOpReturnsValidProvider()
	{
		TelemetryProvider provider = TelemetryProvider.noOp();

		assertNotNull(provider);
		assertTrue(provider.isNoOp());
	}

	@Test
	public void testNoOpReturnsSingleton()
	{
		TelemetryProvider first = TelemetryProvider.noOp();
		TelemetryProvider second = TelemetryProvider.noOp();

		assertSame(first, second);
	}

	@Test
	public void testNoOpGetTracerReturnsNoOpTracer()
	{
		TelemetryProvider provider = TelemetryProvider.noOp();
		Tracer tracer = provider.getTracer("test-instrumentation");

		assertNotNull(tracer);

		// NoOp tracer should create non-recording spans
		Span span = tracer.spanBuilder("test-span").startSpan();
		assertNotNull(span);
		assertFalse(span.getSpanContext().isValid());
		span.end();
	}

	@Test
	public void testNoOpShutdownDoesNotThrow()
	{
		TelemetryProvider provider = TelemetryProvider.noOp();

		assertDoesNotThrow(provider::shutdown);
	}

	@Test
	public void testBuilderWithNullServiceNameThrows()
	{
		assertThrows(IllegalArgumentException.class, () -> TelemetryProvider.builder(null));
	}

	@Test
	public void testBuilderWithBlankServiceNameThrows()
	{
		assertThrows(IllegalArgumentException.class, () -> TelemetryProvider.builder(""));
		assertThrows(IllegalArgumentException.class, () -> TelemetryProvider.builder("   "));
	}

	@Test
	public void testBuilderDisabledReturnsNoOp()
	{
		TelemetryProvider provider = TelemetryProvider.builder("test-service")
			.enabled(false)
			.build();

		assertTrue(provider.isNoOp());
	}

	@Test
	public void testBuilderNoneExporterReturnsNoOp()
	{
		TelemetryProvider provider = TelemetryProvider.builder("test-service")
			.exporter(ExporterType.NONE)
			.build();

		assertTrue(provider.isNoOp());
	}

	@Test
	public void testBuilderConsoleExporter()
	{
		TelemetryProvider provider = TelemetryProvider.builder("test-service")
			.exporter(ExporterType.CONSOLE)
			.build();

		try
		{
			assertFalse(provider.isNoOp());
			assertNotNull(provider.getTracer("test"));

			// Verify tracer produces valid spans
			Tracer tracer = provider.getTracer("test-instrumentation");
			Span span = tracer.spanBuilder("test-span").startSpan();
			assertNotNull(span);
			assertTrue(span.getSpanContext().isValid());
			span.end();
		}
		finally
		{
			provider.shutdown();
		}
	}

	@Test
	public void testBuilderInvalidSampleRateThrows()
	{
		assertThrows(IllegalArgumentException.class, () ->
			TelemetryProvider.builder("test-service").sampleRate(-0.1)
		);
		assertThrows(IllegalArgumentException.class, () ->
			TelemetryProvider.builder("test-service").sampleRate(1.1)
		);
	}

	@Test
	public void testBuilderValidSampleRate()
	{
		TelemetryProvider provider = TelemetryProvider.builder("test-service")
			.exporter(ExporterType.CONSOLE)
			.sampleRate(0.5)
			.build();

		try
		{
			assertFalse(provider.isNoOp());
		}
		finally
		{
			provider.shutdown();
		}
	}

	@Test
	public void testShutdownFlushesSpans()
	{
		TelemetryProvider provider = TelemetryProvider.builder("test-service")
			.exporter(ExporterType.CONSOLE)
			.build();

		Tracer tracer = provider.getTracer("test");
		Span span = tracer.spanBuilder("test-span").startSpan();
		span.end();

		// Shutdown should not throw
		assertDoesNotThrow(provider::shutdown);
	}

	@Test
	public void testBuilderFluentApi()
	{
		// Verify builder returns itself for chaining
		TelemetryProvider.Builder builder = TelemetryProvider.builder("test-service");
		assertSame(builder, builder.serviceName("updated"));
		assertSame(builder, builder.enabled(true));
		assertSame(builder, builder.exporter(ExporterType.NONE));
		assertSame(builder, builder.endpoint("http://localhost:4317"));
		assertSame(builder, builder.protocol("grpc"));
		assertSame(builder, builder.sampleRate(0.5));
	}
}
