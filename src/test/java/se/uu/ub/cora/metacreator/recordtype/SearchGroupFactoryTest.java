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
package se.uu.ub.cora.metacreator.recordtype;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;

public class SearchGroupFactoryTest {
	DataFactorySpy dataFactory = new DataFactorySpy();
	SearchGroupFactoryImp factory;
	private String recordTypeToSearchIn;
	private String dataDivider;

	@BeforeMethod
	public void setUp() {
		DataProvider.onlyForTestSetDataFactory(dataFactory);
		factory = new SearchGroupFactoryImp();

		recordTypeToSearchIn = "someRecordTypeId";
		dataDivider = "someDataDivider";
	}

	@Test
	public void testFactor() throws Exception {
		DataRecordGroup search = factory
				.factorUsingDataDividerAndRecordTypeIdToSearchIn(dataDivider, recordTypeToSearchIn);

		DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) search;
		assertCorrectRecordGroupCreated(recordGroup);
		assertCorrectDataInRecordInfo(recordGroup);
		assertCorrectRecordTypeToSearchInLink(recordGroup);
	}

	// @Test
	// public void testPCollVarFactory() {
	// String presentationOf = "someCollectionVar";
	// String mode = "input";
	//
	// DataRecordGroup pCollVar = factory.factorPVarUsingPresentationOfDataDividerAndMode(
	// presentationOf, "testSystem", mode);
	//
	// DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) pCollVar;
	// assertCorrectRecordGroupCreated(recordGroup);
	// // assertCorrectDataInRecordInfo(recordGroup);
	// // assertCorrectPresentationOfLink(recordGroup, presentationOf);
	// }

	private void assertCorrectRecordGroupCreated(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, recordGroup);
		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "search");
	}

	private void assertCorrectDataInRecordInfo(DataRecordGroupSpy recordGroup) {
		recordGroup.MCR.assertParameters("setId", 0, recordTypeToSearchIn + "Search");
		recordGroup.MCR.assertParameters("setDataDivider", 0, dataDivider);
		recordGroup.MCR.assertParameters("setValidationType", 0, "search");
	}

	private void assertCorrectRecordTypeToSearchInLink(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0,
				"recordTypeToSearchIn", "recordType", recordTypeToSearchIn);
		DataRecordLinkSpy typeToSearchIn = (DataRecordLinkSpy) dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 0);
		typeToSearchIn.MCR.assertParameters("setRepeatId", 0, "0");
		recordGroup.MCR.assertParameters("addChild", 0, typeToSearchIn);
	}

	// @Test
	// public void testCreateSearchGroup() {
	//
	// SearchGroupFactoryImp searchGroupCreator = SearchGroupFactoryImp
	// .withIdIdAndDataDividerAndRecordType("myRecordTypeSearch", "cora", "myRecordType");
	//
	// DataGroup searchGroup = searchGroupCreator.factorDataGroup("");
	// DataGroup recordInfo = searchGroup.getFirstGroupWithNameInData("recordInfo");
	// assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "myRecordTypeSearch");
	//
	// assertCorrectDataDivider(recordInfo);
	//
	// assertFalse(searchGroup.containsChildWithNameInData("childReferences"));
	//
	// assertCorrectRecordTypeToSearchIn(searchGroup);
	//
	// assertCorrectMetadataId(searchGroup);
	//
	// assertCorrectPresentationId(searchGroup);
	//
	// assertEquals(searchGroup.getFirstAtomicValueWithNameInData("searchGroup"), "autocomplete");
	//
	// assertCorrectTexts(searchGroup);
	//
	// }
	//
	// private void assertCorrectDataDivider(DataGroup recordInfo) {
	// DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
	// assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
	// assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "cora");
	// }
	//
	// private void assertCorrectRecordTypeToSearchIn(DataGroup searchGroup) {
	// DataRecordLink recordTypeToSearchIn = (DataRecordLink) searchGroup
	// .getFirstChildWithNameInData("recordTypeToSearchIn");
	// assertEquals(recordTypeToSearchIn.getLinkedRecordId(), "myRecordType");
	// assertEquals(recordTypeToSearchIn.getLinkedRecordType(), "recordType");
	// assertNotNull(recordTypeToSearchIn.getRepeatId());
	// }
	//
	// private void assertCorrectMetadataId(DataGroup searchGroup) {
	// DataRecordLink metadataId = (DataRecordLink) searchGroup
	// .getFirstChildWithNameInData("metadataId");
	// assertEquals(metadataId.getLinkedRecordId(), "autocompleteSearchGroup");
	// }
	//
	// private void assertCorrectPresentationId(DataGroup searchGroup) {
	// DataRecordLink presentationId = (DataRecordLink) searchGroup
	// .getFirstChildWithNameInData("presentationId");
	// assertEquals(presentationId.getLinkedRecordId(), "autocompleteSearchPGroup");
	// }
	//
	// private void assertCorrectTexts(DataGroup searchGroup) {
	// DataGroup textIdGroup = searchGroup.getFirstGroupWithNameInData("textId");
	// assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "myRecordTypeSearchText");
	// assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
	//
	// DataGroup defTextIdGroup = searchGroup.getFirstGroupWithNameInData("defTextId");
	// assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "myRecordTypeSearchDefText");
	// assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "coraText");
	// }
}
