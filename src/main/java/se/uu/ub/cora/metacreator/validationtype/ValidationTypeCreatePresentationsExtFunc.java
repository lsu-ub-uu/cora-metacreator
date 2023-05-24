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
package se.uu.ub.cora.metacreator.validationtype;

import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.metacreator.group.PGroupFactory;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class ValidationTypeCreatePresentationsExtFunc implements ExtendedFunctionality {

	private static final String INPUT = "input";
	private static final String METADATA_ID = "metadataId";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private String authToken;
	private String dataDivider;
	private RecordReader recordReader;
	private RecordCreator recordCreator;
	private DataRecordGroup recordGroup;
	private PGroupFactory pGroupFactory;
	private String pOfMetadataId;
	private String pOfnewMetadataId;
	private List<DataGroup> childReferences;
	private List<DataGroup> childReferencesNewMetadata;

	private ValidationTypeCreatePresentationsExtFunc(PGroupFactory pGroupFactory) {
		this.pGroupFactory = pGroupFactory;
		recordReader = SpiderInstanceProvider.getRecordReader();
		recordCreator = SpiderInstanceProvider.getRecordCreator();
	}

	public static ValidationTypeCreatePresentationsExtFunc usingPGroupFactory(
			PGroupFactory pGroupFactory) {
		return new ValidationTypeCreatePresentationsExtFunc(pGroupFactory);
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		recordGroup = DataProvider.createRecordGroupFromDataGroup(data.dataGroup);
		dataDivider = recordGroup.getDataDivider();

		readChildReferencesFromMetadataAndNewMetadataGroup();
		possiblyCreatePresentationGroups();
	}

	private void readChildReferencesFromMetadataAndNewMetadataGroup() {
		pOfMetadataId = getLinkedRecordIdFromRecordTypeByNameInData(METADATA_ID);
		pOfnewMetadataId = getLinkedRecordIdFromRecordTypeByNameInData(NEW_METADATA_ID);

		childReferences = readChildReferencesFromLinkedRecordId(pOfMetadataId);
		childReferencesNewMetadata = readChildReferencesFromLinkedRecordId(pOfnewMetadataId);
	}

	private List<DataGroup> readChildReferencesFromLinkedRecordId(String recordId) {
		DataRecord metadataRecord = recordReader.readRecord(authToken, "metadata", recordId);
		DataGroup metadataGroup = metadataRecord.getDataGroup();
		DataGroup childReferencesFromRecord = metadataGroup
				.getFirstChildOfTypeAndName(DataGroup.class, "childReferences");
		return childReferencesFromRecord.getChildrenOfTypeAndName(DataGroup.class,
				"childReference");
	}

	private String getLinkedRecordIdFromRecordTypeByNameInData(String textIdToExtract) {
		DataRecordLink link = recordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				textIdToExtract);
		return link.getLinkedRecordId();
	}

	private void possiblyCreatePresentationGroups() {
		possiblyCreateAndStorePresentationForGroup(pOfMetadataId, "presentationFormId", INPUT,
				childReferences);
		possiblyCreateAndStorePresentationForGroup(pOfnewMetadataId, "newPresentationFormId", INPUT,
				childReferencesNewMetadata);
	}

	private void possiblyCreateAndStorePresentationForGroup(String presentationOf,
			String presentationGroupName, String mode, List<DataGroup> childReferences) {
		String presentationId = getPresentationIdFromLink(presentationGroupName);
		possiblyCreateAndStorePresentationWithPresentationId(presentationId, presentationOf, mode,
				childReferences);
	}

	private void possiblyCreateAndStorePresentationWithPresentationId(String presentationId,
			String presentationOf, String mode, List<DataGroup> childReferences) {
		if (recordDoesNotExistInStorage("presentation", presentationId)) {
			DataRecordGroup pFormRecordGroup = createPresentation(presentationId, presentationOf,
					childReferences, mode);
			storeDataRecordGroup(pFormRecordGroup);
		}
	}

	private String getPresentationIdFromLink(String presentationGroupName) {
		DataRecordLink presentationLink = recordGroup
				.getFirstChildOfTypeAndName(DataRecordLink.class, presentationGroupName);
		return presentationLink.getLinkedRecordId();
	}

	private DataRecordGroup createPresentation(String presentationId, String presentationOf,
			List<DataGroup> childReferences, String mode) {
		return pGroupFactory
				.factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences(
						authToken, presentationId, dataDivider, presentationOf, mode,
						childReferences);
	}

	private void storeDataRecordGroup(DataRecordGroup pFormRecordGroup) {
		DataGroup pFormGroup = DataProvider.createGroupFromRecordGroup(pFormRecordGroup);
		recordCreator.createAndStoreRecord(authToken, "presentation", pFormGroup);
	}

	private boolean recordDoesNotExistInStorage(String recordType, String id) {
		try {
			recordReader.readRecord(authToken, recordType, id);
			return false;
		} catch (RecordNotFoundException e) {
			return true;
		}
	}

	public PGroupFactory onlyForTestGetPGroupFactory() {
		return pGroupFactory;
	}
}
