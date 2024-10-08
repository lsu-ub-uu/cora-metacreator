/*
 * Copyright 2016, 2023 Olov McKie
 * Copyright 2022, 2024 Uppsala University Library
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

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.spy.PVarFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.spies.RecordCreatorSpy;
import se.uu.ub.cora.spider.spies.RecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class PVarFromVarExtFuncTest {
	private PVarFactoryFactorySpy pVarFFactory;
	private DataRecordGroupSpy dataRecordGroup;
	private SpiderInstanceFactorySpy spiderInstanceFactory;

	private String authToken;

	private ExtendedFunctionality extendedFunctionality;
	private ExtendedFunctionalityData data;
	private RecordReaderSpy recordReaderSpy;
	private PVarFactorySpy pVarFactory;

	@BeforeMethod
	public void setUp() {
		pVarFFactory = new PVarFactoryFactorySpy();

		pVarFactory = new PVarFactorySpy();

		pVarFFactory.MRV.setDefaultReturnValuesSupplier("factorUsingRecordGroup",
				() -> Optional.of(pVarFactory));

		setUpRecordGroupCreatedFromGroup();

		spiderInstanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactory);

		recordReaderSpy = new RecordReaderSpy();
		spiderInstanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
				() -> recordReaderSpy);
		authToken = "testUser";

		data = createExtendedFunctionalityWithDataGroupSpy();

		extendedFunctionality = PVarFromVarExtFunc.usingPVarFactoryFactory(pVarFFactory);
	}

	private void setUpRecordGroupCreatedFromGroup() {
		dataRecordGroup = new DataRecordGroupSpy();

		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someVariableId");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");
	}

	private ExtendedFunctionalityData createExtendedFunctionalityWithDataGroupSpy() {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataRecordGroup = dataRecordGroup;
		return data;
	}

	@Test
	public void testOnlyForTestGetPVarFactoryFactory() throws Exception {
		PVarFactoryFactory pVarFactory2 = ((PVarFromVarExtFunc) extendedFunctionality)
				.onlyForTestGetPVarFactoryFactory();
		assertSame(pVarFactory2, pVarFFactory);
	}

	@Test
	public void testNoPVarFactoryCreatedDoNothing() throws Exception {
		pVarFFactory.MRV.setDefaultReturnValuesSupplier("factorUsingRecordGroup", Optional::empty);

		extendedFunctionality.useExtendedFunctionality(data);

		pVarFactory.MCR.assertMethodNotCalled("factorPVarUsingPresentationOfDataDividerAndMode");
	}

	@Test
	public void testInputIsCreated() throws Exception {
		extendedFunctionality.useExtendedFunctionality(data);

		var id = dataRecordGroup.MCR.getReturnValue("getId", 0);
		var dataDivider = dataRecordGroup.MCR.getReturnValue("getDataDivider", 0);

		pVarFFactory.MCR.assertParameters("factorUsingRecordGroup", 0, dataRecordGroup);
		pVarFactory.MCR.assertParameters("factorPVarUsingPresentationOfDataDividerAndMode", 0, id,
				dataDivider, "input");
	}

	@Test
	public void testOutputIsCreated() throws Exception {
		extendedFunctionality.useExtendedFunctionality(data);

		var id = dataRecordGroup.MCR.getReturnValue("getId", 0);
		var dataDivider = dataRecordGroup.MCR.getReturnValue("getDataDivider", 0);

		pVarFactory.MCR.assertParameters("factorPVarUsingPresentationOfDataDividerAndMode", 1, id,
				dataDivider, "output");
	}

	@Test
	public void testInputAndOutputIsStoredIfNotInStorageSinceBefore() throws Exception {
		setupPVarFactoryToReturnRecordWithIds("inputIdRecordGroup", "outputIdRecordGroup");
		setupRecordReaderToThrowErrorForReadWithId("inputIdRecordGroup");

		extendedFunctionality.useExtendedFunctionality(data);

		DataRecordGroupSpy recordGroupInput = getRecordGroupReturnedFromPVarFactory(0);
		assertStorageIsCheckedForPresenceOfRecordGroup(recordGroupInput, 0);
		assertRecordGroupIsTurnedIntoGroupAndStored(recordGroupInput, 0);

		DataRecordGroupSpy recordGroupOutput = getRecordGroupReturnedFromPVarFactory(1);
		assertStorageIsCheckedForPresenceOfRecordGroup(recordGroupOutput, 1);
		assertRecordGroupIsTurnedIntoGroupAndStored(recordGroupOutput, 1);
	}

	private void setupPVarFactoryToReturnRecordWithIds(String id, String id2) {
		DataRecordGroupSpy createdPVarGroup = new DataRecordGroupSpy();
		createdPVarGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> id);
		createdPVarGroup.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> id2 + "Type");
		DataRecordGroupSpy createdPVarGroup2 = new DataRecordGroupSpy();
		createdPVarGroup2.MRV.setDefaultReturnValuesSupplier("getId", () -> id2);
		createdPVarGroup2.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> id2 + "Type");

		pVarFactory.MRV.setSpecificReturnValuesSupplier(
				"factorPVarUsingPresentationOfDataDividerAndMode", () -> createdPVarGroup,
				"someVariableId", "someDataDivider", "input");
		pVarFactory.MRV.setSpecificReturnValuesSupplier(
				"factorPVarUsingPresentationOfDataDividerAndMode", () -> createdPVarGroup2,
				"someVariableId", "someDataDivider", "output");
	}

	private void setupRecordReaderToThrowErrorForReadWithId(String id) {
		recordReaderSpy.MRV.setAlwaysThrowException("readRecord",
				RecordNotFoundException.withMessage("Record not found"));
	}

	private DataRecordGroupSpy getRecordGroupReturnedFromPVarFactory(int no) {
		return (DataRecordGroupSpy) pVarFactory.MCR
				.getReturnValue("factorPVarUsingPresentationOfDataDividerAndMode", no);
	}

	private void assertStorageIsCheckedForPresenceOfRecordGroup(DataRecordGroupSpy recordGroup,
			int no) {
		var id = recordGroup.MCR.getReturnValue("getId", 0);
		RecordReaderSpy recordReaderInput = (RecordReaderSpy) spiderInstanceFactory.MCR
				.getReturnValue("factorRecordReader", no);
		recordReaderInput.MCR.assertParameters("readRecord", no, authToken, "presentation", id);
	}

	private void assertRecordGroupIsTurnedIntoGroupAndStored(DataRecordGroupSpy recordGroupInput,
			int no) {
		RecordCreatorSpy recordCreatorInput = (RecordCreatorSpy) spiderInstanceFactory.MCR
				.getReturnValue("factorRecordCreator", no);
		recordCreatorInput.MCR.assertParameters("createAndStoreRecord", 0, authToken,
				"presentation", recordGroupInput);
	}

	@Test
	public void testInputAndOutputIsNotStoredIfInStorageSinceBefore() throws Exception {
		setupPVarFactoryToReturnRecordWithIds("inputIdRecordGroup", "outputIdRecordGroup");

		extendedFunctionality.useExtendedFunctionality(data);

		DataRecordGroupSpy recordGroupInput = getRecordGroupReturnedFromPVarFactory(0);
		assertStorageIsCheckedForPresenceOfRecordGroup(recordGroupInput, 0);
		assertRecordGroupIsNotStored(recordGroupInput);
	}

	private void assertRecordGroupIsNotStored(DataRecordGroupSpy recordGroupInput) {
		spiderInstanceFactory.MCR.assertMethodNotCalled("factorRecordCreator");
	}
}
