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
package se.uu.ub.cora.metacreator.recordtype;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.DataGroup;
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

public class RecordTypeCreatePresentationsExtFunc implements ExtendedFunctionality {

	private static final String METADATA = "metadata";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String OUTPUT = "output";
	private static final String INPUT = "input";
	private static final String RECORD_INFO = "recordInfo";
	private static final String METADATA_ID = "metadataId";
	private String authToken;
	private String dataDivider;
	private RecordReader recordReader;
	private RecordCreator recordCreator;
	private DataRecordGroup recordGroup;
	private PGroupFactory pGroupFactory;
	private String pOfMetadataId;
	private List<DataGroup> childReferences;
	private List<DataGroup> childReferencesRecordInfo;

	private RecordTypeCreatePresentationsExtFunc(PGroupFactory pGroupFactory) {
		this.pGroupFactory = pGroupFactory;
		recordReader = SpiderInstanceProvider.getRecordReader();
		recordCreator = SpiderInstanceProvider.getRecordCreator();
	}

	public static RecordTypeCreatePresentationsExtFunc usingPGroupFactory(
			PGroupFactory pGroupFactory) {
		return new RecordTypeCreatePresentationsExtFunc(pGroupFactory);
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		recordGroup = data.dataRecordGroup;
		dataDivider = recordGroup.getDataDivider();

		readChildReferencesFromMetadataAndNewMetadataGroup();
		possiblyCreatePresentationGroups();
	}

	private void readChildReferencesFromMetadataAndNewMetadataGroup() {
		pOfMetadataId = getLinkedRecordIdFromRecordTypeByNameInData(METADATA_ID);

		childReferences = readChildReferencesFromLinkedRecordId(pOfMetadataId);
		childReferencesRecordInfo = createChildReferencesOnlyRecordInfo();
	}

	private List<DataGroup> readChildReferencesFromLinkedRecordId(String recordId) {
		DataRecord metadataRecord = recordReader.readRecord(authToken, METADATA, recordId);
		DataRecordGroup metadataGroup = metadataRecord.getDataRecordGroup();
		DataGroup childReferencesFromRecord = metadataGroup
				.getFirstChildOfTypeAndName(DataGroup.class, CHILD_REFERENCES);
		return childReferencesFromRecord.getChildrenOfTypeAndName(DataGroup.class, CHILD_REFERENCE);
	}

	private List<DataGroup> createChildReferencesOnlyRecordInfo() {
		List<DataGroup> childRefs = new ArrayList<>();
		for (DataGroup childReference : childReferences) {
			String linkId = getMetadataRefId(childReference);
			if (refIsRecordInfo(linkId)) {
				childRefs.add(childReference);
				return childRefs;
			}
		}
		return childRefs;
	}

	private String getMetadataRefId(DataGroup metadataChildReference) {
		DataRecordLink metadataChildReferenceId = metadataChildReference
				.getFirstChildOfTypeAndName(DataRecordLink.class, "ref");
		return metadataChildReferenceId.getLinkedRecordId();
	}

	private boolean refIsRecordInfo(String linkedRecordId) {
		DataRecord metadataRecord = recordReader.readRecord(authToken, METADATA, linkedRecordId);
		DataRecordGroup metadataGroup = metadataRecord.getDataRecordGroup();
		String nameInData = metadataGroup.getFirstAtomicValueWithNameInData("nameInData");
		return RECORD_INFO.equals(nameInData);
	}

	private String getLinkedRecordIdFromRecordTypeByNameInData(String textIdToExtract) {
		DataRecordLink link = recordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				textIdToExtract);
		return link.getLinkedRecordId();
	}

	private void possiblyCreatePresentationGroups() {
		possiblyCreateAndStorePresentationForGroup(pOfMetadataId, "presentationViewId", OUTPUT,
				childReferences);
		possiblyCreateAndStorePresentationForGroup(pOfMetadataId, "menuPresentationViewId", OUTPUT,
				childReferencesRecordInfo);
		possiblyCreateAndStorePresentationForGroup(pOfMetadataId, "listPresentationViewId", OUTPUT,
				childReferencesRecordInfo);
		possiblyCreateAndStorePresentationForGroup(pOfMetadataId, "autocompletePresentationView",
				INPUT, childReferencesRecordInfo);
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
		recordCreator.createAndStoreRecord(authToken, "presentation", pFormRecordGroup);
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
