/*
 * Copyright 2020, 2022 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.spy.DataGroupSpy;
import se.uu.ub.cora.metacreator.spy.MetadataCompleterSpy;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class MetadataGroupTextCompleterTest {

	private MetadataGroupTextCompleter extendedFunctionality;
	private MetadataCompleterSpy metadataCompleter;
	private String defaultImplementingTextType = "text";

	@BeforeMethod
	public void setUp() {
		metadataCompleter = new MetadataCompleterSpy();
		extendedFunctionality = MetadataGroupTextCompleter
				.withMetadataCompleterForTextLinkedRecordType(metadataCompleter,
						defaultImplementingTextType);
	}

	@Test
	public void textTextCompleterImplementsExtendedFunctionality() {
		assertTrue(extendedFunctionality instanceof ExtendedFunctionality);
	}

	@Test
	public void testTextCompleterCallsMetadataCompleter() {
		assertFalse(metadataCompleter.completeDataGroupWithLinkedTextsWasCalled);
		DataGroupSpy dataGroupSpy = new DataGroupSpy("someName");
		callExtendedFunctionalityWithGroup(dataGroupSpy);
		assertTrue(metadataCompleter.completeDataGroupWithLinkedTextsWasCalled);
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = "authToken";
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	@Test
	public void testTextCompleterPassesOnDataGroupAndImplementingTextTypeToMetadataCompleter() {
		DataGroupSpy someDataGroup = new DataGroupSpy("someName");
		callExtendedFunctionalityWithGroup(someDataGroup);

		assertSame(metadataCompleter.metaDataGroup, someDataGroup);
		assertEquals(extendedFunctionality.getImplementingTextType(), defaultImplementingTextType);
	}

	@Test
	public void testTextCompleterReturnsImplementingTextType() {
		assertEquals(extendedFunctionality.getImplementingTextType(), defaultImplementingTextType);
	}
}
