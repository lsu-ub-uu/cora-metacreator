/*
 * Copyright 2017, 2018, 2022 Uppsala University Library
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

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordCreatorOldSpy;
import se.uu.ub.cora.metacreator.spy.DataAtomicSpy;
import se.uu.ub.cora.metacreator.spy.RecordReaderSpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class PGroupFromMetadataGroupCreatorTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private static final String AUTH_TOKEN = "someAuthToken";
	// private DataGroupFactorySpy dataGroupFactory;
	// private DataAtomicFactorySpy dataAtomicFactory;
	// private DataRecordLinkFactory dataRecordLinkFactory;
	private PGroupFromMetadataGroupCreator extendedFunctionality;
	private DataGroupSpy metadataGroup;
	private PGroupFactorySpy pGroupFactory;
	private DataFactorySpy dataFactory;
	private DataRecordGroupSpy dataRecordGroup;

	@BeforeMethod
	public void setUp() {
		// dataGroupFactory = new DataGroupFactorySpy();
		// DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		// dataAtomicFactory = new DataAtomicFactorySpy();
		// DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		//
		// // instanceFactory = new SpiderInstanceFactoryOldSpy();
		// // SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		// dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		// DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);

		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someMetadataId");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");

		dataFactory = new DataFactorySpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> dataRecordGroup);
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);

		metadataGroup = new DataGroupSpy();
		metadataGroup.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "false");

		pGroupFactory = new PGroupFactorySpy();
		extendedFunctionality = PGroupFromMetadataGroupCreator.usingPGroupFactory(pGroupFactory);

	}

	@Test
	public void testConstructor() throws Exception {
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 1);

	}

	@Test
	public void testPGroupsIsCreated() {

		callExtendedFunctionalityWithGroup(metadataGroup);

		dataFactory.MCR.assertParameters("factorRecordGroupFromDataGroup", 0, metadataGroup);
		DataRecordGroupSpy dataRecordGroup = (DataRecordGroupSpy) dataFactory.MCR
				.getReturnValue("factorRecordGroupFromDataGroup", 0);

		dataRecordGroup.MCR.assertParameters("getId", 0);
		dataRecordGroup.MCR.assertParameters("getDataDivider", 0);
		dataRecordGroup.MCR.assertParameters("getChildrenOfTypeAndName", 0, DataGroup.class,
				"childReferences");

		var metadataId = dataRecordGroup.MCR.getReturnValue("getId", 0);
		var dataDivider = dataRecordGroup.MCR.getReturnValue("getDataDivider", 0);
		var metadataChildReferences = dataRecordGroup.MCR.getReturnValue("getChildrenOfTypeAndName",
				0);

		pGroupFactory.MCR.assertParameters(
				"factorPGroupWithIdDataDividerPresentationOfModeAndChildren", 0, dataDivider,
				metadataId, "input", metadataChildReferences);

		var pGroupInput = pGroupFactory.MCR
				.getReturnValue("factorPGroupWithIdDataDividerPresentationOfModeAndChildren", 0);

		RecordReaderSpy readerSpy = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 0);

		readerSpy.MCR.assertParameters("readRecord", 0, AUTH_TOKEN);

		// DataGroup metadataGroup = DataCreator
		// .createMetadataGroupWithIdAndTextVarAsChildReference("someTestGroup");
		//
		// callExtendedFunctionalityWithGroup(metadataGroup);
		//
		// assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
		// assertTrue(extendedFunctionality.constructor
		// .getPChildRefConstructorFactory() instanceof MetadataIdToPresentationIdImp);
		//
		// assertCorrectPGroupWithIndexPGroupIdAndChildId(0, "someTestPGroup", "somePVar", "input",
		// "someTextVarText");
		// assertCorrectPGroupWithIndexPGroupIdAndChildId(1, "someTestOutputPGroup",
		// "someOutputPVar",
		// "output", "someTextVarText");
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	private void assertCorrectPGroupWithIndexPGroupIdAndChildId(int index, String pGroupId,
			String childId, String mode, String textId) {
		SpiderRecordCreatorOldSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators
				.get(index);
		assertEquals(spiderRecordCreatorSpy.type, "presentationGroup");

		DataGroup record = spiderRecordCreatorSpy.record;

		assertEquals(record.getNameInData(), "presentation");

		assertCorrectRecordInfo(record, pGroupId);
		assertCorrectPresentationOf(record);

		assertCorrectChildRef(childId, "presentationVar", record, textId);
		assertEquals(record.getFirstAtomicValueWithNameInData("mode"), mode);
	}

	private void assertCorrectRecordInfo(DataGroup record, String expectedId) {
		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), expectedId);
		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "test");
	}

	private void assertCorrectPresentationOf(DataGroup record) {
		DataGroup presentationOf = record.getFirstGroupWithNameInData("presentationOf");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someTestGroup");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"metadataGroup");
	}

	private void assertCorrectChildRef(String childId, String childType, DataGroup record,
			String textId) {
		DataGroup childReferences = record.getFirstGroupWithNameInData("childReferences");
		List<DataGroup> childReferenceList = childReferences
				.getAllGroupsWithNameInData("childReference");
		DataGroup refGroupText = childReferenceList.get(0).getFirstGroupWithNameInData("refGroup");
		DataGroup refText = refGroupText.getFirstGroupWithNameInData("ref");
		assertEquals(refText.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
		assertEquals(refText.getFirstAtomicValueWithNameInData("linkedRecordId"), textId);

		DataGroup refGroupPresentation = childReferenceList.get(1)
				.getFirstGroupWithNameInData("refGroup");
		DataGroup ref = refGroupPresentation.getFirstGroupWithNameInData("ref");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"), childType);
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childId);
	}

	@Test
	public void testPGroupsAlreadyExist() {

		metadataGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData", () -> true);
		metadataGroup.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "true");

		callExtendedFunctionalityWithGroup(metadataGroup);

		instanceFactory.MCR.assertMethodNotCalled("factorRecordReader");

		// assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}

	// @Test
	// public void testPGroupsShouldNotBeCreated() {
	// metadataGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData", () -> true);
	// metadataGroup.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
	// () -> "true");
	// // DataGroup metadataGroup = DataCreator
	// // .createMetadataGroupWithIdAndTextVarAsChildReference("someTestGroup");
	// // metadataGroup.addChild(new DataAtomicSpy("excludePGroupCreation", "true"));
	//
	// callExtendedFunctionalityWithGroup(metadataGroup);
	//
	// instanceFactory.MCR.assertMethodNotCalled("factorRecordReader");
	// }

	@Test
	public void testPGroupsExcludeCreationIsTrueSoPGroupsShouldNotBeCreated() {
		// TODO: Är vi säkra att detta är en OR, det känns att det borde vara ett AND.
		metadataGroup.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "true");

		callExtendedFunctionalityWithGroup(metadataGroup);

		instanceFactory.MCR.assertMethodNotCalled("factorRecordReader");

		// DataGroup metadataGroup = DataCreator
		// .createMetadataGroupWithIdAndTextVarAsChildReference("someTestGroup");
		// metadataGroup.addChild(new DataAtomicSpy("excludePGroupCreation", "true"));
		//
		// callExtendedFunctionalityWithGroup(metadataGroup);
		//
		// assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}

	@Test
	public void testPGroupsExcludeCreationIsFalseSoPGroupsShouldBeCreated() {
		DataGroup metadataGroup = DataCreator
				.createMetadataGroupWithIdAndTextVarAsChildReference("someTestGroup");
		metadataGroup.addChild(new DataAtomicSpy("excludePGroupCreation", "false"));

		callExtendedFunctionalityWithGroup(metadataGroup);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
	}

	@Test
	public void testPGroupsNotPossibleToCreatePGroups() {
		DataGroup metadataGroup = DataCreator
				.createMetadataGroupWithIdAndTextVarAsChildReference("someTestGroup");
		DataGroup childReferences = metadataGroup.getFirstGroupWithNameInData("childReferences");
		childReferences.removeFirstChildWithNameInData("childReference");

		callExtendedFunctionalityWithGroup(metadataGroup);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}
}
