# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

import logging
import os
import unittest
from unittest.mock import patch

from cassandragargoyle.api.telemetry import (
    ExporterType,
    TelemetryProvider,
)


class TestTelemetryProviderNoOp(unittest.TestCase):
    """Tests for NoOp TelemetryProvider."""

    def test_no_op_returns_valid_provider(self):
        provider = TelemetryProvider.no_op()

        self.assertIsNotNone(provider)
        self.assertTrue(provider.is_no_op)

    def test_no_op_returns_singleton(self):
        first = TelemetryProvider.no_op()
        second = TelemetryProvider.no_op()

        self.assertIs(first, second)

    def test_no_op_get_tracer_returns_noop_tracer(self):
        provider = TelemetryProvider.no_op()
        tracer = provider.get_tracer("test-instrumentation")

        self.assertIsNotNone(tracer)

        # NoOp tracer should create non-recording spans
        span = tracer.start_span("test-span")
        self.assertIsNotNone(span)
        self.assertFalse(span.get_span_context().is_valid)
        span.end()

    def test_no_op_shutdown_does_not_throw(self):
        provider = TelemetryProvider.no_op()
        provider.shutdown()


class TestTelemetryProviderBuilder(unittest.TestCase):
    """Tests for TelemetryProvider builder."""

    def test_builder_with_none_service_name_throws(self):
        with self.assertRaises(ValueError):
            TelemetryProvider.builder(None)

    def test_builder_with_blank_service_name_throws(self):
        with self.assertRaises(ValueError):
            TelemetryProvider.builder("")
        with self.assertRaises(ValueError):
            TelemetryProvider.builder("   ")

    def test_builder_disabled_returns_no_op(self):
        provider = (
            TelemetryProvider.builder("test-service").enabled(False).build()
        )

        self.assertTrue(provider.is_no_op)

    def test_builder_none_exporter_returns_no_op(self):
        provider = (
            TelemetryProvider.builder("test-service")
            .exporter(ExporterType.NONE)
            .build()
        )

        self.assertTrue(provider.is_no_op)

    def test_builder_console_exporter(self):
        provider = (
            TelemetryProvider.builder("test-service")
            .exporter(ExporterType.CONSOLE)
            .build()
        )

        try:
            self.assertFalse(provider.is_no_op)
            self.assertIsNotNone(provider.get_tracer("test"))

            # Verify tracer produces valid spans
            tracer = provider.get_tracer("test-instrumentation")
            span = tracer.start_span("test-span")
            self.assertIsNotNone(span)
            self.assertTrue(span.get_span_context().is_valid)
            span.end()
        finally:
            provider.shutdown()

    def test_builder_invalid_sample_rate_throws(self):
        with self.assertRaises(ValueError):
            TelemetryProvider.builder("test-service").sample_rate(-0.1)
        with self.assertRaises(ValueError):
            TelemetryProvider.builder("test-service").sample_rate(1.1)

    def test_builder_valid_sample_rate(self):
        provider = (
            TelemetryProvider.builder("test-service")
            .exporter(ExporterType.CONSOLE)
            .sample_rate(0.5)
            .build()
        )

        try:
            self.assertFalse(provider.is_no_op)
        finally:
            provider.shutdown()

    def test_shutdown_flushes_spans(self):
        provider = (
            TelemetryProvider.builder("test-service")
            .exporter(ExporterType.CONSOLE)
            .build()
        )

        tracer = provider.get_tracer("test")
        span = tracer.start_span("test-span")
        span.end()

        # Shutdown should not throw
        provider.shutdown()

    def test_builder_fluent_api(self):
        builder = TelemetryProvider.builder("test-service")
        self.assertIs(builder, builder.service_name("updated"))
        self.assertIs(builder, builder.enabled(True))
        self.assertIs(builder, builder.exporter(ExporterType.NONE))
        self.assertIs(builder, builder.endpoint("http://localhost:4317"))
        self.assertIs(builder, builder.protocol("grpc"))
        self.assertIs(builder, builder.sample_rate(0.5))


class TestTelemetryProviderEnvOverrides(unittest.TestCase):
    """Tests for environment variable overrides."""

    def test_env_exporter_override_console(self):
        with patch.dict(os.environ, {"OTEL_TRACES_EXPORTER": "console"}):
            provider = (
                TelemetryProvider.builder("test-service")
                .exporter(ExporterType.NONE)
                .build()
            )
            try:
                self.assertFalse(provider.is_no_op)
            finally:
                provider.shutdown()

    def test_env_exporter_override_none(self):
        with patch.dict(os.environ, {"OTEL_TRACES_EXPORTER": "none"}):
            provider = (
                TelemetryProvider.builder("test-service")
                .exporter(ExporterType.CONSOLE)
                .build()
            )
            self.assertTrue(provider.is_no_op)

    def test_env_service_name_override(self):
        with patch.dict(os.environ, {"OTEL_SERVICE_NAME": "overridden"}):
            builder = TelemetryProvider.builder("original")
            builder.exporter(ExporterType.CONSOLE)
            builder._apply_environment_overrides()
            self.assertEqual(builder._service_name, "overridden")

    def test_env_sampler_always_off(self):
        with patch.dict(os.environ, {"OTEL_TRACES_SAMPLER": "always_off"}):
            builder = TelemetryProvider.builder("test-service")
            builder.exporter(ExporterType.CONSOLE)
            builder._apply_environment_overrides()
            sampler = builder._create_sampler()
            # always_off sampler should be returned
            self.assertIsNotNone(sampler)


class TestEnableTraceLogging(unittest.TestCase):
    """Tests for enable_trace_logging."""

    def test_enable_trace_logging_sets_level(self):
        TelemetryProvider.enable_trace_logging()

        otel_logger = logging.getLogger("opentelemetry")
        self.assertEqual(otel_logger.level, logging.INFO)


if __name__ == "__main__":
    unittest.main()
