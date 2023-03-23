/*
 * Copyright 2016, 2017, 2022, 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.recordtype;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class RecordTypeAddMissingLinksTest {
	private static final String ID = "someRecordId";
	private String authToken;
	private RecordTypeAddMissingLinks extFunc;

	private DataFactorySpy dataFactory;
	private DataGroupSpy recordType;
	private DataRecordGroupSpy recordGroup;

	@BeforeMethod
	public void setUp() {
		extFunc = new RecordTypeAddMissingLinks();
		recordType = new DataGroupSpy();

		recordGroup = new DataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> ID);
		dataFactory = new DataFactorySpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> recordGroup);
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		authToken = "testUser";
	}

	@Test
	public void testConvertToDataRecordAndRedaId() {

		callExtendedFunctionalityWithGroup(recordType);

		dataFactory.MCR.assertParameters("factorRecordGroupFromDataGroup", 0, recordType);
		recordGroup.MCR.assertParameters("getId", 0);
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		extFunc.useExtendedFunctionality(data);
	}

	@Test
	public void testDefaultValuesWhenAllValuesMissing() {

		callExtendedFunctionalityWithGroup(recordType);

		assertAddLinkToRecordType(0, "metadataId", "metadata", ID + "Group");
		assertAddLinkToRecordType(1, "newMetadataId", "metadata", ID + "NewGroup");
		assertAddLinkToRecordType(2, "presentationViewId", "presentation", ID + "OutputPGroup");
		assertAddLinkToRecordType(3, "presentationFormId", "presentation", ID + "PGroup");
		assertAddLinkToRecordType(4, "newPresentationFormId", "presentation", ID + "NewPGroup");
		assertAddLinkToRecordType(5, "menuPresentationViewId", "presentation", ID + "MenuPGroup");
		assertAddLinkToRecordType(6, "listPresentationViewId", "presentation", ID + "ListPGroup");
		assertAddLinkToRecordType(7, "autocompletePresentationView", "presentation",
				ID + "AutocompletePGroup");

		dataFactory.MCR.assertNumberOfCallsToMethod("factorRecordLinkUsingNameInDataAndTypeAndId",
				8);
	}

	private void assertAddLinkToRecordType(int callNumber, String nameInData,
			String linkedRecordType, String linkedRecordId) {
		recordType.MCR.assertParameters("containsChildWithNameInData", callNumber, nameInData);
		var recordLink = assertCreateLink(callNumber, nameInData, linkedRecordType, linkedRecordId);
		recordType.MCR.assertParameters("addChild", callNumber, recordLink);
	}

	private Object assertCreateLink(int callNumber, String nameInData, String linkedRecordType,
			String linkedRecordId) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", callNumber,
				nameInData, linkedRecordType, linkedRecordId);
		return dataFactory.MCR.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId",
				callNumber);
	}

	@Test
	public void testAllLinksExists() throws Exception {
		recordType.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData", () -> true);

		callExtendedFunctionalityWithGroup(recordType);

		dataFactory.MCR.assertNumberOfCallsToMethod("factorRecordLinkUsingNameInDataAndTypeAndId",
				0);
	}

	@Test
	public void testAddPublicToFalse() throws Exception {

		callExtendedFunctionalityWithGroup(recordType);

		recordType.MCR.assertParameters("containsChildWithNameInData", 8, "public");
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "public",
				"false");
		var atomic = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 0);
		recordType.MCR.assertParameters("addChild", 8, atomic);
	}

	@Test
	public void testPublicExist() throws Exception {
		recordType.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData", () -> true);

		callExtendedFunctionalityWithGroup(recordType);

		dataFactory.MCR.assertNumberOfCallsToMethod("factorAtomicUsingNameInDataAndValue", 0);
	}
}
