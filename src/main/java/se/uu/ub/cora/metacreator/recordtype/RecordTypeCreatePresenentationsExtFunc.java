package se.uu.ub.cora.metacreator.recordtype;

import java.util.ArrayList;
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

public class RecordTypeCreatePresenentationsExtFunc implements ExtendedFunctionality {

	private static final String OUTPUT = "output";
	private static final String INPUT = "input";
	private static final String RECORD_INFO = "recordInfo";
	private static final String METADATA_ID = "metadataId";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private String authToken;
	private String dataDivider;
	private RecordReader recordReader;
	private RecordCreator recordCreator;
	private DataRecordGroup recordGroup;
	private PGroupFactory pGroupFactory;
	private String pOfMetadata;
	private String pOfnewMeteadata;
	private List<DataGroup> childReferences;
	private List<DataGroup> childReferencesNewMetadata;
	private List<DataGroup> childReferencesRecordInfo;

	private RecordTypeCreatePresenentationsExtFunc(PGroupFactory pGroupFactory) {
		this.pGroupFactory = pGroupFactory;
		recordReader = SpiderInstanceProvider.getRecordReader();
		recordCreator = SpiderInstanceProvider.getRecordCreator();
		childReferencesRecordInfo = new ArrayList<>();
	}

	public static RecordTypeCreatePresenentationsExtFunc usingPGroupFactory(
			PGroupFactory pGroupFactory) {
		return new RecordTypeCreatePresenentationsExtFunc(pGroupFactory);
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
		pOfMetadata = getLinkedRecordIdFromGroupByNameInData(METADATA_ID);
		pOfnewMeteadata = getLinkedRecordIdFromGroupByNameInData(NEW_METADATA_ID);

		childReferences = readChildReferencesFromMetadataGroup(pOfMetadata);
		childReferencesNewMetadata = readChildReferencesFromMetadataGroup(pOfnewMeteadata);
		// TODO: Menu, list and autocomplet must create presentations that only contains recordInfo
		// in the childReferences. The Code mark with SPIKE needs test to be created
		// SPIKE
		createChildReferencesOnlyRecordInfo();
	}

	// SPIKE
	private void createChildReferencesOnlyRecordInfo() {
		for (DataGroup childReference : childReferences) {
			String linkId = getMetadataRefId(childReference);
			if (refIsRecordInfo(linkId)) {
				childReferencesRecordInfo.add(childReference);
			}
		}
	}

	// SPIKE
	private String getMetadataRefId(DataGroup metadataChildReference) {
		DataRecordLink metadataChildReferenceId = metadataChildReference
				.getFirstChildOfTypeAndName(DataRecordLink.class, "ref");
		return metadataChildReferenceId.getLinkedRecordId();
	}

	// SPIKE
	private boolean refIsRecordInfo(String linkedRecordId) {
		return linkedRecordId.startsWith(RECORD_INFO);
	}

	private String getLinkedRecordIdFromGroupByNameInData(String textIdToExtract) {
		DataRecordLink link = recordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				textIdToExtract);
		return link.getLinkedRecordId();
	}

	private void possiblyCreatePresentationGroups() {

		possiblyCreateAndStorePresentationForGroup(pOfMetadata, "presentationFormId", INPUT,
				childReferences);
		possiblyCreateAndStorePresentationForGroup(pOfnewMeteadata, "newPresentationFormId", INPUT,
				childReferencesNewMetadata);
		possiblyCreateAndStorePresentationForGroup(pOfMetadata, "presentationViewId", OUTPUT,
				childReferences);
		possiblyCreateAndStorePresentationForGroup(pOfMetadata, "menuPresentationViewId", OUTPUT,
				childReferencesRecordInfo);
		possiblyCreateAndStorePresentationForGroup(pOfMetadata, "listPresentationViewId", OUTPUT,
				childReferencesRecordInfo);
		possiblyCreateAndStorePresentationForGroup(pOfMetadata, "autocompletePresentationView",
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
		return pGroupFactory.factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences(
				authToken, presentationId, dataDivider, presentationOf, mode, childReferences);
	}

	private List<DataGroup> readChildReferencesFromMetadataGroup(String presentationOf) {
		DataRecord metadataRecord = recordReader.readRecord(authToken, "metadata", presentationOf);
		DataGroup metadataGroup = metadataRecord.getDataGroup();
		return metadataGroup.getChildrenOfTypeAndName(DataGroup.class, "childReferences");
	}

	private void storeDataRecordGroup(DataRecordGroup pFormRecordGroup) {
		DataGroup pFormGroup = DataProvider.createGroupFromRecordGroup(pFormRecordGroup);
		recordCreator.createAndStoreRecord(authToken, "presentationGroup", pFormGroup);
	}

	private boolean recordDoesNotExistInStorage(String recordType, String id) {
		try {
			recordReader.readRecord(authToken, recordType, id);
			return false;
		} catch (RecordNotFoundException e) {
			return true;
		}
	}
}
