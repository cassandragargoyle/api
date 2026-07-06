/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

/**
 * The TypeScript analogue of the Java `LogSkipException` marker interface.
 * Marker interfaces do not exist at runtime in TS, so instead we tag the value
 * with a non-enumerable Symbol that the default filter recognizes.
 */
const LOG_SKIP = Symbol.for("cassandragargoyle.api.log.skip");

/**
 * Tag an error so the default filter drops its records before any transport,
 * mirroring `DefaultLoggerFilter` dropping `LogSkipException` throwables.
 *
 * @returns the same error, for convenient inline use (`throw markLogSkip(err)`)
 */
export function markLogSkip<E extends object>(error: E): E {
  Object.defineProperty(error, LOG_SKIP, {
    value: true,
    enumerable: false,
    configurable: true,
  });
  return error;
}

/** Report whether an error was tagged with {@link markLogSkip}. */
export function isLogSkip(error: unknown): boolean {
  return (
    typeof error === "object" &&
    error !== null &&
    (error as Record<symbol, unknown>)[LOG_SKIP] === true
  );
}
