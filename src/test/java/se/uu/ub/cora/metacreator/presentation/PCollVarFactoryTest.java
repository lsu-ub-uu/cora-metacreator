/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.presentation;

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.MetadataIdToPresentationId;
import se.uu.ub.cora.metacreator.spy.MetadataIdToPresentationIdSpy;

public class PCollVarFactoryTest {
	private DataFactorySpy dataFactory;
	private PVarFactory factory;
	private MetadataIdToPresentationIdSpy metadataIdToPresentationId;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);
		metadataIdToPresentationId = new MetadataIdToPresentationIdSpy();
		metadataIdToPresentationId.MRV.setDefaultReturnValuesSupplier(
				"createPresentationIdUsingMetadataIdAndMode", () -> "spyCreatedId");

		factory = PCollVarFactoryImp.usingMetadataIdToPresentationId(metadataIdToPresentationId);
	}

	@Test
	public void testOnlyForTestGetMetadataIdToPresentationId() throws Exception {
		MetadataIdToPresentationId metadataIdToPresentationId2 = ((PCollVarFactoryImp) factory)
				.onlyForTestGetMetadataIdToPresentationId();
		assertSame(metadataIdToPresentationId2, metadataIdToPresentationId);
	}

	@Test
	public void testPCollVarFactory() {
		String presentationOf = "someCollectionVar";
		String mode = "input";

		DataRecordGroup pCollVar = factory.factorPVarUsingPresentationOfDataDividerAndMode(
				presentationOf, "testSystem", mode);

		DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) pCollVar;
		assertCorrectRecordGroupCreated(recordGroup);
		assertCorrectDataInRecordInfo(recordGroup);
		assertCorrectPresentationOfLink(recordGroup, presentationOf);
		assertCorrectMode(recordGroup, mode);
		assertCorrectLinkToEmptyText(recordGroup);
	}

	private void assertCorrectRecordGroupCreated(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, recordGroup);
		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "presentation");
		recordGroup.MCR.assertParameters("addAttributeByIdWithValue", 0, "type", "pCollVar");
	}

	private void assertCorrectDataInRecordInfo(DataRecordGroupSpy recordGroup) {
		metadataIdToPresentationId.MCR.assertParameters(
				"createPresentationIdUsingMetadataIdAndMode", 0, "someCollectionVar", "input");
		var pVarId = metadataIdToPresentationId.MCR
				.getReturnValue("createPresentationIdUsingMetadataIdAndMode", 0);
		recordGroup.MCR.assertParameters("setId", 0, pVarId);
		recordGroup.MCR.assertParameters("setDataDivider", 0, "testSystem");
		recordGroup.MCR.assertParameters("setValidationType", 0, "presentationCollectionVar");
	}

	private void assertCorrectPresentationOfLink(DataRecordGroupSpy recordGroup,
			String presentationOf) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0,
				"presentationOf", "metadata", presentationOf);
		var presentationOfSpy = dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 0);
		recordGroup.MCR.assertParameters("addChild", 0, presentationOfSpy);
	}

	private void assertCorrectMode(DataRecordGroupSpy recordGroup, String mode) {
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "mode", mode);
		var modeSpy = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 0);
		recordGroup.MCR.assertParameters("addChild", 1, modeSpy);
	}

	private void assertCorrectLinkToEmptyText(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 1,
				"emptyTextId", "text", "initialEmptyValueText");
		var emptyTextLink = dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 1);
		recordGroup.MCR.assertParameters("addChild", 2, emptyTextLink);
	}

}
