/**
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.telemetry;

/**
 * Supported OpenTelemetry exporter types
 *
 * @author Zdenek
 * @since 2026-03-18
 */
public enum ExporterType
{
	/** No-op exporter, zero overhead */
	NONE,

	/** Console/logging exporter for development */
	CONSOLE,

	/** OTLP exporter for production use */
	OTLP
}
