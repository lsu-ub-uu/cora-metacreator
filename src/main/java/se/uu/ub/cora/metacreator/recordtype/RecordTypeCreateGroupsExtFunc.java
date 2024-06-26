/*
 * Copyright 2023 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class RecordTypeCreateGroupsExtFunc implements ExtendedFunctionality {

	private DataRecordGroup recordGroup;

	private MetadataGroupFactory groupFactory;
	private RecordReader recordReader;
	private RecordCreator recordCreator;
	private String authToken;
	private String dataDivider;
	private String recordTypeId;

	private static final String METADATA_ID = "metadataId";
	private static final String METADATA = "metadata";
	private static final boolean EXCLUDE_P_GROUP_CREATION = true;

	private RecordTypeCreateGroupsExtFunc(MetadataGroupFactory groupFactory) {
		this.groupFactory = groupFactory;
		recordReader = SpiderInstanceProvider.getRecordReader();
		recordCreator = SpiderInstanceProvider.getRecordCreator();
	}

	public static RecordTypeCreateGroupsExtFunc usingGroupFactory(
			MetadataGroupFactory groupFactory) {
		return new RecordTypeCreateGroupsExtFunc(groupFactory);
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		readAndConvertDataFromExtendednFunctionality(data);

		readIdAndDataDivider();
		possiblyCreateMetadataGroup(METADATA_ID, "recordInfoGroup");
	}

	private void readAndConvertDataFromExtendednFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		recordGroup = DataProvider.createRecordGroupFromDataGroup(data.dataGroup);
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

	private void createAndStoreMetadataGroup(String metadataId, String refToRecordInfo) {
		DataGroup dataGroupToStore = createMetadataGroup(metadataId, refToRecordInfo);
		storeMetadataGroup(METADATA, dataGroupToStore);
	}

	private DataGroup createMetadataGroup(String metadataId, String refToRecordInfo) {
		DataRecordGroup metadataGroup = groupFactory.factorMetadataGroup(dataDivider, metadataId,
				recordTypeId, refToRecordInfo, EXCLUDE_P_GROUP_CREATION);
		return DataProvider.createGroupFromRecordGroup(metadataGroup);
	}

	private void storeMetadataGroup(String type, DataGroup dataGroup) {
		recordCreator.createAndStoreRecord(authToken, type, dataGroup);
	}

	public MetadataGroupFactory onlyForTestGetGroupFactory() {
		return groupFactory;
	}
}
