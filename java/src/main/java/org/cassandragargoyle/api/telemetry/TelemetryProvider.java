/**
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized OpenTelemetry TracerProvider initialization for CassandraGargoyle plugins.
 * Supports three modes: NoOp (default, zero overhead), Console (development),
 * and OTLP (production).
 *
 * <p>Standard OTel environment variables override programmatic configuration:
 * {@code OTEL_EXPORTER_OTLP_ENDPOINT}, {@code OTEL_SERVICE_NAME},
 * {@code OTEL_TRACES_SAMPLER}, {@code OTEL_TRACES_EXPORTER}.</p>
 *
 * @author Zdenek
 * @since 2026-03-18
 */
public class TelemetryProvider
{
	private static final Logger LOG = Logger.getLogger(TelemetryProvider.class.getName());

	private static final String ENV_OTEL_SERVICE_NAME = "OTEL_SERVICE_NAME";
	private static final String ENV_OTEL_TRACES_EXPORTER = "OTEL_TRACES_EXPORTER";
	private static final String ENV_OTEL_EXPORTER_OTLP_ENDPOINT = "OTEL_EXPORTER_OTLP_ENDPOINT";
	private static final String ENV_OTEL_EXPORTER_OTLP_PROTOCOL = "OTEL_EXPORTER_OTLP_PROTOCOL";
	private static final String ENV_OTEL_TRACES_SAMPLER = "OTEL_TRACES_SAMPLER";
	private static final String ENV_OTEL_TRACES_SAMPLER_ARG = "OTEL_TRACES_SAMPLER_ARG";

	private static final String DEFAULT_OTLP_ENDPOINT = "http://localhost:4317";
	private static final String PROTOCOL_HTTP = "http/protobuf";
	private static final double DEFAULT_SAMPLE_RATE = 1.0;

	private static final TelemetryProvider NOOP_INSTANCE = new TelemetryProvider(
		OpenTelemetry.noop(), null, true
	);

	private final OpenTelemetry openTelemetry;
	private final SdkTracerProvider sdkTracerProvider;
	private final boolean noOp;

	private TelemetryProvider(OpenTelemetry openTelemetry, SdkTracerProvider sdkTracerProvider, boolean noOp)
	{
		this.openTelemetry = openTelemetry;
		this.sdkTracerProvider = sdkTracerProvider;
		this.noOp = noOp;
	}

	/**
	 * Returns a no-op TelemetryProvider singleton with zero overhead
	 *
	 * @return no-op provider instance
	 */
	public static TelemetryProvider noOp()
	{
		return NOOP_INSTANCE;
	}

	/**
	 * Creates a new builder for configuring the TelemetryProvider
	 *
	 * @param serviceName the service name for resource identification
	 * @return new Builder instance
	 * @throws IllegalArgumentException if serviceName is null or blank
	 */
	public static Builder builder(String serviceName)
	{
		if (serviceName == null || serviceName.isBlank())
		{
			throw new IllegalArgumentException("Service name must not be null or blank");
		}
		return new Builder(serviceName);
	}

	/**
	 * Returns a tracer for the given instrumentation scope
	 *
	 * @param instrumentationName the name identifying the instrumentation scope
	 * @return a Tracer instance
	 */
	public Tracer getTracer(String instrumentationName)
	{
		return openTelemetry.getTracer(instrumentationName);
	}

	/**
	 * Shuts down the TracerProvider, flushing any pending spans.
	 * No-op for disabled providers.
	 */
	public void shutdown()
	{
		if (!noOp && sdkTracerProvider != null)
		{
			LOG.log(Level.FINE, "Shutting down TelemetryProvider, flushing pending spans");
			sdkTracerProvider.forceFlush().join(5, TimeUnit.SECONDS);
			sdkTracerProvider.shutdown().join(5, TimeUnit.SECONDS);
		}
	}

