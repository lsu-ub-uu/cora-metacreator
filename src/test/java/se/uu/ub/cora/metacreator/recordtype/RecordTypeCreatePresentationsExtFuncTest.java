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
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.spies.RecordCreatorSpy;
import se.uu.ub.cora.spider.spies.RecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class RecordTypeCreatePresentationsExtFuncTest {

	private RecordTypeCreatePresentationsExtFunc extfunc;
	private DataFactorySpy dataFactory;
	private SpiderInstanceFactorySpy instanceFactory;

	private static final String METADATA_ID_LINK_ID = "someMetadataLinkId";
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
	public void testReadPresentationOfForMeatadata() throws Exception {

		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "presentation",
				MENU_P_VIEW_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordType);

		recordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 0, DataRecordLink.class,
				"metadataId");
		DataRecordLinkSpy metadataLink = (DataRecordLinkSpy) recordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 0);

		recordReader.MCR.assertParameters("readRecord", 0, AUTH_TOKEN, "metadata",
				metadataLink.getLinkedRecordId());
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
		recordCreator.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN, "presentation",
				pFormGroup);
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

		recordReader.MCR.assertNumberOfCallsToMethod("readRecord", 6);
		recordCreator.MCR.assertNumberOfCallsToMethod("createAndStoreRecord", 0);
	}

	private DataRecordSpy readMetadataIdRecord() {
		DataRecordSpy readRecordMetadataId = (DataRecordSpy) recordReader.MCR
				.getReturnValue("readRecord", 0);
		return readRecordMetadataId;
	}

	@Test
	public void testViewPresentation() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "presentation",
				P_VIEW_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(2, 1, "presentationViewId", P_VIEW_ID_LINK_ID);
		assertCreationAndStoreOfPresentation(readMetadataIdRecord(), P_VIEW_ID_LINK_ID,
				METADATA_ID_LINK_ID, "output");
	}

	@Test
	public void testMenuPresentationViewId() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "presentation",
				MENU_P_VIEW_ID_LINK_ID);

		setRecordReaderToReturnRecordWithChildReferenceIds(METADATA_ID_LINK_ID, "someRefId",
				"recordInfoSomeRefId");
		setRecordReaderToReturnMetadataRecordForIdWithChildNameInData("recordInfoSomeRefId",
				"recordInfo");

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(4, 2, "menuPresentationViewId", MENU_P_VIEW_ID_LINK_ID);
		assertCreationAndStoreOfPresentationWithOnlyRecordInfos("recordInfoSomeRefId",
				MENU_P_VIEW_ID_LINK_ID, METADATA_ID_LINK_ID, "output");
	}

	private void setRecordReaderToReturnMetadataRecordForIdWithChildNameInData(String recordId,
			String childNameInData) {
		DataRecordSpy newMetadataRecord = new DataRecordSpy();
		recordReader.MRV.setSpecificReturnValuesSupplier("readRecord", () -> newMetadataRecord,
				AUTH_TOKEN, "metadata", recordId);
		DataGroupSpy newMetadataGroup = new DataGroupSpy();
		newMetadataRecord.MRV.setDefaultReturnValuesSupplier("getDataGroup",
				() -> newMetadataGroup);

		newMetadataGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> childNameInData, "nameInData");
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
		recordCreator.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN, "presentation",
				pFormGroup);
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
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "presentation",
				LIST_P_VIEW_ID_LINK_ID);

		setRecordReaderToReturnRecordWithChildReferenceIds(METADATA_ID_LINK_ID, "someRefId",
				"recordInfoSomeRefId");
		setRecordReaderToReturnMetadataRecordForIdWithChildNameInData("recordInfoSomeRefId",
				"recordInfo");

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(5, 3, "listPresentationViewId", LIST_P_VIEW_ID_LINK_ID);
		assertCreationAndStoreOfPresentationWithOnlyRecordInfos("recordInfoSomeRefId",
				LIST_P_VIEW_ID_LINK_ID, METADATA_ID_LINK_ID, "output");
	}

	@Test
	public void testAutocompletePresentationView() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "presentation",
				AUTOCOMPLETE_P_VIEW_ID_LINK_ID);

		setRecordReaderToReturnRecordWithChildReferenceIds(METADATA_ID_LINK_ID, "someRefId",
				"recordInfoSomeRefId");
		setRecordReaderToReturnMetadataRecordForIdWithChildNameInData("recordInfoSomeRefId",
				"recordInfo");

		callExtendedFunctionalityWithGroup(recordType);

		assertPresentationLink(6, 4, "autocompletePresentationView",
				AUTOCOMPLETE_P_VIEW_ID_LINK_ID);
		assertCreationAndStoreOfPresentationWithOnlyRecordInfos("recordInfoSomeRefId",
				AUTOCOMPLETE_P_VIEW_ID_LINK_ID, METADATA_ID_LINK_ID, "input");
	}

	private void assertPresentationLink(int callNumberRecordReader, int callNumber,
			String groupName, String presentationId) {
		recordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", callNumber,
				DataRecordLink.class, groupName);

		recordReader.MCR.assertParameters("readRecord", callNumberRecordReader, AUTH_TOKEN,
				"presentation", presentationId);
	}
}
