/*
 * Copyright 2017, 2022, 2023, 2024 Uppsala University Library
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

import static org.testng.Assert.assertSame;

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.spy.CollectionVariableFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.spies.RecordCreatorSpy;
import se.uu.ub.cora.spider.spies.RecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class ColVarFromItemCollectionExtFuncTest {
	private SpiderInstanceFactorySpy spiderInstanceFactory;
	private String authToken;
	private ColVarFromItemCollectionExtFunc extendedFunctionality;
	private CollectionVariableFactorySpy colVarFactory;
	private DataRecordGroupSpy dataRecordGroup;
	private ExtendedFunctionalityData data;
	private RecordReaderSpy recordReaderSpy;
	private RecordCreatorSpy recordCreatorSpy;

	@BeforeMethod
	public void setUp() {
		spiderInstanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactory);
		recordReaderSpy = new RecordReaderSpy();
		spiderInstanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
				() -> recordReaderSpy);
		recordCreatorSpy = new RecordCreatorSpy();
		spiderInstanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordCreator",
				() -> recordCreatorSpy);

		authToken = "someAuthToken";
		setUpRecordGroupCreatedFromGroup();
		data = createExtendedFunctionalityWithDataGroupSpy();

		colVarFactory = new CollectionVariableFactorySpy();
		extendedFunctionality = ColVarFromItemCollectionExtFunc
				.usingCollectionVariableFactory(colVarFactory);
	}

	private void setUpRecordGroupCreatedFromGroup() {
		dataRecordGroup = new DataRecordGroupSpy();

		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of("itemCollection"), "type");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someCollection");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "someNameInData");
	}

	private ExtendedFunctionalityData createExtendedFunctionalityWithDataGroupSpy() {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataRecordGroup = dataRecordGroup;
		return data;
	}

	@Test
	public void testOnlyForTestGetColVarFactory() throws Exception {
		CollectionVariableFactory colVarFactory2 = extendedFunctionality
				.onlyForTestGetColVarFactory();
		assertSame(colVarFactory2, colVarFactory);
	}

	@Test
	public void testNoTypeOfMetadataDoNothing() throws Exception {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.empty(), "type");

		extendedFunctionality.useExtendedFunctionality(data);

		assertExtFuncDoesNothing();
	}

	private void assertExtFuncDoesNothing() {
		colVarFactory.MCR.assertMethodNotCalled(
				"factorCollectionVarUsingItemCollectionIdDataDividerAndNameInData");
		spiderInstanceFactory.MCR.assertMethodNotCalled("factorRecordReader");
		assertNoColVarCreatedInStorage();
	}

	@Test
	public void testWrongTypeOfMetadataDoNothing() throws Exception {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of("NOTitemCollection"), "type");

		extendedFunctionality.useExtendedFunctionality(data);

		assertExtFuncDoesNothing();
	}

	@Test
	public void testCollectionVarExistInStorage() {
		extendedFunctionality.useExtendedFunctionality(data);

		assertExtFuncFactorsColVarWithCorrectParameters();
		DataRecordGroupSpy collectionVarFromSpy = getFactoredColVar();
		assertStorageIsCheckedForExistenseOfFactoredColVar(collectionVarFromSpy);
		assertNoColVarCreatedInStorage();
	}

	private void assertExtFuncFactorsColVarWithCorrectParameters() {
		colVarFactory.MCR.assertParameters(
				"factorCollectionVarUsingItemCollectionIdDataDividerAndNameInData", 0,
				dataRecordGroup.getId(), dataRecordGroup.getDataDivider(),
				dataRecordGroup.getFirstAtomicValueWithNameInData("nameInData"));
	}

	private DataRecordGroupSpy getFactoredColVar() {
		return (DataRecordGroupSpy) colVarFactory.MCR.getReturnValue(
				"factorCollectionVarUsingItemCollectionIdDataDividerAndNameInData", 0);
	}

	private void assertStorageIsCheckedForExistenseOfFactoredColVar(
			DataRecordGroupSpy collectionVarFromSpy) {
		recordReaderSpy.MCR.assertParameters("readRecord", 0, authToken, "metadata",
				collectionVarFromSpy.getId());
	}

	private void assertNoColVarCreatedInStorage() {
		spiderInstanceFactory.MCR.assertMethodNotCalled("factorRecordCreator");
	}

	@Test
	public void testCollectionVarDoesNotExistInStorageAndIsStored() {
		setupRecordReaderToThrowErrorForReadWithId("inputIdRecordGroup");

		extendedFunctionality.useExtendedFunctionality(data);

		assertExtFuncFactorsColVarWithCorrectParameters();
		DataRecordGroupSpy collectionVarFromSpy = getFactoredColVar();
		assertStorageIsCheckedForExistenseOfFactoredColVar(collectionVarFromSpy);

		assertColVarGroupStoredInStorage(collectionVarFromSpy);
	}

	private void setupRecordReaderToThrowErrorForReadWithId(String id) {
		recordReaderSpy.MRV.setAlwaysThrowException("readRecord",
				RecordNotFoundException.withMessage("Record not found"));
	}

	private void assertColVarGroupStoredInStorage(Object groupFromFactoredColVar) {
		spiderInstanceFactory.MCR.assertParameters("factorRecordCreator", 0);
		recordCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0, authToken, "metadata",
				groupFromFactoredColVar);
	}
}
