/*
 * Copyright 2017, 2022 Uppsala University Library
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
package se.uu.ub.cora.metacreator.collection;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordCreatorSpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class PCollVarFromCollectionVarCreatorTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private String authToken;

	private DataFactorySpy dataFactory;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;
	private PCollVarFromCollectionVarCreator extendedFunctionality;
	private DataGroupSpy recordInfo;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);
		recordInfo = new DataGroupSpy();
		// dataFactory.MRV.setSpecificReturnValuesSupplier("factorGroupUsingNameInData",
		// () -> recordInfo, "recordInfo");
		dataFactory.MRV.setSpecificReturnValuesSupplier("factorGroupUsingNameInData",
				() -> recordInfo, "recordInfo");
		recordInfo.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "recordInfo");

		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		authToken = "testUser";
		extendedFunctionality = new PCollVarFromCollectionVarCreator();
	}

	@Test
	public void testPCollVarsDoesNotExist() {
		DataGroup collectionVar = DataCreator
				.createCollectionVariableWithIdDataDividerAndNameInData("someTestCollectionVar",
						"testSystem", "some");

		callExtendedFunctionalityWithGroup(collectionVar);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
		assertCorrectlyCreatedInputPCollVar();
		assertCorrectlyCreatedOutputPCollVar();
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	private void assertCorrectlyCreatedInputPCollVar() {
		SpiderRecordCreatorSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators.get(0);
		assertEquals(spiderRecordCreatorSpy.type, "presentationCollectionVar");
		DataGroup record = spiderRecordCreatorSpy.record;

		assertEquals(record.getNameInData(), "presentation");
		assertEquals(record.getFirstAtomicValueWithNameInData("mode"), "input");

		assertCorrectPresentationOf(record);
		assertCorrectRecordInfo(record, "someTestPCollVar");
		assertEquals(record.getAttribute("type").getValue(), "pCollVar");
	}

	private void assertCorrectPresentationOf(DataGroup record) {
		DataGroup presentationOf = record.getFirstGroupWithNameInData("presentationOf");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someTestCollectionVar");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"metadataCollectionVariable");
	}

	private void assertCorrectRecordInfo(DataGroup record, String expextedId) {
		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		// assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), expextedId);
		// DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		// assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"),
		// "testSystem");
		testCreatedRecordInfo();
	}

	//
	public void testCreatedRecordInfo() {
		// DataGroupSpy recordInfo = (DataGroupSpy) DataCreatorHelper
		// .createRecordInfoWithIdAndDataDividerAndValidationType(SOME_ID, DATA_DIVIDER_ID,
		// SOME_VALIDATION_TYPE_ID);
		assertCorrectRecordInfo(recordInfo);
		assertCorrectId(recordInfo);
		assertCorrectDataDivider(recordInfo);
		assertCorrectValidationType(recordInfo);
	}

	private void assertCorrectRecordInfo(DataGroupSpy recordInfo) {
		String factoryMethodName = "factorGroupUsingNameInData";
		// dataFactory.MCR.assertNumberOfCallsToMethod(factoryMethodName, 1);
		dataFactory.MCR.assertParameters(factoryMethodName, 0, "recordInfo");
		dataFactory.MCR.assertReturn(factoryMethodName, 0, recordInfo);

	}

	private void assertCorrectId(DataGroupSpy recordInfo) {
		String factoryMethodName = "factorAtomicUsingNameInDataAndValue";
		// dataFactory.MCR.assertNumberOfCallsToMethod(factoryMethodName, 1);
		dataFactory.MCR.assertParameters(factoryMethodName, 0, "id", "");
		var idSpy = dataFactory.MCR.getReturnValue(factoryMethodName, 0);
		recordInfo.MCR.assertParameters("addChild", 0, idSpy);
	}

	private void assertCorrectDataDivider(DataGroupSpy recordInfo) {
		String factoryMethodName = "factorRecordLinkUsingNameInDataAndTypeAndId";
		// dataFactory.MCR.assertNumberOfCallsToMethod(factoryMethodName, 2);
		dataFactory.MCR.assertParameters(factoryMethodName, 0, "dataDivider", "system", "");
		var dataDividerSpy = dataFactory.MCR.getReturnValue(factoryMethodName, 0);
		recordInfo.MCR.assertParameters("addChild", 1, dataDividerSpy);
	}

	private void assertCorrectValidationType(DataGroupSpy recordInfo) {
		String factoryMethodName = "factorRecordLinkUsingNameInDataAndTypeAndId";
		dataFactory.MCR.assertNumberOfCallsToMethod(factoryMethodName, 2);
		dataFactory.MCR.assertParameters(factoryMethodName, 1, "validationType", "validationType",
				"");
		var dataDividerSpy = dataFactory.MCR.getReturnValue(factoryMethodName, 1);
		recordInfo.MCR.assertParameters("addChild", 2, dataDividerSpy);
	}
	//

	private void assertCorrectlyCreatedOutputPCollVar() {
		SpiderRecordCreatorSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators.get(1);
		assertEquals(spiderRecordCreatorSpy.type, "presentationCollectionVar");
		DataGroup record = spiderRecordCreatorSpy.record;
		assertEquals(record.getNameInData(), "presentation");
		assertEquals(record.getFirstAtomicValueWithNameInData("mode"), "output");

		assertCorrectPresentationOf(record);
		assertCorrectRecordInfo(record, "someTestOutputPCollVar");
		assertEquals(record.getAttribute("type").getValue(), "pCollVar");
	}

	@Test
	public void testPCollVarsAlreadyExist() {
		DataGroup collectionVar = DataCreator
				.createCollectionVariableWithIdDataDividerAndNameInData("someExistingCollectionVar",
						"testSystem", "someExisting");

		callExtendedFunctionalityWithGroup(collectionVar);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}
}
