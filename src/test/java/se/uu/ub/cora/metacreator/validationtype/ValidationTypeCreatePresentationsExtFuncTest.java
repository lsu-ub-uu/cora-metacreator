/*
 * Copyright 2017, 2022, 2024 Uppsala University Library
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
package se.uu.ub.cora.metacreator.validationtype;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
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

public class ValidationTypeCreatePresentationsExtFuncTest {

	private ValidationTypeCreatePresentationsExtFunc extfunc;
	private DataFactorySpy dataFactory;
	private SpiderInstanceFactorySpy instanceFactory;

	private static final String METADATA_ID_LINK_ID = "someMetadataLinkId";
	private static final String NEW_METADATA_ID_LINK_ID = "someNewMetadataIdLinkId";
	private static final String P_FORM_ID_LINK_ID = "somePresentationFormId";
	private static final String NEW_P_FORM_ID_LINK_ID = "someNewPresentationFormId";

	private static final String AUTH_TOKEN = "someAuthToken";
	private static final String DATA_DIVIDER = "someDataDivider";

	private RecordReaderSpy recordReader;
	private RecordCreatorSpy recordCreator;
	private PGroupFactorySpy pGroupFactory;
	private DataRecordGroupSpy recordGroup;

	@BeforeMethod
	public void setUp() {
		pGroupFactory = new PGroupFactorySpy();
		recordGroup = new DataRecordGroupSpy();

		DataRecordLinkSpy metadataIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy newMetadataIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy presentationFormIdLink = new DataRecordLinkSpy();
		DataRecordLinkSpy newPresentationFormIdLink = new DataRecordLinkSpy();

		metadataIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> METADATA_ID_LINK_ID);
		newMetadataIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> NEW_METADATA_ID_LINK_ID);
		presentationFormIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> P_FORM_ID_LINK_ID);
		newPresentationFormIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> NEW_P_FORM_ID_LINK_ID);

		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataIdLink, DataRecordLink.class, "metadataId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newMetadataIdLink, DataRecordLink.class, "newMetadataId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> presentationFormIdLink, DataRecordLink.class, "presentationFormId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newPresentationFormIdLink, DataRecordLink.class, "newPresentationFormId");

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

		extfunc = ValidationTypeCreatePresentationsExtFunc.usingPGroupFactory(pGroupFactory);
	}

	@Test
	public void testConstructor() throws Exception {
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 1);
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordCreator", 1);
	}

	@Test
	public void testCheckDataDividerFromRecordGroup() throws Exception {
		callExtendedFunctionalityWithGroup(recordGroup);

		recordGroup.MCR.assertParameters("getDataDivider", 0);
	}

	private void callExtendedFunctionalityWithGroup(DataRecordGroup dataRecordGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataRecordGroup = dataRecordGroup;
		extfunc.useExtendedFunctionality(data);
	}

	@Test
	public void testReadPresentationOfForMeatadataAndMetadataId() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "presentation",
				NEW_P_FORM_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordGroup);

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
		DataRecordGroupSpy newMetadataGroup = new DataRecordGroupSpy();
		newMetadataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
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
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "presentation",
				P_FORM_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordGroup);

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

		recordCreator.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN, "presentation",
				formPresentation);
		recordCreator.MCR.assertNumberOfCallsToMethod("createAndStoreRecord", 1);
	}

	private Object assertReadChildReferencesFromRecord(DataRecordSpy readRecordMetadataId) {
		DataRecordGroupSpy metadataIdDataGroup = (DataRecordGroupSpy) readRecordMetadataId.MCR
				.getReturnValue("getDataRecordGroup", 0);

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
		callExtendedFunctionalityWithGroup(recordGroup);

		recordReader.MCR.assertNumberOfCallsToMethod("readRecord", 4);
		recordCreator.MCR.assertNumberOfCallsToMethod("createAndStoreRecord", 0);
	}

	@Test
	public void testFormNewPresentation() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "presentation",
				NEW_P_FORM_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordGroup);

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

	private void assertPresentationLink(int callNumber, String groupName, String presentationId) {
		recordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", callNumber,
				DataRecordLink.class, groupName);

		recordReader.MCR.assertParameters("readRecord", callNumber, AUTH_TOKEN, "presentation",
				presentationId);
	}
}
