/*
 * Copyright 2016, 2017, 2022, 2023, 2024 Uppsala University Library
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
package se.uu.ub.cora.metacreator.validationtype;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class ValidationTypeAddMissingLinksExtFuncTest {
	private static final String ID = "someRecordId";
	private String authToken;
	private ValidationTypeAddMissingLinksExtFunc extFunc;

	private DataFactorySpy dataFactory;
	private DataRecordGroupSpy recordGroup;

	@BeforeMethod
	public void setUp() {
		extFunc = new ValidationTypeAddMissingLinksExtFunc();
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
		callExtendedFunctionalityWithGroup(recordGroup);

		recordGroup.MCR.assertParameters("getId", 0);
	}

	private void callExtendedFunctionalityWithGroup(DataRecordGroupSpy recordGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataRecordGroup = recordGroup;
		extFunc.useExtendedFunctionality(data);
	}

	@Test
	public void testDefaultValuesWhenAllValuesMissing() {
		callExtendedFunctionalityWithGroup(recordGroup);

		assertAddLinkToRecordType(0, "metadataId", "metadata", ID + "Group");
		assertAddLinkToRecordType(1, "newMetadataId", "metadata", ID + "NewGroup");
		assertAddLinkToRecordType(2, "presentationFormId", "presentation", ID + "PGroup");
		assertAddLinkToRecordType(3, "newPresentationFormId", "presentation", ID + "NewPGroup");

		dataFactory.MCR.assertNumberOfCallsToMethod("factorRecordLinkUsingNameInDataAndTypeAndId",
				4);
	}

	private void assertAddLinkToRecordType(int callNumber, String nameInData,
			String linkedRecordType, String linkedRecordId) {
		recordGroup.MCR.assertParameters("containsChildWithNameInData", callNumber, nameInData);
		var recordLink = assertCreateLink(callNumber, nameInData, linkedRecordType, linkedRecordId);
		recordGroup.MCR.assertParameters("addChild", callNumber, recordLink);
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
		recordGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData", () -> true);

		callExtendedFunctionalityWithGroup(recordGroup);

		dataFactory.MCR.assertNumberOfCallsToMethod("factorRecordLinkUsingNameInDataAndTypeAndId",
				0);
	}
}
