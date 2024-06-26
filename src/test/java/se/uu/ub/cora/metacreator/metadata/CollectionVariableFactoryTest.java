/*
 * Copyright 2017, 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.metadata;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class CollectionVariableFactoryTest {
	private CollectionVariableFactory factory;
	private DataFactorySpy dataFactory;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		factory = new CollectionVariableFactoryImp();
	}

	@Test
	public void testcreateCollectionVar() throws Exception {
		String itemCollectionId = "someItemCollection";
		String nameInData = "someNameInData";
		DataRecordGroupSpy collectionVar = (DataRecordGroupSpy) factory
				.factorCollectionVarUsingItemCollectionIdDataDividerAndNameInData(itemCollectionId,
						"someDataDivider", nameInData);

		assertCorrectRecordGroupCreated(collectionVar);
		assertCorrectDataInRecordInfo(collectionVar);
		assertCorrectNameInData(collectionVar, nameInData);
		assertCorrectRefCollectionLink(collectionVar, itemCollectionId);
	}

	private void assertCorrectRecordGroupCreated(DataRecordGroup collectionVar) {
		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "metadata");
		dataFactory.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, collectionVar);
	}

	private void assertCorrectDataInRecordInfo(DataRecordGroupSpy collectionVar) {
		collectionVar.MCR.assertParameters("addAttributeByIdWithValue", 0, "type",
				"collectionVariable");
		collectionVar.MCR.assertParameters("setId", 0, "someItemCollection" + "Var");
		collectionVar.MCR.assertParameters("setDataDivider", 0, "someDataDivider");
		collectionVar.MCR.assertParameters("setValidationType", 0, "metadataCollectionVariable");
	}

	private void assertCorrectNameInData(DataRecordGroupSpy collectionVar, String nameInData) {
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "nameInData",
				nameInData);
		var nameInDataVar = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue",
				0);
		collectionVar.MCR.assertParameters("addChild", 0, nameInDataVar);
	}

	private void assertCorrectRefCollectionLink(DataRecordGroupSpy collectionVar,
			String refCollection) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0,
				"refCollection", "metadata", refCollection);
		var itemLink = dataFactory.MCR.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId",
				0);
		collectionVar.MCR.assertParameters("addChild", 1, itemLink);
	}
}
