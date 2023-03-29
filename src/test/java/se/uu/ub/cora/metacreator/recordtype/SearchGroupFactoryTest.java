/*
 * Copyright 2023 Uppsala University Library
 * Copyright 2023 Olov McKie
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
	SearchGroupFactory factory;
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
				.factorUsingRecordTypeIdToSearchInAndDataDivider(recordTypeToSearchIn, dataDivider);

		DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) search;
		assertCorrectRecordGroupCreated(recordGroup);
		assertCorrectDataInRecordInfo(recordGroup);
		assertCorrectRecordTypeToSearchInLink(recordGroup);
		assertCorrectMetadataIdLink(recordGroup);
		assertCorrectPresentationIdLink(recordGroup);
		assertCorrectSearchGroup(recordGroup);
	}

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

	private void assertCorrectMetadataIdLink(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 1,
				"metadataId", "metadata", "autocompleteSearchGroup");
		DataRecordLinkSpy link = (DataRecordLinkSpy) dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 1);
		recordGroup.MCR.assertParameters("addChild", 1, link);
	}

	private void assertCorrectPresentationIdLink(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 2,
				"presentationId", "presentation", "autocompleteSearchPGroup");
		DataRecordLinkSpy link = (DataRecordLinkSpy) dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 2);
		recordGroup.MCR.assertParameters("addChild", 2, link);
	}

	private void assertCorrectSearchGroup(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "searchGroup",
				"autocomplete");
		var atomic = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 0);
		recordGroup.MCR.assertParameters("addChild", 3, atomic);
	}
}
