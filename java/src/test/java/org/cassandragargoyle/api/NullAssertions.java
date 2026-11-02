/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.function.Executable;

/**
 * Assertions for the null checks written with {@code Objects.requireNonNull(value, "parameterName")}
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public final class NullAssertions
{
	private NullAssertions()
	{
		throw new IllegalStateException("Utility class.");
	}

	/**
	 * Asserts that the call throws {@link NullPointerException} with the parameter name as the message
	 *
	 * @param parameterName name of the parameter that is {@code null} in the call
	 * @param call          call with a {@code null} argument
	 */
	public static void assertRejectsNull(String parameterName, Executable call)
	{
		NullPointerException e = assertThrows(NullPointerException.class, call);
		assertEquals(parameterName, e.getMessage());
	}
}