	/**
	 * Enables INFO-level logging for OpenTelemetry span output.
	 * Call this when trace output should be visible in console/logs
	 * (e.g., when --trace CLI flag is active).
	 * Works with SLF4J/Logback (via reflection) and java.util.logging.
	 */
	public static void enableTraceLogging()
	{
		try
		{
			// Try Logback (SLF4J) via reflection - avoids compile-time dependency
			Class<?> loggerFactoryClass = Class.forName("org.slf4j.LoggerFactory");
			Object factory = loggerFactoryClass.getMethod("getILoggerFactory").invoke(null);

			Class<?> logbackContextClass = Class.forName("ch.qos.logback.classic.LoggerContext");
			if (logbackContextClass.isInstance(factory))
			{
				Object logger = logbackContextClass.getMethod("getLogger", String.class)
					.invoke(factory, "io.opentelemetry");

				Class<?> levelClass = Class.forName("ch.qos.logback.classic.Level");
				Object infoLevel = levelClass.getField("INFO").get(null);

				logger.getClass().getMethod("setLevel", levelClass).invoke(logger, infoLevel);
				return;
			}
		}
		catch (Exception ignored)
		{
			// Logback/SLF4J not on classpath or reflection failed
		}

		// Fallback: java.util.logging
		java.util.logging.Logger.getLogger("io.opentelemetry")
			.setLevel(java.util.logging.Level.INFO);
	}

	/**
	 * Returns whether this provider is a no-op instance
	 *
	 * @return true if no-op
	 */
	public boolean isNoOp()
	{
		return noOp;
	}

	/**
	 * Builder for configuring a TelemetryProvider instance
	 */
	public static class Builder
	{
		private String serviceName;
		private boolean enabled = true;
		private ExporterType exporter = ExporterType.NONE;
		private String endpoint = DEFAULT_OTLP_ENDPOINT;
		private String protocol = "grpc";
		private double sampleRate = DEFAULT_SAMPLE_RATE;

		private Builder(String serviceName)
		{
			this.serviceName = serviceName;
		}

		/**
		 * Sets the service name
		 *
		 * @param name service name
		 * @return this builder
		 */
		public Builder serviceName(String name)
		{
			this.serviceName = name;
			return this;
		}

		/**
		 * Enables or disables telemetry. When disabled, returns a no-op provider.
		 *
		 * @param enabled true to enable
		 * @return this builder
		 */
		public Builder enabled(boolean enabled)
		{
			this.enabled = enabled;
			return this;
		}

		/**
		 * Sets the exporter type
		 *
		 * @param type exporter type (NONE, CONSOLE, OTLP)
		 * @return this builder
		 */
		public Builder exporter(ExporterType type)
		{
			this.exporter = type;
			return this;
		}

		/**
		 * Sets the OTLP endpoint URL
		 *
		 * @param endpoint OTLP endpoint
		 * @return this builder
		 */
		public Builder endpoint(String endpoint)
		{
			this.endpoint = endpoint;
			return this;
		}

		/**
		 * Sets the OTLP protocol (grpc or http/protobuf)
		 *
		 * @param protocol transport protocol
		 * @return this builder
		 */
		public Builder protocol(String protocol)
		{
			this.protocol = protocol;
			return this;
		}

		/**
		 * Sets the sampling rate
		 *
		 * @param rate sampling rate between 0.0 and 1.0
		 * @return this builder
		 * @throws IllegalArgumentException if rate is outside valid range
		 */
		public Builder sampleRate(double rate)
		{
			if (rate < 0.0 || rate > 1.0)
			{
				throw new IllegalArgumentException("Sample rate must be between 0.0 and 1.0, got: " + rate);
			}
			this.sampleRate = rate;
			return this;
		}

