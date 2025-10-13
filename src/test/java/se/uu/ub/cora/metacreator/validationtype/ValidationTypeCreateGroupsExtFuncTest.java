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

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.data.spies.DataRecordSpy;
import se.uu.ub.cora.metacreator.recordtype.MetadataGroupFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.spies.RecordCreatorSpy;
import se.uu.ub.cora.spider.spies.RecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class ValidationTypeCreateGroupsExtFuncTest {
	private ValidationTypeCreateGroupsExtFunc extendedFunc;
	private DataFactorySpy dataFactory;
	private SpiderInstanceFactorySpy instanceFactory;
	private static final String VALIDATES_RECORD_TYPE_LINK = "someValidatesRecordTypeLink";
	private static final String METADATA_ID_LINK_ID = "someMetadataLinkId";
	private static final String NEW_METADATA_ID_LINK_ID = "someNewMetadataIdLinkId";
	private static final String VALIDATION_TYPE_ID = "someValidationTypeId";
	private static final String AUTH_TOKEN = "someAuthToken";
	private static final String DATA_DIVIDER = "someDataDivider";
	private RecordReaderSpy recordReader;
	private RecordCreatorSpy recordCreator;
	private MetadataGroupFactorySpy groupFactory;
	private DataRecordGroupSpy recordGroup;

	private DataRecordSpy recordTypeRecord;
	private DataRecordGroupSpy recordTypeGroup;

	@BeforeMethod
	public void setUp() {
		groupFactory = new MetadataGroupFactorySpy();

		recordGroup = new DataRecordGroupSpy();
		DataRecordLinkSpy metadataIdLink = new DataRecordLinkSpy();
		metadataIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> METADATA_ID_LINK_ID);
		DataRecordLinkSpy valdiatesRecodTypeLink = new DataRecordLinkSpy();
		valdiatesRecodTypeLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> VALIDATES_RECORD_TYPE_LINK);
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataIdLink, DataRecordLink.class, "metadataId");
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> valdiatesRecodTypeLink, DataRecordLink.class, "validatesRecordType");
		DataRecordLinkSpy newMetadataIdLink = new DataRecordLinkSpy();
		newMetadataIdLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> NEW_METADATA_ID_LINK_ID);
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newMetadataIdLink, DataRecordLink.class, "newMetadataId");
		recordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider", () -> DATA_DIVIDER);
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> VALIDATION_TYPE_ID);

		dataFactory = new DataFactorySpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> recordGroup);
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		recordTypeGroup = new DataRecordGroupSpy();
		recordTypeRecord = new DataRecordSpy();
		recordTypeRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> recordTypeGroup);
		recordReader = new RecordReaderSpy();
		recordReader.MRV.setSpecificReturnValuesSupplier("readRecord", () -> recordTypeRecord,
				AUTH_TOKEN, "recordType", VALIDATES_RECORD_TYPE_LINK);
		recordReader.MRV.setSpecificReturnValuesSupplier("readRecord", () -> new DataRecordSpy(),
				AUTH_TOKEN, "metadata", METADATA_ID_LINK_ID);

		recordCreator = new RecordCreatorSpy();

		instanceFactory = new SpiderInstanceFactorySpy();
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
				() -> recordReader);
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordCreator",
				() -> recordCreator);
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);

		extendedFunc = ValidationTypeCreateGroupsExtFunc.usingGroupFactory(groupFactory);
	}

	@Test
	public void testConstructor() {
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 1);
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordCreator", 1);
	}

	@Test
	public void testOnlyForTestGetGroupFactory() {
		assertSame(extendedFunc.onlyForTestGetGroupFactory(), groupFactory);
	}

	@Test
	public void testCheckDataDividerFromRecordGroup() {
		setReadRecordForMetadataToNotFoundRecord();

		callExtendedFunctionalityWithGroup(recordGroup);

		recordGroup.MCR.assertParameters("getDataDivider", 0);
		recordGroup.MCR.assertParameters("getId", 0);

		recordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 1, DataRecordLink.class,
				"metadataId");

		DataRecordLinkSpy metadataIdLinkId = (DataRecordLinkSpy) recordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 1);

		recordReader.MCR.assertParameters("readRecord", 1, AUTH_TOKEN, "metadata",
				metadataIdLinkId.getLinkedRecordId());

		groupFactory.MCR.assertParameters("factorMetadataGroup", 0, DATA_DIVIDER,
				metadataIdLinkId.getLinkedRecordId(), VALIDATION_TYPE_ID, "recordInfoGroup", true);
		var metadataIdGroup = groupFactory.MCR.getReturnValue("factorMetadataGroup", 0);

		recordCreator.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN, "metadata",
				metadataIdGroup);
	}

	private void callExtendedFunctionalityWithGroup(DataRecordGroup dataRecordGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataRecordGroup = dataRecordGroup;
		extendedFunc.useExtendedFunctionality(data);
	}

	@Test
	public void testReadIsAutoGeneratedReadsFromRecordType() {
		callExtendedFunctionalityWithGroup(recordGroup);

		recordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 0, DataRecordLink.class,
				"validatesRecordType");

		DataRecordLink validatesRecordTypelink = (DataRecordLink) recordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 0);
		recordReader.MCR.assertParameters("readRecord", 0, AUTH_TOKEN, "recordType",
				validatesRecordTypelink.getLinkedRecordId());

		DataRecordSpy recordType = (DataRecordSpy) recordReader.MCR.getReturnValue("readRecord", 0);
		recordType.MCR.assertParameters("getDataRecordGroup", 0);

		DataRecordGroupSpy recordTypeRecordGroup = (DataRecordGroupSpy) recordType.MCR
				.getReturnValue("getDataRecordGroup", 0);
		recordTypeRecordGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				"idSource");
	}

	@Test(expectedExceptions = DataValidationException.class, expectedExceptionsMessageRegExp = ""
			+ "Record for validates record type could not be found in storage.")
	public void testReadValidatesRecordTypeNotFoundInStorage() {
		recordReader.MRV.setAlwaysThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"));

		callExtendedFunctionalityWithGroup(recordGroup);
	}

	@Test
	public void testCreateGroupWithAutogenerated() {
		setReadRecordForMetadataToNotFoundRecord();
		setReadRecordForNewMetadataToNotFoundRecord();

		recordTypeGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "userSupplied", "idSource");

		callExtendedFunctionalityWithGroup(recordGroup);

		groupFactory.MCR.assertParameter("factorMetadataGroup", 1, "childRefRecordInfoId",
				"recordInfoNewGroup");
	}

	private void setReadRecordForNewMetadataToNotFoundRecord() {
		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "metadata",
				NEW_METADATA_ID_LINK_ID);
	}

	private void setReadRecordForMetadataToNotFoundRecord() {
		recordReader.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("someErrorMessage"), AUTH_TOKEN, "metadata",
				METADATA_ID_LINK_ID);
	}

	@Test
	public void testCreateGroupWithNotAutogenerated_timestamp() {
		setReadRecordForMetadataToNotFoundRecord();
		setReadRecordForNewMetadataToNotFoundRecord();

		recordTypeRecord.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "timestamp", "idSource");

		callExtendedFunctionalityWithGroup(recordGroup);

		groupFactory.MCR.assertParameter("factorMetadataGroup", 1, "childRefRecordInfoId",
				"recordInfoAutogeneratedNewGroup");
	}

	@Test
	public void testCreateGroupWithNotAutogenerated_sequence() {
		setReadRecordForMetadataToNotFoundRecord();
		setReadRecordForNewMetadataToNotFoundRecord();

		recordTypeRecord.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "sequence", "idSource");

		callExtendedFunctionalityWithGroup(recordGroup);

		groupFactory.MCR.assertParameter("factorMetadataGroup", 1, "childRefRecordInfoId",
				"recordInfoAutogeneratedNewGroup");
	}

	@Test
	public void testNewMetadataGroupAlreadyExistsCreateMetadataIdOnly() {
		setReadRecordForMetadataToNotFoundRecord();

		callExtendedFunctionalityWithGroup(recordGroup);

		groupFactory.MCR.assertNumberOfCallsToMethod("factorMetadataGroup", 1);
		groupFactory.MCR.assertParameters("factorMetadataGroup", 0, DATA_DIVIDER,
				METADATA_ID_LINK_ID, VALIDATION_TYPE_ID, "recordInfoGroup", true);
	}

}
