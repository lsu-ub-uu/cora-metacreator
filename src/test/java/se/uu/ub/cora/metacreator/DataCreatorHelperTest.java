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
package se.uu.ub.cora.metacreator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;

public class DataCreatorHelperTest {
	private static final String SOME_VALIDATION_TYPE_ID = "someValidationTypeId";
	private static final String SOME_ID = "someId";
	private static final String DATA_DIVIDER_ID = "someDataDivider";

	private DataFactorySpy dataFactory;
	private DataCreatorHelper dataCreatorHelper;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		dataCreatorHelper = new DataCreatorHelperImp();

	}

	@Test
	public void testImplementsInterface() throws Exception {
		assertTrue(dataCreatorHelper instanceof DataCreatorHelper);
	}

	@Test
	public void testCreateRecordInfo() {
		DataGroupSpy recordInfo = (DataGroupSpy) dataCreatorHelper
				.createRecordInfoWithIdAndDataDividerAndValidationType(SOME_ID, DATA_DIVIDER_ID,
						SOME_VALIDATION_TYPE_ID);
		assertCorrectRecordInfo(recordInfo);
		assertCorrectId(recordInfo);
		assertCorrectDataDivider(recordInfo);
		assertCorrectValidationType(recordInfo);
	}

	private void assertCorrectRecordInfo(DataGroupSpy recordInfo) {
		String factoryMethodName = "factorGroupUsingNameInData";
		dataFactory.MCR.assertNumberOfCallsToMethod(factoryMethodName, 1);
		dataFactory.MCR.assertParameters(factoryMethodName, 0, "recordInfo");
		dataFactory.MCR.assertReturn(factoryMethodName, 0, recordInfo);

	}

	private void assertCorrectId(DataGroupSpy recordInfo) {
		String factoryMethodName = "factorAtomicUsingNameInDataAndValue";
		dataFactory.MCR.assertNumberOfCallsToMethod(factoryMethodName, 1);
		dataFactory.MCR.assertParameters(factoryMethodName, 0, "id", SOME_ID);
		var idSpy = dataFactory.MCR.getReturnValue(factoryMethodName, 0);
		recordInfo.MCR.assertParameters("addChild", 0, idSpy);
	}

	private void assertCorrectDataDivider(DataGroupSpy recordInfo) {
		String factoryMethodName = "factorRecordLinkUsingNameInDataAndTypeAndId";
		dataFactory.MCR.assertNumberOfCallsToMethod(factoryMethodName, 2);
		dataFactory.MCR.assertParameters(factoryMethodName, 0, "dataDivider", "system",
				DATA_DIVIDER_ID);
		var dataDividerSpy = dataFactory.MCR.getReturnValue(factoryMethodName, 0);
		recordInfo.MCR.assertParameters("addChild", 1, dataDividerSpy);
	}

	private void assertCorrectValidationType(DataGroupSpy recordInfo) {
		String factoryMethodName = "factorRecordLinkUsingNameInDataAndTypeAndId";
		dataFactory.MCR.assertNumberOfCallsToMethod(factoryMethodName, 2);
		dataFactory.MCR.assertParameters(factoryMethodName, 1, "validationType", "validationType",
				SOME_VALIDATION_TYPE_ID);
		var dataDividerSpy = dataFactory.MCR.getReturnValue(factoryMethodName, 1);
		recordInfo.MCR.assertParameters("addChild", 2, dataDividerSpy);
	}

	@Test
	public void testExtractDataDividerId() {
		String firstGroup = "getFirstGroupWithNameInData";
		String firstChild = "getFirstChildWithNameInData";
		DataGroupSpy recordInfo = new DataGroupSpy();
		recordInfo.MRV.setDefaultReturnValuesSupplier(firstChild, DataRecordLinkSpy::new);
		DataGroupSpy dataGroup = new DataGroupSpy();
		dataGroup.MRV.setDefaultReturnValuesSupplier(firstGroup, () -> recordInfo);

		String dataDivider = dataCreatorHelper.extractDataDividerIdFromDataGroup(dataGroup);

		dataGroup.MCR.assertParameters(firstGroup, 0, "recordInfo");
		DataRecordLinkSpy dividerSpy = (DataRecordLinkSpy) recordInfo.MCR.getReturnValue(firstChild,
				0);
		dividerSpy.MCR.assertReturn("getLinkedRecordId", 0, dataDivider);
	}

	@Test
	public void testExtractId() {
		DataGroup mainDataGroup = DataCreator.createTextVarGroupWithIdAndTextIdAndDefTextId(SOME_ID,
				"someTextId", "someDefTextId");
		String id = dataCreatorHelper.extractIdFromDataGroup(mainDataGroup);
		assertEquals(id, SOME_ID);
	}

}
