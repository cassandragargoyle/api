# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

from __future__ import annotations

import logging
import os

from opentelemetry import trace
from opentelemetry.sdk.resources import Resource
from opentelemetry.sdk.trace import TracerProvider as SdkTracerProvider
from opentelemetry.sdk.trace.export import (
    BatchSpanProcessor,
    ConsoleSpanExporter,
    SimpleSpanProcessor,
    SpanExporter,
)
from opentelemetry.sdk.trace.sampling import (
    ALWAYS_OFF,
    ALWAYS_ON,
    TraceIdRatioBased,
)
from opentelemetry.trace import Tracer

from .exporter_type import ExporterType

_LOG = logging.getLogger(__name__)

_ENV_OTEL_SERVICE_NAME = "OTEL_SERVICE_NAME"
_ENV_OTEL_TRACES_EXPORTER = "OTEL_TRACES_EXPORTER"
_ENV_OTEL_EXPORTER_OTLP_ENDPOINT = "OTEL_EXPORTER_OTLP_ENDPOINT"
_ENV_OTEL_EXPORTER_OTLP_PROTOCOL = "OTEL_EXPORTER_OTLP_PROTOCOL"
_ENV_OTEL_TRACES_SAMPLER = "OTEL_TRACES_SAMPLER"
_ENV_OTEL_TRACES_SAMPLER_ARG = "OTEL_TRACES_SAMPLER_ARG"

_DEFAULT_OTLP_ENDPOINT = "http://localhost:4317"
_PROTOCOL_HTTP = "http/protobuf"
_DEFAULT_SAMPLE_RATE = 1.0


class TelemetryProvider:
    """Centralized OpenTelemetry TracerProvider for Python Portunix components.

    Supports three modes: NoOp (default, zero overhead), Console (development),
    and OTLP (production).

    Standard OTel environment variables override programmatic configuration:
    OTEL_EXPORTER_OTLP_ENDPOINT, OTEL_SERVICE_NAME,
    OTEL_TRACES_SAMPLER, OTEL_TRACES_EXPORTER.
    """

    _noop_instance: TelemetryProvider | None = None

    def __init__(
        self,
        tracer_provider: trace.TracerProvider,
        sdk_tracer_provider: SdkTracerProvider | None,
        no_op: bool,
    ) -> None:
        self._tracer_provider = tracer_provider
        self._sdk_tracer_provider = sdk_tracer_provider
        self._no_op = no_op

    @classmethod
    def no_op(cls) -> TelemetryProvider:
        """Return a NoOp provider singleton (zero overhead)."""
        if cls._noop_instance is None:
            cls._noop_instance = TelemetryProvider(
                trace.NoOpTracerProvider(), None, True
            )
        return cls._noop_instance

    @classmethod
    def builder(cls, service_name: str) -> TelemetryProviderBuilder:
        """Create a builder for configuring the provider.

        Args:
            service_name: the service name for resource identification

        Raises:
            ValueError: if service_name is None or blank
        """
        if not service_name or not service_name.strip():
            raise ValueError("Service name must not be None or blank")
        return TelemetryProviderBuilder(service_name)

    def get_tracer(self, instrumentation_name: str) -> Tracer:
        """Get a tracer for the given instrumentation scope."""
        return self._tracer_provider.get_tracer(instrumentation_name)

    def shutdown(self) -> None:
        """Shutdown and flush pending spans. No-op for disabled providers."""
        if not self._no_op and self._sdk_tracer_provider is not None:
            _LOG.debug("Shutting down TelemetryProvider, flushing pending spans")
            self._sdk_tracer_provider.force_flush(timeout_millis=5000)
            self._sdk_tracer_provider.shutdown()

    @staticmethod
    def enable_trace_logging() -> None:
        """Enable INFO-level logging for OpenTelemetry span output.

        Call when trace output should be visible in console/logs
        (e.g., when --trace CLI flag is active).
        Sets the 'opentelemetry' logger to INFO level.
        """
        logging.getLogger("opentelemetry").setLevel(logging.INFO)

    @property
    def is_no_op(self) -> bool:
        """Whether this provider is a no-op instance."""
        return self._no_op


