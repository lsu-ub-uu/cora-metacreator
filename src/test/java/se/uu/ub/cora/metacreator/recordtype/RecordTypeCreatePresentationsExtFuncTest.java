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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.data.spies.DataRecordSpy;
import se.uu.ub.cora.metacreator.group.PGroupFactorySpy;
import se.uu.ub.cora.metacreator.spy.RecordCreatorSpy;
import se.uu.ub.cora.metacreator.spy.RecordReaderSpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class RecordTypeCreatePresentationsExtFuncTest {

	private RecordTypeCreatePresentationsExtFunc extfunc;
	private DataFactorySpy dataFactory;
	private SpiderInstanceFactorySpy instanceFactory;

	private static final String METADATA_ID_LINK_ID = "someMetadataLinkId";
	private static final String NEW_METADATA_ID_LINK_ID = "someNewMetadataIdLinkId";
	private static final String P_FORM_ID_LINK_ID = "somePresentationFormId";
	private static final String NEW_P_FORM_ID_LINK_ID = "someNewPresentationFormId";
	private static final String P_VIEW_ID_LINK_ID = "somePresentationViewId";
	private static final String MENU_P_VIEW_ID_LINK_ID = "someMenuPresentationViewId";
	private static final String LIST_P_VIEW_ID_LINK_ID = "someListPresentationViewId";
	private static final String AUTOCOMPLETE_P_VIEW_ID_LINK_ID = "someAutocompletePresentationView";

	private static final String AUTH_TOKEN = "someAuthToken";
	private static final String DATA_DIVIDER = "someDataDivider";

	private RecordReaderSpy recordReader;
	private RecordCreatorSpy recordCreator;
	private PGroupFactorySpy pGroupFactory;
	private DataGroupSpy recordType;
	private DataRecordGroupSpy recordGroup;

	@BeforeMethod
	public void setUp() {
		recordType = new DataGroupSpy();
		pGroupFactory = new PGroupFactorySpy();

		recordGroup = new DataRecordGroupSpy();

		// SET LINKS
		DataRecordLinkSpy metadataIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy newMetadataIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy presentationFormIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy newPresentationFormIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy presentationViewIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy menuPresentationViewLink = new DataRecordLinkSpy();
		DataRecordLinkSpy listPresentationViewIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy autocompletePresentationViewLink = new DataRecordLinkSpy();

		metadataIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> METADATA_ID_LINK_ID);
		newMetadataIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> NEW_METADATA_ID_LINK_ID);
		presentationFormIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> P_FORM_ID_LINK_ID);
		newPresentationFormIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> NEW_P_FORM_ID_LINK_ID);
		presentationViewIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> P_VIEW_ID_LINK_ID);
		menuPresentationViewLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> MENU_P_VIEW_ID_LINK_ID);
		listPresentationViewIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> LIST_P_VIEW_ID_LINK_ID);
		autocompletePresentationViewLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> AUTOCOMPLETE_P_VIEW_ID_LINK_ID);

		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataIdLink, DataRecordLink.class, "metadataId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newMetadataIdLink, DataRecordLink.class, "newMetadataId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> presentationFormIdLink, DataRecordLink.class, "presentationFormId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newPresentationFormIdLink, DataRecordLink.class, "newPresentationFormId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> presentationViewIdLink, DataRecordLink.class, "presentationViewId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> menuPresentationViewLink, DataRecordLink.class, "menuPresentationViewId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> listPresentationViewIdLink, DataRecordLink.class, "listPresentationViewId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> autocompletePresentationViewLink, DataRecordLink.class,
				"autocompletePresentationView");

		//////////////// STOP setting links

		recordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider", () -> DATA_DIVIDER);

		dataFactory = new DataFactorySpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> recordGroup);
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		recordReader = new RecordReaderSpy();
		recordCreator = new RecordCreatorSpy();

		setRecordReaderToReturnRecordWithChildReferenceIds(METADATA_ID_LINK_ID, "someRefId");
		setRecordReaderToReturnRecordWithChildReferenceIds(NEW_METADATA_ID_LINK_ID, "someRefId");

		instanceFactory = new SpiderInstanceFactorySpy();
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
				() -> recordReader);
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordCreator",
				() -> recordCreator);
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);

		extfunc = RecordTypeCreatePresentationsExtFunc.usingPGroupFactory(pGroupFactory);
	}

	@Test
	public void testConstructor() throws Exception {
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 1);
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordCreator", 1);
	}

	@Test
	public void testCheckDataDividerFromRecordGroup() throws Exception {

		callExtendedFunctionalityWithGroup(recordType);

		dataFactory.MCR.assertParameters("factorRecordGroupFromDataGroup", 0, recordType);
		recordGroup.MCR.assertParameters("getDataDivider", 0);
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataGroup = dataGroup;
		extfunc.useExtendedFunctionality(data);
	}

	@Test
	public void testReadPresentationOfForMeatadataAndMetadataId() throws Exception {

		recordReader.MRV.setThrowException("readRecord",
				new RecordNotFoundException("someErrorMessage"), AUTH_TOKEN, "presentation",
				MENU_P_VIEW_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordType);

		recordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 0, DataRecordLink.class,
				"metadataId");
		recordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 1, DataRecordLink.class,
				"newMetadataId");
		DataRecordLinkSpy metadataLink = (DataRecordLinkSpy) recordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 0);
		DataRecordLinkSpy newMetadataLink = (DataRecordLinkSpy) recordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 1);

		recordReader.MCR.assertParameters("readRecord", 0, AUTH_TOKEN, "metadata",
				metadataLink.getLinkedRecordId());
		recordReader.MCR.assertParameters("readRecord", 1, AUTH_TOKEN, "metadata",
				newMetadataLink.getLinkedRecordId());

	}

	private void setRecordReaderToReturnRecordWithChildReferenceIds(String metadataIdLinkId,
			String... childRefrenceIds) {
		DataRecordSpy newMetadataRecord = new DataRecordSpy();
		recordReader.MRV.setSpecificReturnValuesSupplier("readRecord", () -> newMetadataRecord,
				AUTH_TOKEN, "metadata", metadataIdLinkId);
		DataGroupSpy newMetadataGroup = new DataGroupSpy();
		newMetadataRecord.MRV.setDefaultReturnValuesSupplier("getDataGroup",
				() -> newMetadataGroup);

		DataGroupSpy newMetadataGroupChildReferences = new DataGroupSpy();
		newMetadataGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newMetadataGroupChildReferences, DataGroup.class, "childReferences");

		List<DataGroupSpy> childRefs = new ArrayList<>();
		for (String childRefrenceId : childRefrenceIds) {
			DataGroupSpy childRef1 = new DataGroupSpy();
			DataRecordLinkSpy ref1 = new DataRecordLinkSpy();
			childRef1.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName", () -> ref1,
					DataRecordLink.class, "ref");
			ref1.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> childRefrenceId);
			childRefs.add(childRef1);
		}
		newMetadataGroupChildReferences.MRV.setSpecificReturnValuesSupplier(
				"getChildrenOfTypeAndName", () -> childRefs, DataGroup.class, "childReference");
	}

	@Test
	public void testFormPresentation() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				new RecordNotFoundException("someErrorMessage"), AUTH_TOKEN, "presentation",
				P_FORM_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(2, "presentationFormId", P_FORM_ID_LINK_ID);
		assertCreationAndStoreOfPresentation(readMetadataIdRecord(), P_FORM_ID_LINK_ID,
				METADATA_ID_LINK_ID, "input");
	}

	private void assertCreationAndStoreOfPresentation(DataRecordSpy readRecordMetadataId,
			String presentationFormIdLinkId, String metadataIdLinkId, String mode) {
		var listOfChildReferences = assertReadChildReferencesFromRecord(readRecordMetadataId);

		pGroupFactory.MCR.assertParameters(
				"factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences", 0,
				presentationFormIdLinkId, DATA_DIVIDER, metadataIdLinkId, mode,
				listOfChildReferences);

		DataRecordGroupSpy formPresentation = (DataRecordGroupSpy) pGroupFactory.MCR.getReturnValue(
				"factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences", 0);

		dataFactory.MCR.assertParameters("factorGroupFromDataRecordGroup", 0, formPresentation);
		var pFormGroup = dataFactory.MCR.getReturnValue("factorGroupFromDataRecordGroup", 0);
		recordCreator.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN,
				"presentationGroup", pFormGroup);
		recordCreator.MCR.assertNumberOfCallsToMethod("createAndStoreRecord", 1);
	}

	private Object assertReadChildReferencesFromRecord(DataRecordSpy readRecordMetadataId) {
		DataGroupSpy metadataIdDataGroup = (DataGroupSpy) readRecordMetadataId.MCR
				.getReturnValue("getDataGroup", 0);

		metadataIdDataGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 0, DataGroup.class,
				"childReferences");
		DataGroupSpy childReferences = (DataGroupSpy) metadataIdDataGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 0);
		childReferences.MCR.assertParameters("getChildrenOfTypeAndName", 0, DataGroup.class,
				"childReference");
		var listOfChildReferences = childReferences.MCR.getReturnValue("getChildrenOfTypeAndName",
				0);
		return listOfChildReferences;
	}

	@Test
	public void testPresentationsAlreadyExists() throws Exception {
		callExtendedFunctionalityWithGroup(recordType);

		recordReader.MCR.assertNumberOfCallsToMethod("readRecord", 8);
		recordCreator.MCR.assertNumberOfCallsToMethod("createAndStoreRecord", 0);
	}

	@Test
	public void testFormNewPresentation() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				new RecordNotFoundException("someErrorMessage"), AUTH_TOKEN, "presentation",
				NEW_P_FORM_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(3, "newPresentationFormId", NEW_P_FORM_ID_LINK_ID);
		assertCreationAndStoreOfPresentation(readNewMetadataIdRecord(), NEW_P_FORM_ID_LINK_ID,
				NEW_METADATA_ID_LINK_ID, "input");
	}

	private DataRecordSpy readMetadataIdRecord() {
		DataRecordSpy readRecordMetadataId = (DataRecordSpy) recordReader.MCR
				.getReturnValue("readRecord", 0);
		return readRecordMetadataId;
	}

	private DataRecordSpy readNewMetadataIdRecord() {
		DataRecordSpy readRecordMetadataId = (DataRecordSpy) recordReader.MCR
				.getReturnValue("readRecord", 1);
		return readRecordMetadataId;
	}

	@Test
	public void testViewPresentation() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				new RecordNotFoundException("someErrorMessage"), AUTH_TOKEN, "presentation",
				P_VIEW_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(4, "presentationViewId", P_VIEW_ID_LINK_ID);
		assertCreationAndStoreOfPresentation(readMetadataIdRecord(), P_VIEW_ID_LINK_ID,
				METADATA_ID_LINK_ID, "output");
	}

	@Test
	public void testMenuPresentationViewId() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				new RecordNotFoundException("someErrorMessage"), AUTH_TOKEN, "presentation",
				MENU_P_VIEW_ID_LINK_ID);

		setRecordReaderToReturnRecordWithChildReferenceIds(METADATA_ID_LINK_ID, "someRefId",
				"recordInfoSomeRefId");

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(5, "menuPresentationViewId", MENU_P_VIEW_ID_LINK_ID);
		assertCreationAndStoreOfPresentationWithOnlyRecordInfos("recordInfoSomeRefId",
				MENU_P_VIEW_ID_LINK_ID, METADATA_ID_LINK_ID, "output");
	}

	private void assertCreationAndStoreOfPresentationWithOnlyRecordInfos(
			String idStartingWithRecordInfo, String presentationFormIdLinkId,
			String metadataIdLinkId, String mode) {

		assertListOfMetadataChildReferencesInCallToPGroupFactoryOnlyContainsReferencePointingTo(0,
				idStartingWithRecordInfo);

		pGroupFactory.MCR.assertParameters(
				"factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences", 0,
				presentationFormIdLinkId, DATA_DIVIDER, metadataIdLinkId, mode);

		DataRecordGroupSpy formPresentation = (DataRecordGroupSpy) pGroupFactory.MCR.getReturnValue(
				"factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences", 0);

		dataFactory.MCR.assertParameters("factorGroupFromDataRecordGroup", 0, formPresentation);
		var pFormGroup = dataFactory.MCR.getReturnValue("factorGroupFromDataRecordGroup", 0);
		recordCreator.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN,
				"presentationGroup", pFormGroup);
		recordCreator.MCR.assertNumberOfCallsToMethod("createAndStoreRecord", 1);
	}

	private void assertListOfMetadataChildReferencesInCallToPGroupFactoryOnlyContainsReferencePointingTo(
			int callNoToPGroupFactory, String idStartingWithRecordInfo) {
		List<DataGroup> metadataChildReferences = (List<DataGroup>) pGroupFactory.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(
						"factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences",
						callNoToPGroupFactory, "metadataChildReferences");
		assertEquals(metadataChildReferences.size(), 1);
		DataGroup metadataChildReference = metadataChildReferences.get(0);
		DataRecordLink refLink = metadataChildReference
				.getFirstChildOfTypeAndName(DataRecordLink.class, "ref");
		assertEquals(refLink.getLinkedRecordId(), idStartingWithRecordInfo);
	}

	@Test
	public void testListPresentationViewId() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				new RecordNotFoundException("someErrorMessage"), AUTH_TOKEN, "presentation",
				LIST_P_VIEW_ID_LINK_ID);

		setRecordReaderToReturnRecordWithChildReferenceIds(METADATA_ID_LINK_ID, "someRefId",
				"recordInfoSomeRefId");

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(6, "listPresentationViewId", LIST_P_VIEW_ID_LINK_ID);
		assertCreationAndStoreOfPresentationWithOnlyRecordInfos("recordInfoSomeRefId",
				LIST_P_VIEW_ID_LINK_ID, METADATA_ID_LINK_ID, "output");
	}

	@Test
	public void testAutocompletePresentationView() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				new RecordNotFoundException("someErrorMessage"), AUTH_TOKEN, "presentation",
				AUTOCOMPLETE_P_VIEW_ID_LINK_ID);

		setRecordReaderToReturnRecordWithChildReferenceIds(METADATA_ID_LINK_ID, "someRefId",
				"recordInfoSomeRefId");

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(7, "autocompletePresentationView", AUTOCOMPLETE_P_VIEW_ID_LINK_ID);
		assertCreationAndStoreOfPresentationWithOnlyRecordInfos("recordInfoSomeRefId",
				AUTOCOMPLETE_P_VIEW_ID_LINK_ID, METADATA_ID_LINK_ID, "input");
	}

	private void assertPresentationLink(int callNumber, String groupName, String presentationId) {
		recordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", callNumber,
				DataRecordLink.class, groupName);

		recordReader.MCR.assertParameters("readRecord", callNumber, AUTH_TOKEN, "presentation",
				presentationId);
	}

	// @Test
	// public void testRecordTypeCreatorNoMetadataGroupOrPresentationsExists() {
	//
	// DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType");
	// DataCreator.addAllValuesToDataGroup(recordType, "myRecordType");
	//
	// callExtendedFunctionalityWithGroup(recordType);
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 10);
	//
	// assertCorrectlyCreatedMetadataGroup(2, "myRecordTypeGroup", "recordInfoGroup",
	// "myRecordType");
	// assertCorrectlyCreatedMetadataGroup(3, "myRecordTypeNewGroup", "recordInfoNewGroup",
	// "myRecordType");
	//
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(4,
	// "myRecordTypePGroup", "myRecordTypeGroup", "input", "recordInfoPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(5,
	// "myRecordTypeNewPGroup", "myRecordTypeNewGroup", "input", "recordInfoNewPGroup");
	//
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(6,
	// "myRecordTypeOutputPGroup", "myRecordTypeGroup", "output",
	// "recordInfoOutputPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(7,
	// "myRecordTypeMenuPGroup", "myRecordTypeGroup", "output", "recordInfoOutputPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(8,
	// "myRecordTypeListPGroup", "myRecordTypeGroup", "output", "recordInfoOutputPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(9,
	// "myRecordTypeAutocompletePGroup", "myRecordTypeGroup", "input", "recordInfoPGroup");
	//
	// }
	//
	// private void assertCorrectlyCreatedMetadataGroup(int createdPGroupNo, String id,
	// String childRefId, String nameInData) {
	// SpiderRecordCreatorOldSpy spiderRecordCreator = instanceFactory.spiderRecordCreators
	// .get(createdPGroupNo);
	// assertEquals(spiderRecordCreator.type, "metadataGroup");
	//
	// DataGroup record = spiderRecordCreator.record;
	// assertEquals(record.getFirstAtomicValueWithNameInData("nameInData"), nameInData);
	// assertCorrectUserAndRecordInfo(id, spiderRecordCreator);
	// assertCorrectlyCreatedMetadataChildReference(childRefId, spiderRecordCreator.record);
	//
	// DataGroup textIdGroup = record.getFirstGroupWithNameInData("textId");
	// assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
	// DataGroup defTextIdGroup = record.getFirstGroupWithNameInData("defTextId");
	// assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "coraText");
	//
	// assertEquals(record.getFirstAtomicValueWithNameInData("excludePGroupCreation"), "true");
	// }
	//
	// private void assertCorrectlyCreatedMetadataChildReference(String childRefId, DataGroup
	// record) {
	// DataGroup childRef = getChildRefbyIndex(record, 0);
	// assertEquals(record.getFirstGroupWithNameInData("childReferences").getChildren().size(), 1);
	// DataGroup ref = (DataGroup) childRef.getFirstChildWithNameInData("ref");
	//
	// assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childRefId);
	// assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"), "metadataGroup");
	// assertEquals(childRef.getFirstAtomicValueWithNameInData("repeatMin"), "1");
	// assertEquals(childRef.getFirstAtomicValueWithNameInData("repeatMax"), "1");
	// }
	//
	// private void
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(
	// int index, String id, String presentationOf, String mode, String childRefId) {
	//
	// SpiderRecordCreatorOldSpy spiderRecordCreator = instanceFactory.spiderRecordCreators
	// .get(index);
	// assertCorrectlyCreatedPresentationGroup(spiderRecordCreator, index, id, presentationOf,
	// mode);
	// assertCorrectlyCreatedPresentationChildReference(childRefId, spiderRecordCreator.record);
	// }
	//
	// private void assertCorrectlyCreatedPresentationGroup(
	// SpiderRecordCreatorOldSpy spiderRecordCreator, int createdPGroupNo, String id,
	// String presentationOf, String mode) {
	// assertEquals(spiderRecordCreator.type, "presentationGroup");
	// DataGroup record = spiderRecordCreator.record;
	// DataGroup presentationOfGroup = record.getFirstGroupWithNameInData("presentationOf");
	// assertEquals(presentationOfGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// presentationOf);
	// assertCorrectUserAndRecordInfo(id, spiderRecordCreator);
	// assertEquals(record.getFirstAtomicValueWithNameInData("mode"), mode);
	// }
	//
	// private void assertCorrectUserAndRecordInfo(String id,
	// SpiderRecordCreatorOldSpy spiderRecordCreator) {
	// assertEquals(spiderRecordCreator.authToken, authToken);
	// DataGroup recordInfo = spiderRecordCreator.record.getFirstGroupWithNameInData("recordInfo");
	// assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), id);
	//
	// DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
	// assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "test");
	// }
	//
	// private void assertCorrectlyCreatedPresentationChildReference(String childRefId,
	// DataGroup record) {
	// DataGroup childRef = getChildRefbyIndex(record, 1);
	// assertEquals(record.getFirstGroupWithNameInData("childReferences").getChildren().size(), 2);
	// DataGroup refGroup = (DataGroup) childRef.getFirstChildWithNameInData("refGroup");
	// assertEquals(refGroup.getRepeatId(), "0");
	//
	// DataGroup ref = (DataGroup) refGroup.getFirstChildWithNameInData("ref");
	// assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childRefId);
	// assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "presentationGroup");
	// assertFalse(childRef.containsChildWithNameInData("default"));
	// assertFalse(childRef.containsChildWithNameInData("repeatMax"));
	//
	// }
	//
	// private DataGroup getChildRefbyIndex(DataGroup record, int index) {
	// DataGroup childReferences = record.getFirstGroupWithNameInData("childReferences");
	//
	// DataGroup childRef = childReferences.getAllGroupsWithNameInData("childReference")
	// .get(index);
	// return childRef;
	// }
	//
	// @Test
	// public void testPGroupCreatorAllPresentationsExists() {
	// DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType2");
	// DataCreator.addAllValuesToDataGroup(recordType, "myRecordType2");
	//
	// callExtendedFunctionalityWithGroup(recordType);
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	// }
	//
	// @Test
	// public void testRecordTypeCreatorNoTextsExists() {
	// DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType");
	// DataCreator.addAllValuesToDataGroup(recordType, "myRecordType");
	//
	// callExtendedFunctionalityWithGroup(recordType);
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 10);
	// SpiderRecordCreatorOldSpy spiderRecordCreator = instanceFactory.spiderRecordCreators.get(0);
	// assertEquals(spiderRecordCreator.type, "textSystemOne");
	// SpiderRecordCreatorOldSpy spiderRecordCreator2 = instanceFactory.spiderRecordCreators
	// .get(1);
	// assertEquals(spiderRecordCreator2.type, "textSystemOne");
	//
	// }
	//
	// @Test
	// public void testRecordTypeCreatorMetadataGroupsExistButNoPresentations() {
	// DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType3");
	// DataCreator.addAllValuesToDataGroup(recordType, "myRecordType3");
	//
	// callExtendedFunctionalityWithGroup(recordType);
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 8);
	//
	// assertCorrectPresentationByIndexIdModeRecordInfoRefAndChildPresentation(2,
	// "myRecordType3PGroup", "input", "recordInfoPGroup", "somePVar", 4);
	// assertCorrectPresentationByIndexIdModeRecordInfoRefAndChildPresentation(3,
	// "myRecordType3NewPGroup", "input", "recordInfoNewPGroup", "somePVar", 4);
	//
	// assertCorrectPresentationByIndexIdModeRecordInfoRefAndChildPresentation(4,
	// "myRecordType3OutputPGroup", "output", "recordInfoOutputPGroup", "someOutputPVar",
	// 4);
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(5,
	// "myRecordType3MenuPGroup", "myRecordType3Group", "output",
	// "recordInfoOutputPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(6,
	// "myRecordType3ListPGroup", "myRecordType3Group", "output",
	// "recordInfoOutputPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(7,
	// "myRecordType3AutocompletePGroup", "myRecordType3Group", "input",
	// "recordInfoPGroup");
	// }
	//
	// private void assertCorrectPresentationByIndexIdModeRecordInfoRefAndChildPresentation(int
	// index,
	// String id, String mode, String recordInfoRef, String childPresentationId,
	// int expectedNumberOfChildren) {
	// SpiderRecordCreatorOldSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators
	// .get(index);
	// DataGroup createdRecord = spiderRecordCreatorSpy.record;
	// DataGroup childReferences = createdRecord.getFirstGroupWithNameInData("childReferences");
	// assertEquals(childReferences.getChildren().size(), expectedNumberOfChildren);
	// assertCorrectChildByIndexAndRefId(childReferences, 1, childPresentationId);
	// assertCorrectChildByIndexAndRefId(childReferences, 3, recordInfoRef);
	// DataGroup ref = getRefByIndex(childReferences, 1);
	//
	// DataGroup recordInfo = createdRecord.getFirstGroupWithNameInData("recordInfo");
	// assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), id);
	//
	// assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childPresentationId);
	// assertEquals(createdRecord.getFirstAtomicValueWithNameInData("mode"), mode);
	// }
	//
	// private DataGroup getRefByIndex(DataGroup childReferences, int index) {
	// DataGroup childReference = childReferences.getAllGroupsWithNameInData("childReference")
	// .get(index);
	// DataGroup refGroup = childReference.getFirstGroupWithNameInData("refGroup");
	// return refGroup.getFirstGroupWithNameInData("ref");
	// }
	//
	// private void assertCorrectChildByIndexAndRefId(DataGroup childReferences, int index,
	// String childRefId) {
	// DataGroup recordInfo = (DataGroup) childReferences.getChildren().get(index);
	// DataGroup refGroup = recordInfo.getFirstGroupWithNameInData("refGroup");
	// DataGroup ref = refGroup.getFirstGroupWithNameInData("ref");
	// assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), childRefId);
	// }
	//
	// @Test
	// public void
	// testRecordTypeCreatorMetadataGroupsExistButNoPresentationsAndOneChildPresentationDoesNotExist()
	// {
	// DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType4");
	// DataCreator.addAllValuesToDataGroup(recordType, "myRecordType4");
	//
	// callExtendedFunctionalityWithGroup(recordType);
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 8);
	// assertCorrectNumberOfChildReferencesForIndex(4, 2);
	// assertCorrectNumberOfChildReferencesForIndex(4, 3);
	// assertCorrectNumberOfChildReferencesForIndex(4, 4);
	// assertCorrectNumberOfChildReferencesForIndex(2, 5);
	// assertCorrectNumberOfChildReferencesForIndex(2, 6);
	// assertCorrectNumberOfChildReferencesForIndex(2, 7);
	//
	// }
	//
	// private void assertCorrectNumberOfChildReferencesForIndex(int numberOfChildReferences,
	// int index) {
	// SpiderRecordCreatorOldSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators
	// .get(index);
	// DataGroup childReferences = spiderRecordCreatorSpy.record
	// .getFirstGroupWithNameInData("childReferences");
	// assertEquals(childReferences.getChildren().size(), numberOfChildReferences);
	// }
	//
	// @Test
	// public void testRecordTypeCreatorWithAutogeneratedIdNoMetadataGroupOrPresentationsExists() {
	// instanceFactory.userSuppliedId = false;
	//
	// DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType");
	// recordType.removeFirstChildWithNameInData("userSuppliedId");
	// recordType.addChild(new DataAtomicSpy("userSuppliedId", "false"));
	// DataCreator.addAllValuesToDataGroup(recordType, "myRecordType");
	//
	// callExtendedFunctionalityWithGroup(recordType);
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 10);
	//
	// assertCorrectlyCreatedMetadataGroup(2, "myRecordTypeGroup", "recordInfoGroup",
	// "myRecordType");
	// assertCorrectlyCreatedMetadataGroup(3, "myRecordTypeNewGroup",
	// "recordInfoAutogeneratedNewGroup", "myRecordType");
	//
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(4,
	// "myRecordTypePGroup", "myRecordTypeGroup", "input", "recordInfoPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(5,
	// "myRecordTypeNewPGroup", "myRecordTypeNewGroup", "input",
	// "recordInfoAutogeneratedNewPGroup");
	//
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(6,
	// "myRecordTypeOutputPGroup", "myRecordTypeGroup", "output",
	// "recordInfoOutputPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(7,
	// "myRecordTypeMenuPGroup", "myRecordTypeGroup", "output", "recordInfoOutputPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(8,
	// "myRecordTypeListPGroup", "myRecordTypeGroup", "output", "recordInfoOutputPGroup");
	// assertCorrectlyCreatedPresentationGroupWithIndexIdPresentationOfModeAndRecordInfo(9,
	// "myRecordTypeAutocompletePGroup", "myRecordTypeGroup", "input", "recordInfoPGroup");
	//
	// }

}
