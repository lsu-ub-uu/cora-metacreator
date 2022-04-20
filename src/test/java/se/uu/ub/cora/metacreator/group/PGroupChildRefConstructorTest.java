/*
 * Copyright 2018 Uppsala University Library
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
package se.uu.ub.cora.metacreator.group;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.PresentationChildReference;
import se.uu.ub.cora.metacreator.RecordIdentifier;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;

public class PGroupChildRefConstructorTest {

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
	}

	@Test
	public void testConstructorForInput() {
		DataGroup metadataChildRef = DataCreatorForPresentationsConstructor
				.createMetadataChildRefWithIdAndRepeatId("identifierChildGroup", "0");
		PChildRefConstructor constructor = PGroupChildRefConstructor
				.usingMetadataChildReferenceAndMode(metadataChildRef, "input");

		PresentationChildReference childRef = constructor.getChildRef();
		assertCorrectRecordIdentifier(childRef, "identifierChildPGroup");

		assertCorrectRef(childRef, "identifierChildPGroup");

	}

	private void assertCorrectRef(PresentationChildReference childRef, String linkedRecordId) {
		DataRecordLink ref = childRef.ref;
		assertEquals(ref.getLinkedRecordType(), "presentationGroup");
		assertEquals(ref.getLinkedRecordId(), linkedRecordId);
		assertEquals(ref.getAttribute("type").getValue(), "presentation");
	}

	private void assertCorrectRecordIdentifier(PresentationChildReference childRef,
			String identifierId) {
		RecordIdentifier recordIdentifier = childRef.recordIdentifier;
		assertEquals(recordIdentifier.type, "presentationGroup");
		assertEquals(recordIdentifier.id, identifierId);
	}

	@Test
	public void testConstructorForOutput() {
		DataGroup metadataChildRef = DataCreatorForPresentationsConstructor
				.createMetadataChildRefWithIdAndRepeatId("identifierChildGroup", "0");
		PChildRefConstructor constructor = PGroupChildRefConstructor
				.usingMetadataChildReferenceAndMode(metadataChildRef, "output");

		PresentationChildReference childRef = constructor.getChildRef();
		assertCorrectRecordIdentifier(childRef, "identifierChildOutputPGroup");

		assertCorrectRef(childRef, "identifierChildOutputPGroup");

	}
}
