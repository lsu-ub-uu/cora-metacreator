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
package se.uu.ub.cora.metacreator.recordtype;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataAtomicSpy;
import se.uu.ub.cora.metacreator.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordCreatorSpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class RecordTypeCreatorTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private String authToken;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;
	private RecordTypeCreator extendedFunctionality;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		authToken = "testUser";
		extendedFunctionality = RecordTypeCreator.forImplementingTextType("textSystemOne");
	}

	@Test
	public void testRecordTypeCreatorNoMetadataGroupOrPresentationsExists() {

		DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType");
		DataCreator.addAllValuesToDataGroup(recordType, "myRecordType");

		callExtendedFunctionalityWithGroup(recordType);
		assertEquals(instanceFactory.spiderRecordCreators.size(), 10);

		assertCorrectlyCreatedMetadataGroup(2, "myRecordTypeGroup", "recordInfoGroup",
				"myRecordType");
		assertCorrectlyCreatedMetadataGroup(3, "myRecordTypeNewGroup", "recordInfoNewGroup",
				"myRecordType");

		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(4,
				"myRecordTypePGroup", "myRecordTypeGroup", "input", "recordInfoPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(5,
				"myRecordTypeNewPGroup", "myRecordTypeNewGroup", "input", "recordInfoNewPGroup");

		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(6,
				"myRecordTypeOutputPGroup", "myRecordTypeGroup", "output",
				"recordInfoOutputPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(7,
				"myRecordTypeMenuPGroup", "myRecordTypeGroup", "output", "recordInfoOutputPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(8,
				"myRecordTypeListPGroup", "myRecordTypeGroup", "output", "recordInfoOutputPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(9,
				"myRecordTypeAutocompletePGroup", "myRecordTypeGroup", "input", "recordInfoPGroup");

	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	private void assertCorrectlyCreatedMetadataGroup(int createdPGroupNo, String id,
			String childRefId, String nameInData) {
		SpiderRecordCreatorSpy spiderRecordCreator = instanceFactory.spiderRecordCreators
				.get(createdPGroupNo);
		assertEquals(spiderRecordCreator.type, "metadataGroup");

		DataGroup record = spiderRecordCreator.record;
		assertEquals(record.getFirstAtomicValueWithNameInData("nameInData"), nameInData);
		assertCorrectUserAndRecordInfo(id, spiderRecordCreator);
		assertCorrectlyCreatedMetadataChildReference(childRefId, spiderRecordCreator.record);

		DataGroup textIdGroup = record.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
		DataGroup defTextIdGroup = record.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"coraText");

		assertEquals(record.getFirstAtomicValueWithNameInData("excludePGroupCreation"), "true");
	}

	private void assertCorrectlyCreatedMetadataChildReference(String childRefId, DataGroup record) {
		DataGroup childRef = getChildRefbyIndex(record, 0);
		assertEquals(record.getFirstGroupWithNameInData("childReferences").getChildren().size(), 1);
		DataGroup ref = (DataGroup) childRef.getFirstChildWithNameInData("ref");

		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childRefId);
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"), "metadataGroup");
		assertEquals(childRef.getFirstAtomicValueWithNameInData("repeatMin"), "1");
		assertEquals(childRef.getFirstAtomicValueWithNameInData("repeatMax"), "1");
	}

	private void assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(
			int index, String id, String presentationOf, String mode, String childRefId) {

		SpiderRecordCreatorSpy spiderRecordCreator = instanceFactory.spiderRecordCreators
				.get(index);
		assertCorrectlyCreatedPresentationGroup(spiderRecordCreator, index, id, presentationOf,
				mode);
		assertCorrectlyCreatedPresentationChildReference(childRefId, spiderRecordCreator.record);
	}

	private void assertCorrectlyCreatedPresentationGroup(SpiderRecordCreatorSpy spiderRecordCreator,
			int createdPGroupNo, String id, String presentationOf, String mode) {
		assertEquals(spiderRecordCreator.type, "presentationGroup");
		DataGroup record = spiderRecordCreator.record;
		DataGroup presentationOfGroup = record.getFirstGroupWithNameInData("presentationOf");
		assertEquals(presentationOfGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				presentationOf);
		assertCorrectUserAndRecordInfo(id, spiderRecordCreator);
		assertEquals(record.getFirstAtomicValueWithNameInData("mode"), mode);
	}

	private void assertCorrectUserAndRecordInfo(String id,
			SpiderRecordCreatorSpy spiderRecordCreator) {
		assertEquals(spiderRecordCreator.authToken, authToken);
		DataGroup recordInfo = spiderRecordCreator.record.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), id);

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "test");
	}

	private void assertCorrectlyCreatedPresentationChildReference(String childRefId,
			DataGroup record) {
		DataGroup childRef = getChildRefbyIndex(record, 1);
		assertEquals(record.getFirstGroupWithNameInData("childReferences").getChildren().size(), 2);
		DataGroup refGroup = (DataGroup) childRef.getFirstChildWithNameInData("refGroup");
		assertEquals(refGroup.getRepeatId(), "0");

		DataGroup ref = (DataGroup) refGroup.getFirstChildWithNameInData("ref");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childRefId);
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"presentationGroup");
		assertFalse(childRef.containsChildWithNameInData("default"));
		assertFalse(childRef.containsChildWithNameInData("repeatMax"));

	}

	private DataGroup getChildRefbyIndex(DataGroup record, int index) {
		DataGroup childReferences = record.getFirstGroupWithNameInData("childReferences");

		DataGroup childRef = childReferences.getAllGroupsWithNameInData("childReference")
				.get(index);
		return childRef;
	}

	@Test
	public void testPGroupCreatorAllPresentationsExists() {
		DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType2");
		DataCreator.addAllValuesToDataGroup(recordType, "myRecordType2");

		callExtendedFunctionalityWithGroup(recordType);
		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}

	@Test
	public void testRecordTypeCreatorNoTextsExists() {
		DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType");
		DataCreator.addAllValuesToDataGroup(recordType, "myRecordType");

		callExtendedFunctionalityWithGroup(recordType);
		assertEquals(instanceFactory.spiderRecordCreators.size(), 10);
		SpiderRecordCreatorSpy spiderRecordCreator = instanceFactory.spiderRecordCreators.get(0);
		assertEquals(spiderRecordCreator.type, "textSystemOne");
		SpiderRecordCreatorSpy spiderRecordCreator2 = instanceFactory.spiderRecordCreators.get(1);
		assertEquals(spiderRecordCreator2.type, "textSystemOne");

	}

	@Test
	public void testRecordTypeCreatorMetadataGroupsExistButNoPresentations() {
		DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType3");
		DataCreator.addAllValuesToDataGroup(recordType, "myRecordType3");

		callExtendedFunctionalityWithGroup(recordType);
		assertEquals(instanceFactory.spiderRecordCreators.size(), 8);

		assertCorrectPresentationByIndexIdModeRecordInfoRefAndChildPresentation(2,
				"myRecordType3PGroup", "input", "recordInfoPGroup", "somePVar", 4);
		assertCorrectPresentationByIndexIdModeRecordInfoRefAndChildPresentation(3,
				"myRecordType3NewPGroup", "input", "recordInfoNewPGroup", "somePVar", 4);

		assertCorrectPresentationByIndexIdModeRecordInfoRefAndChildPresentation(4,
				"myRecordType3OutputPGroup", "output", "recordInfoOutputPGroup", "someOutputPVar",
				4);
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(5,
				"myRecordType3MenuPGroup", "myRecordType3Group", "output",
				"recordInfoOutputPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(6,
				"myRecordType3ListPGroup", "myRecordType3Group", "output",
				"recordInfoOutputPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(7,
				"myRecordType3AutocompletePGroup", "myRecordType3Group", "input",
				"recordInfoPGroup");
	}

	private void assertCorrectPresentationByIndexIdModeRecordInfoRefAndChildPresentation(int index,
			String id, String mode, String recordInfoRef, String childPresentationId,
			int expectedNumberOfChildren) {
		SpiderRecordCreatorSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators
				.get(index);
		DataGroup createdRecord = spiderRecordCreatorSpy.record;
		DataGroup childReferences = createdRecord.getFirstGroupWithNameInData("childReferences");
		assertEquals(childReferences.getChildren().size(), expectedNumberOfChildren);
		assertCorrectChildByIndexAndRefId(childReferences, 1, childPresentationId);
		assertCorrectChildByIndexAndRefId(childReferences, 3, recordInfoRef);
		DataGroup ref = getRefByIndex(childReferences, 1);

		DataGroup recordInfo = createdRecord.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), id);

		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childPresentationId);
		assertEquals(createdRecord.getFirstAtomicValueWithNameInData("mode"), mode);
	}

	private DataGroup getRefByIndex(DataGroup childReferences, int index) {
		DataGroup childReference = childReferences.getAllGroupsWithNameInData("childReference")
				.get(index);
		DataGroup refGroup = childReference.getFirstGroupWithNameInData("refGroup");
		return refGroup.getFirstGroupWithNameInData("ref");
	}

	private void assertCorrectChildByIndexAndRefId(DataGroup childReferences, int index,
			String childRefId) {
		DataGroup recordInfo = (DataGroup) childReferences.getChildren().get(index);
		DataGroup refGroup = recordInfo.getFirstGroupWithNameInData("refGroup");
		DataGroup ref = refGroup.getFirstGroupWithNameInData("ref");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childRefId);
	}

	@Test
	public void testRecordTypeCreatorMetadataGroupsExistButNoPresentationsAndOneChildPresentationDoesNotExist() {
		DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType4");
		DataCreator.addAllValuesToDataGroup(recordType, "myRecordType4");

		callExtendedFunctionalityWithGroup(recordType);
		assertEquals(instanceFactory.spiderRecordCreators.size(), 8);
		assertCorrectNumberOfChildReferencesForIndex(4, 2);
		assertCorrectNumberOfChildReferencesForIndex(4, 3);
		assertCorrectNumberOfChildReferencesForIndex(4, 4);
		assertCorrectNumberOfChildReferencesForIndex(2, 5);
		assertCorrectNumberOfChildReferencesForIndex(2, 6);
		assertCorrectNumberOfChildReferencesForIndex(2, 7);

	}

	private void assertCorrectNumberOfChildReferencesForIndex(int numberOfChildReferences,
			int index) {
		SpiderRecordCreatorSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators
				.get(index);
		DataGroup childReferences = spiderRecordCreatorSpy.record
				.getFirstGroupWithNameInData("childReferences");
		assertEquals(childReferences.getChildren().size(), numberOfChildReferences);
	}

	@Test
	public void testRecordTypeCreatorWithAutogeneratedIdNoMetadataGroupOrPresentationsExists() {
		instanceFactory.userSuppliedId = false;

		DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType");
		recordType.removeFirstChildWithNameInData("userSuppliedId");
		recordType.addChild(new DataAtomicSpy("userSuppliedId", "false"));
		DataCreator.addAllValuesToDataGroup(recordType, "myRecordType");

		callExtendedFunctionalityWithGroup(recordType);
		assertEquals(instanceFactory.spiderRecordCreators.size(), 10);

		assertCorrectlyCreatedMetadataGroup(2, "myRecordTypeGroup", "recordInfoGroup",
				"myRecordType");
		assertCorrectlyCreatedMetadataGroup(3, "myRecordTypeNewGroup",
				"recordInfoAutogeneratedNewGroup", "myRecordType");

		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(4,
				"myRecordTypePGroup", "myRecordTypeGroup", "input", "recordInfoPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(5,
				"myRecordTypeNewPGroup", "myRecordTypeNewGroup", "input",
				"recordInfoAutogeneratedNewPGroup");

		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(6,
				"myRecordTypeOutputPGroup", "myRecordTypeGroup", "output",
				"recordInfoOutputPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(7,
				"myRecordTypeMenuPGroup", "myRecordTypeGroup", "output", "recordInfoOutputPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(8,
				"myRecordTypeListPGroup", "myRecordTypeGroup", "output", "recordInfoOutputPGroup");
		assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(9,
				"myRecordTypeAutocompletePGroup", "myRecordTypeGroup", "input", "recordInfoPGroup");

	}

}
