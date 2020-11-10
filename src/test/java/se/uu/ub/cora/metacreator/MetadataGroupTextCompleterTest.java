/*
 * Copyright 2020 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.metacreator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;

public class MetadataGroupTextCompleterTest {

	private MetadataGroupTextCompleter textCompleter;
	private MetadataCompleterSpy metadataCompleter;
	private String defaultImplementingTextType = "text";

	@BeforeMethod
	public void setUp() {
		metadataCompleter = new MetadataCompleterSpy();
		textCompleter = MetadataGroupTextCompleter.withMetadataCompleterForTextLinkedRecordType(
				metadataCompleter, defaultImplementingTextType);
	}

	@Test
	public void textTextCompleterImplementsExtendedFunctionality() {
		assertTrue(textCompleter instanceof ExtendedFunctionality);
	}

	@Test
	public void testTextCompleterCallsMetadataCompleter() {
		assertFalse(metadataCompleter.completeDataGroupWithLinkedTextsWasCalled);
		textCompleter.useExtendedFunctionality("authToken", new DataGroupSpy("someName"));
		assertTrue(metadataCompleter.completeDataGroupWithLinkedTextsWasCalled);
	}

	@Test
	public void testTextCompleterPassesOnDataGroupAndImplementingTextTypeToMetadataCompleter() {
		DataGroupSpy someDataGroup = new DataGroupSpy("someName");
		textCompleter.useExtendedFunctionality("authToken", someDataGroup);

		assertSame(metadataCompleter.metaDataGroup, someDataGroup);
		assertEquals(textCompleter.getImplementingTextType(), defaultImplementingTextType);
	}

	@Test
	public void testTextCompleterReturnsImplementingTextType() {
		assertEquals(textCompleter.getImplementingTextType(), defaultImplementingTextType);
	}
}