		/**
		 * Builds the TelemetryProvider based on current configuration.
		 * Environment variables override programmatic settings.
		 *
		 * @return configured TelemetryProvider instance
		 */
		public TelemetryProvider build()
		{
			if (!enabled)
			{
				return TelemetryProvider.noOp();
			}

			// Apply environment variable overrides
			applyEnvironmentOverrides();

			if (exporter == ExporterType.NONE)
			{
				return TelemetryProvider.noOp();
			}

			Resource resource = Resource.getDefault().merge(
				Resource.create(Attributes.of(
					AttributeKey.stringKey("service.name"), serviceName
				))
			);

			Sampler sampler = createSampler();
			SpanExporter spanExporter = createSpanExporter();

			SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
				.setResource(resource)
				.setSampler(sampler)
				.addSpanProcessor(createSpanProcessor(spanExporter))
				.build();

			OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
				.setTracerProvider(tracerProvider)
				.build();

			LOG.log(Level.CONFIG, "TelemetryProvider initialized: exporter={0}, endpoint={1}, sampleRate={2}",
				new Object[]{exporter, endpoint, sampleRate});

			return new TelemetryProvider(sdk, tracerProvider, false);
		}

		private void applyEnvironmentOverrides()
		{
			String envServiceName = System.getenv(ENV_OTEL_SERVICE_NAME);
			if (envServiceName != null && !envServiceName.isBlank())
			{
				serviceName = envServiceName;
			}

			String envExporter = System.getenv(ENV_OTEL_TRACES_EXPORTER);
			if (envExporter != null && !envExporter.isBlank())
			{
				switch (envExporter.toLowerCase())
				{
					case "otlp":
						exporter = ExporterType.OTLP;
						break;
					case "logging":
					case "console":
						exporter = ExporterType.CONSOLE;
						break;
					case "none":
						exporter = ExporterType.NONE;
						break;
					default:
						LOG.log(Level.WARNING,
							"Unknown OTEL_TRACES_EXPORTER value: {0}, using configured default",
							envExporter);
				}
			}

			String envEndpoint = System.getenv(ENV_OTEL_EXPORTER_OTLP_ENDPOINT);
			if (envEndpoint != null && !envEndpoint.isBlank())
			{
				endpoint = envEndpoint;
			}

			String envProtocol = System.getenv(ENV_OTEL_EXPORTER_OTLP_PROTOCOL);
			if (envProtocol != null && !envProtocol.isBlank())
			{
				protocol = envProtocol;
			}

			String envSamplerArg = System.getenv(ENV_OTEL_TRACES_SAMPLER_ARG);
			if (envSamplerArg != null && !envSamplerArg.isBlank())
			{
				try
				{
					sampleRate = Double.parseDouble(envSamplerArg);
				}
				catch (NumberFormatException e)
				{
					LOG.log(Level.WARNING, "Invalid OTEL_TRACES_SAMPLER_ARG value: {0}", envSamplerArg);
				}
			}
		}

		private Sampler createSampler()
		{
			String envSampler = System.getenv(ENV_OTEL_TRACES_SAMPLER);
			if (envSampler != null)
			{
				switch (envSampler.toLowerCase())
				{
					case "always_on":
						return Sampler.alwaysOn();
					case "always_off":
						return Sampler.alwaysOff();
					case "traceidratio":
						return Sampler.traceIdRatioBased(sampleRate);
					default:
						LOG.log(Level.WARNING, "Unknown OTEL_TRACES_SAMPLER value: {0}, using ratio-based", envSampler);
				}
			}

			if (sampleRate >= 1.0)
			{
				return Sampler.alwaysOn();
			}
			if (sampleRate <= 0.0)
			{
				return Sampler.alwaysOff();
			}
			return Sampler.traceIdRatioBased(sampleRate);
		}

		private SpanExporter createSpanExporter()
		{
			switch (exporter)
			{
				case CONSOLE:
					return LoggingSpanExporter.create();
				case OTLP:
					if (PROTOCOL_HTTP.equals(protocol))
					{
						return OtlpHttpSpanExporter.builder()
							.setEndpoint(endpoint)
							.build();
					}
					return OtlpGrpcSpanExporter.builder()
						.setEndpoint(endpoint)
						.build();
				default:
					throw new IllegalStateException("Cannot create exporter for type: " + exporter);
			}
		}

		private io.opentelemetry.sdk.trace.SpanProcessor createSpanProcessor(SpanExporter spanExporter)
		{
			if (exporter == ExporterType.CONSOLE)
			{
				return SimpleSpanProcessor.create(spanExporter);
			}
			return BatchSpanProcessor.builder(spanExporter).build();
		}
	}
}
