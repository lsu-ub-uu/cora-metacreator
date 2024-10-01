/*
 * Copyright 2017, 2018, 2022, 2024 Uppsala University Library
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
package se.uu.ub.cora.metacreator.group;

import static org.testng.Assert.assertSame;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.spies.RecordCreatorSpy;
import se.uu.ub.cora.spider.spies.RecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;

public class PGroupFromMetadataGroupExtFuncTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private static final String AUTH_TOKEN = "someAuthToken";
	private PGroupFromMetadataGroupExtFunc extendedFunctionality;
	private PGroupFactorySpy pGroupFactory;
	private DataRecordGroupSpy dataRecordGroup;
	private RecordCreatorSpy recordCreator;
	private RecordReaderSpy recordReader;
	private DataRecordGroupSpy pGroupInput;
	private DataRecordGroupSpy pGroupOutput;
	private DataGroupSpy childReferencesGroup;

	@BeforeMethod
	public void setUp() {
		DataGroupSpy childReference1 = new DataGroupSpy();
		DataGroupSpy childReference2 = new DataGroupSpy();
		List<DataGroup> metadataChildReferences = List.of(childReference1, childReference2);

		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someMetadataId");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of("group"), "type");

		childReferencesGroup = new DataGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> childReferencesGroup);
		childReferencesGroup.MRV.setDefaultReturnValuesSupplier("getChildrenOfTypeAndName",
				() -> metadataChildReferences);

		recordReader = new RecordReaderSpy();
		recordCreator = new RecordCreatorSpy();
		instanceFactory = new SpiderInstanceFactorySpy();
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
				() -> recordReader);
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordCreator",
				() -> recordCreator);
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);

		pGroupFactory = new PGroupFactorySpy();
		pGroupInput = new DataRecordGroupSpy();
		pGroupInput.MRV.setDefaultReturnValuesSupplier("getId", () -> "someIdPGroup");
		pGroupOutput = new DataRecordGroupSpy();
		pGroupOutput.MRV.setDefaultReturnValuesSupplier("getId", () -> "someIdOutputPGroup");
		pGroupFactory.MRV.setSpecificReturnValuesSupplier(
				"factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences",
				() -> pGroupInput, "someDataDivider", "someMetadataId", "input",
				metadataChildReferences);
		pGroupFactory.MRV.setSpecificReturnValuesSupplier(
				"factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences",
				() -> pGroupOutput, "someDataDivider", "someMetadataId", "output",
				metadataChildReferences);
		extendedFunctionality = PGroupFromMetadataGroupExtFunc.usingPGroupFactory(pGroupFactory);
	}

	@Test
	public void testOnlyForTestGetPGroupFactory() throws Exception {
		assertSame(pGroupFactory, extendedFunctionality.onlyForTestGetPGroupFactory());
	}

	@Test
	public void testRecordReaderAndRecordCreatedFetchedFromInstanceProvider() throws Exception {
		callExtendedFunctionalityWithGroup(dataRecordGroup);

		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 1);
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordCreator", 1);
	}

	@Test
	public void testNoTypeOfMetadataDoNothing() throws Exception {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.empty(), "type");

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		assertExtFuncDoesNothing();
	}

	private void assertExtFuncDoesNothing() {
		dataRecordGroup.MCR.assertMethodNotCalled("containsChildWithNameInData");
		recordReader.MCR.assertNumberOfCallsToMethod("readRecord", 0);
	}

	@Test
	public void testWrongTypeOfMetadataDoNothing() throws Exception {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of("NOTmetadataGroup"), "type");

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		assertExtFuncDoesNothing();
	}

	@Test
	public void testPGroupsIsCreated() {
		recordReader.MRV.setAlwaysThrowException("readRecord", new RuntimeException());

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		dataRecordGroup.MCR.assertParameters("getId", 0);
		dataRecordGroup.MCR.assertParameters("getDataDivider", 0);
		dataRecordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 0, DataGroup.class,
				"childReferences");
		DataGroupSpy childReferencesGroup = (DataGroupSpy) dataRecordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 0);

		var metadataId = dataRecordGroup.MCR.getReturnValue("getId", 0);
		var dataDivider = dataRecordGroup.MCR.getReturnValue("getDataDivider", 0);

		var metadataChildReferences = childReferencesGroup.MCR
				.getReturnValue("getChildrenOfTypeAndName", 0);

		assertCreateReadAndStorePGroupByMode(metadataId, dataDivider, metadataChildReferences,
				pGroupInput, "input", 0);
		assertCreateReadAndStorePGroupByMode(metadataId, dataDivider, metadataChildReferences,
				pGroupOutput, "output", 1);
	}

	private void assertCreateReadAndStorePGroupByMode(Object metadataId, Object dataDivider,
			Object metadataChildReferences, DataRecordGroupSpy pGroup, String mode,
			int callNumber) {
		// Create PGroup
		pGroupFactory.MCR.assertParameters(
				"factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences",
				callNumber, dataDivider, metadataId, mode, metadataChildReferences);
		// Read record
		recordReader.MCR.assertParameters("readRecord", callNumber, AUTH_TOKEN, "presentation",
				pGroup.getId());

		recordCreator.MCR.assertParameters("createAndStoreRecord", callNumber, AUTH_TOKEN,
				"presentation", pGroup);
	}

	@Test
	public void testPGroupAlreadyExists() throws Exception {
		callExtendedFunctionalityWithGroup(dataRecordGroup);

		recordReader.MCR.assertNumberOfCallsToMethod("readRecord", 2);
		recordCreator.MCR.assertMethodNotCalled("createAndStoreRecord");
	}

	private void callExtendedFunctionalityWithGroup(DataRecordGroupSpy metadataRecordGroup2) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataRecordGroup = metadataRecordGroup2;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	@Test
	public void testPGroupsExcludeCreationExistsButIsFalseSoPGroupsShouldBeCreated() {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "excludePGroupCreation");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "false", "excludePGroupCreation");

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		recordReader.MCR.assertNumberOfCallsToMethod("readRecord", 2);
	}

	@Test
	public void testPGroupsExcludeCreationIsTrueSoPGroupsShouldNotBeCreated() {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "excludePGroupCreation");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "true", "excludePGroupCreation");

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		recordReader.MCR.assertNumberOfCallsToMethod("readRecord", 0);
	}

	@Test
	public void testMetadataChildrenReferencesNoChilds() {
		childReferencesGroup.MRV.setDefaultReturnValuesSupplier("getChildrenOfTypeAndName",
				() -> Collections.emptyList());

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		recordReader.MCR.assertNumberOfCallsToMethod("readRecord", 0);
	}
}
