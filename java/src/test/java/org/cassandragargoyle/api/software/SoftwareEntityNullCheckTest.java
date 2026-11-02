/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.software;

import static org.cassandragargoyle.api.NullAssertions.assertRejectsNull;
import org.junit.jupiter.api.Test;

/**
 * Tests of the null checks in {@link SoftwareEntity}
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public class SoftwareEntityNullCheckTest
{
	@Test
	public void isInstalledByWhereRejectsNullName()
	{
		SoftwareEntity software = new SoftwareEntity(null);
		assertRejectsNull("name", () -> software.isInstalledByWhere(null));
		assertRejectsNull("name", () -> software.isInstalled(null));
	}
}