class TelemetryProviderBuilder:
    """Builder for configuring a TelemetryProvider instance."""

    def __init__(self, service_name: str) -> None:
        self._service_name = service_name
        self._enabled = True
        self._exporter = ExporterType.NONE
        self._endpoint = _DEFAULT_OTLP_ENDPOINT
        self._protocol = "grpc"
        self._sample_rate = _DEFAULT_SAMPLE_RATE

    def service_name(self, name: str) -> TelemetryProviderBuilder:
        """Set the service name."""
        self._service_name = name
        return self

    def enabled(self, enabled: bool) -> TelemetryProviderBuilder:
        """Enable or disable telemetry. When disabled, returns a no-op provider."""
        self._enabled = enabled
        return self

    def exporter(self, exporter_type: ExporterType) -> TelemetryProviderBuilder:
        """Set the exporter type."""
        self._exporter = exporter_type
        return self

    def endpoint(self, endpoint: str) -> TelemetryProviderBuilder:
        """Set the OTLP endpoint URL."""
        self._endpoint = endpoint
        return self

    def protocol(self, protocol: str) -> TelemetryProviderBuilder:
        """Set the OTLP protocol (grpc or http/protobuf)."""
        self._protocol = protocol
        return self

    def sample_rate(self, rate: float) -> TelemetryProviderBuilder:
        """Set the sampling rate.

        Args:
            rate: sampling rate between 0.0 and 1.0

        Raises:
            ValueError: if rate is outside valid range
        """
        if rate < 0.0 or rate > 1.0:
            raise ValueError(
                f"Sample rate must be between 0.0 and 1.0, got: {rate}"
            )
        self._sample_rate = rate
        return self

    def build(self) -> TelemetryProvider:
        """Build the TelemetryProvider based on current configuration.

        Environment variables override programmatic settings.
        """
        if not self._enabled:
            return TelemetryProvider.no_op()

        self._apply_environment_overrides()

        if self._exporter == ExporterType.NONE:
            return TelemetryProvider.no_op()

        resource = Resource.create({"service.name": self._service_name})

        sampler = self._create_sampler()
        span_exporter = self._create_span_exporter()
        span_processor = self._create_span_processor(span_exporter)

        tracer_provider = SdkTracerProvider(
            resource=resource,
            sampler=sampler,
        )
        tracer_provider.add_span_processor(span_processor)

        _LOG.info(
            "TelemetryProvider initialized: exporter=%s, endpoint=%s, sampleRate=%s",
            self._exporter,
            self._endpoint,
            self._sample_rate,
        )

        return TelemetryProvider(tracer_provider, tracer_provider, False)

    def _apply_environment_overrides(self) -> None:
        env_service_name = os.environ.get(_ENV_OTEL_SERVICE_NAME)
        if env_service_name and env_service_name.strip():
            self._service_name = env_service_name

        env_exporter = os.environ.get(_ENV_OTEL_TRACES_EXPORTER)
        if env_exporter and env_exporter.strip():
            mapping = {
                "otlp": ExporterType.OTLP,
                "logging": ExporterType.CONSOLE,
                "console": ExporterType.CONSOLE,
                "none": ExporterType.NONE,
            }
            mapped = mapping.get(env_exporter.lower())
            if mapped is not None:
                self._exporter = mapped
            else:
                _LOG.warning(
                    "Unknown OTEL_TRACES_EXPORTER value: %s, using configured default",
                    env_exporter,
                )

        env_endpoint = os.environ.get(_ENV_OTEL_EXPORTER_OTLP_ENDPOINT)
        if env_endpoint and env_endpoint.strip():
            self._endpoint = env_endpoint

        env_protocol = os.environ.get(_ENV_OTEL_EXPORTER_OTLP_PROTOCOL)
        if env_protocol and env_protocol.strip():
            self._protocol = env_protocol

        env_sampler_arg = os.environ.get(_ENV_OTEL_TRACES_SAMPLER_ARG)
        if env_sampler_arg and env_sampler_arg.strip():
            try:
                self._sample_rate = float(env_sampler_arg)
            except ValueError:
                _LOG.warning(
                    "Invalid OTEL_TRACES_SAMPLER_ARG value: %s", env_sampler_arg
                )

    def _create_sampler(self):
        env_sampler = os.environ.get(_ENV_OTEL_TRACES_SAMPLER)
        if env_sampler is not None:
            sampler_lower = env_sampler.lower()
            if sampler_lower == "always_on":
                return ALWAYS_ON
            elif sampler_lower == "always_off":
                return ALWAYS_OFF
            elif sampler_lower == "traceidratio":
                return TraceIdRatioBased(self._sample_rate)
            else:
                _LOG.warning(
                    "Unknown OTEL_TRACES_SAMPLER value: %s, using ratio-based",
                    env_sampler,
                )

        if self._sample_rate >= 1.0:
            return ALWAYS_ON
        if self._sample_rate <= 0.0:
            return ALWAYS_OFF
        return TraceIdRatioBased(self._sample_rate)

    def _create_span_exporter(self) -> SpanExporter:
        if self._exporter == ExporterType.CONSOLE:
            return ConsoleSpanExporter()
        elif self._exporter == ExporterType.OTLP:
            if self._protocol == _PROTOCOL_HTTP:
                from opentelemetry.exporter.otlp.proto.http.trace_exporter import (
                    OTLPSpanExporter as OTLPHttpSpanExporter,
                )

                return OTLPHttpSpanExporter(endpoint=self._endpoint)
            else:
                from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import (
                    OTLPSpanExporter as OTLPGrpcSpanExporter,
                )

                return OTLPGrpcSpanExporter(endpoint=self._endpoint)
        else:
            raise RuntimeError(
                f"Cannot create exporter for type: {self._exporter}"
            )

    def _create_span_processor(self, span_exporter: SpanExporter):
        if self._exporter == ExporterType.CONSOLE:
            return SimpleSpanProcessor(span_exporter)
        return BatchSpanProcessor(span_exporter)
