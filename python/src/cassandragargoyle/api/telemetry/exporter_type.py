# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

from enum import Enum


class ExporterType(Enum):
    """Supported OpenTelemetry exporter types."""

    NONE = "none"
    """No-op exporter, zero overhead"""

    CONSOLE = "console"
    """Console/logging exporter for development"""

    OTLP = "otlp"
    """OTLP exporter for production use"""
