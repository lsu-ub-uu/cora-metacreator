/*
 * Copyright 2023, 2024 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.metacreator.recordtype.MetadataGroupFactory;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class ValidationTypeCreateGroupsExtFunc implements ExtendedFunctionality {

	private DataRecordGroup recordGroup;

	private MetadataGroupFactory groupFactory;
	private RecordReader recordReader;
	private RecordCreator recordCreator;
	private String authToken;
	private String dataDivider;
	private String recordTypeId;

	private static final String METADATA_ID = "metadataId";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private static final String METADATA = "metadata";
	private static final boolean EXCLUDE_P_GROUP_CREATION = true;
	private DataRecordGroup validatesRecordTypeGroup;

	private ValidationTypeCreateGroupsExtFunc(MetadataGroupFactory groupFactory) {
		this.groupFactory = groupFactory;
		recordReader = SpiderInstanceProvider.getRecordReader();
		recordCreator = SpiderInstanceProvider.getRecordCreator();
	}

	public static ValidationTypeCreateGroupsExtFunc usingGroupFactory(
			MetadataGroupFactory groupFactory) {
		return new ValidationTypeCreateGroupsExtFunc(groupFactory);
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		readDataFromExtendedFunctionality(data);
		tryToReadValidatesRecordTypeGroup();

		readIdAndDataDivider();
		possiblyCreateMetadataGroup(METADATA_ID, "recordInfoGroup");
		possiblyCreateMetadataGroup(NEW_METADATA_ID, decidedIdForRecordInfoNew());
	}

	private void readDataFromExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		recordGroup = data.dataRecordGroup;
	}

	private void tryToReadValidatesRecordTypeGroup() {
		try {
			readValidatesRecordTypeGroup();
		} catch (Exception e) {
			throw DataValidationException
					.withMessage("Record for validates record type could not be found in storage.");
		}
	}

	private void readValidatesRecordTypeGroup() {
		DataRecordLink validatesRecordTypelink = recordGroup
				.getFirstChildOfTypeAndName(DataRecordLink.class, "validatesRecordType");
		DataRecord recordTypeRecord = recordReader.readRecord(authToken, "recordType",
				validatesRecordTypelink.getLinkedRecordId());

		validatesRecordTypeGroup = recordTypeRecord.getDataRecordGroup();
	}

	private void readIdAndDataDivider() {
		recordTypeId = recordGroup.getId();
		dataDivider = recordGroup.getDataDivider();
	}

	private void possiblyCreateMetadataGroup(String groupId, String childReference) {
		String metadataId = getLinkedRecordIdFromGroupByNameInData(groupId);
		if (recordDoesNotExistInStorage(METADATA, metadataId)) {
			createAndStoreMetadataGroup(metadataId, childReference);
		}
	}

	private String getLinkedRecordIdFromGroupByNameInData(String textIdToExtract) {
		DataRecordLink link = recordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				textIdToExtract);
		return link.getLinkedRecordId();
	}

	private boolean recordDoesNotExistInStorage(String recordType, String id) {
		try {
			recordReader.readRecord(authToken, recordType, id);
			return false;
		} catch (RecordNotFoundException e) {
			return true;
		}
	}

	private String decidedIdForRecordInfoNew() {
		if (idIsAutoGenerated()) {
			return "recordInfoAutogeneratedNewGroup";
		}
		return "recordInfoNewGroup";
	}

	private boolean idIsAutoGenerated() {
		return "true".equals(
				validatesRecordTypeGroup.getFirstAtomicValueWithNameInData("userSuppliedId"));
	}

	private void createAndStoreMetadataGroup(String metadataId, String refToRecordInfo) {
		DataRecordGroup dataRecordGroupToStore = createMetadataGroup(metadataId, refToRecordInfo);
		storeMetadataGroup(METADATA, dataRecordGroupToStore);
	}

	private DataRecordGroup createMetadataGroup(String metadataId, String refToRecordInfo) {
		return groupFactory.factorMetadataGroup(dataDivider, metadataId, recordTypeId,
				refToRecordInfo, EXCLUDE_P_GROUP_CREATION);
	}

	private void storeMetadataGroup(String type, DataRecordGroup dataRecordGroup) {
		recordCreator.createAndStoreRecord(authToken, type, dataRecordGroup);
	}

	public MetadataGroupFactory onlyForTestGetGroupFactory() {
		return groupFactory;
	}
}
