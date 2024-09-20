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
package se.uu.ub.cora.metacreator.recordtype;

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.spies.RecordCreatorSpy;
import se.uu.ub.cora.spider.spies.RecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class RecordTypeCreateGroupsExtFuncTest {
	private RecordTypeCreateGroupsExtFunc recordTypeCreator;
	private SpiderInstanceFactorySpy instanceFactory;
	private static final String METADATA_ID_LINK_ID = "someMetadataLinkId";
	private static final String NEW_METADATA_ID_LINK_ID = "someNewMetadataIdLinkId";
	private static final String RECORD_TYPE_ID = "someRecordTypeId";
	private static final String AUTH_TOKEN = "someAuthToken";
	private static final String DATA_DIVIDER = "someDataDivider";
	private RecordReaderSpy recordReader;
	private RecordCreatorSpy recordCreator;
	private MetadataGroupFactorySpy groupFactory;
	private DataRecordGroupSpy recordType;

	@BeforeMethod
	public void setUp() {
		recordType = new DataRecordGroupSpy();
		groupFactory = new MetadataGroupFactorySpy();

		DataRecordLinkSpy metadataIdLink = new DataRecordLinkSpy();
		metadataIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> METADATA_ID_LINK_ID);
		recordType.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataIdLink, DataRecordLink.class, "metadataId");
		DataRecordLinkSpy newMetadataIdLink = new DataRecordLinkSpy();
		newMetadataIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> NEW_METADATA_ID_LINK_ID);
		recordType.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newMetadataIdLink, DataRecordLink.class, "newMetadataId");
		recordType.MRV.setDefaultReturnValuesSupplier("getDataDivider", () -> DATA_DIVIDER);
		recordType.MRV.setDefaultReturnValuesSupplier("getId", () -> RECORD_TYPE_ID);

		recordReader = new RecordReaderSpy();
		recordCreator = new RecordCreatorSpy();

		instanceFactory = new SpiderInstanceFactorySpy();
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
				() -> recordReader);
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordCreator",
				() -> recordCreator);
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);

		recordTypeCreator = RecordTypeCreateGroupsExtFunc.usingGroupFactory(groupFactory);
	}

	@Test
	public void testConstructor() throws Exception {
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 1);
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordCreator", 1);
	}

	@Test
	public void testOnlyForTestGetGroupFactory() throws Exception {
		assertSame(recordTypeCreator.onlyForTestGetGroupFactory(), groupFactory);
	}

	@Test
	public void testCheckDataDividerFromRecordGroup() throws Exception {
		recordReader.MRV.setAlwaysThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"));

		callExtendedFunctionalityWithGroup(recordType);

		recordType.MCR.assertParameters("getDataDivider", 0);
		recordType.MCR.assertParameters("getId", 0);

		assertCreateAndStoreGroup(0, "metadataId", "recordInfoGroup");
	}

	private void assertCreateAndStoreGroup(int callNumber, String groupId, String recordInfoGroup) {
		recordType.MCR.assertParameters("getFirstChildOfTypeAndName", callNumber,
				DataRecordLink.class, groupId);

		DataRecordLinkSpy metadataIdLinkId = (DataRecordLinkSpy) recordType.MCR
				.getReturnValue("getFirstChildOfTypeAndName", callNumber);

		recordReader.MCR.assertParameters("readRecord", callNumber, AUTH_TOKEN, "metadata",
				metadataIdLinkId.getLinkedRecordId());

		groupFactory.MCR.assertParameters("factorMetadataGroup", callNumber, DATA_DIVIDER,
				metadataIdLinkId.getLinkedRecordId(), RECORD_TYPE_ID, recordInfoGroup, true);
		var metadataIdGroup = groupFactory.MCR.getReturnValue("factorMetadataGroup", callNumber);

		recordCreator.MCR.assertParameters("createAndStoreRecord", callNumber, AUTH_TOKEN,
				"metadata", metadataIdGroup);
	}

	private void callExtendedFunctionalityWithGroup(DataRecordGroup dataRecordGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataRecordGroup = dataRecordGroup;
		recordTypeCreator.useExtendedFunctionality(data);
	}

	@Test
	public void testNewMetadataGroupAlreadyExistsCreateMetadataIdOnly() throws Exception {
		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "metadata",
				METADATA_ID_LINK_ID);

		callExtendedFunctionalityWithGroup(recordType);

		groupFactory.MCR.assertNumberOfCallsToMethod("factorMetadataGroup", 1);
		groupFactory.MCR.assertParameters("factorMetadataGroup", 0, DATA_DIVIDER,
				METADATA_ID_LINK_ID, RECORD_TYPE_ID, "recordInfoGroup", true);
	}
}
