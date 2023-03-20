package se.uu.ub.cora.metacreator.recordtype;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.DataChild;
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

public class RecordTypeCreateGroupsExtFunc implements ExtendedFunctionality {

	private static final boolean EXCLUDE_P_GROUP_CREATION = true;
	private static final String RECORD_INFO = "recordInfo";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String METADATA_ID = "metadataId";
	private static final String METADATA_GROUP = "metadataGroup";
	private static final String INPUT_MODE = "input";
	private String authToken;
	private DataGroup topLevelDataGroup;
	private String dataDivider;
	private String recordTypeId;
	private RecordReader recordReader;
	private RecordCreator recordCreator;
	private DataRecordGroup recordGroup;
	private GroupFactory groupFactory;
	private PGroupFactory pGroupFactory;

	private RecordTypeCreateGroupsExtFunc(GroupFactory groupFactory, PGroupFactory pGroupFactory) {
		this.groupFactory = groupFactory;
		this.pGroupFactory = pGroupFactory;
		recordReader = SpiderInstanceProvider.getRecordReader();
		recordCreator = SpiderInstanceProvider.getRecordCreator();
	}

	public static RecordTypeCreateGroupsExtFunc usingGroupFactoryAndPGroupFactory(
			GroupFactory groupFactory, PGroupFactory pGroupFactory) {
		return new RecordTypeCreateGroupsExtFunc(groupFactory, pGroupFactory);
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		recordGroup = DataProvider.createRecordGroupFromDataGroup(data.dataGroup);

		readRecordInfo();
		possiblyCreateNecessaryTextsMetadataAndPresentations();
	}

	private void readRecordInfo() {
		// extractDataDivider();
		recordTypeId = recordGroup.getId();
		dataDivider = recordGroup.getDataDivider();
	}

	private void possiblyCreateNecessaryTextsMetadataAndPresentations() {

		possiblyCreateMetadataGroups();
		// TO BE CONTINUED
		possiblyCreatePresentationGroups();
	}

	private void possiblyCreateMetadataGroups() {
		possiblyCreateMetadataGroup(METADATA_ID, "recordInfoGroup");
		String recordInfoNewGroup = decidedIdForRecordInfoNew();
		possiblyCreateMetadataGroup("newMetadataId", recordInfoNewGroup);
	}

	private String decidedIdForRecordInfoNew() {
		if (idIsAutoGenerated()) {
			return "recordInfoAutogeneratedNewGroup";
		}
		return "recordInfoNewGroup";
	}

	private boolean idIsAutoGenerated() {
		return "false".equals(recordGroup.getFirstAtomicValueWithNameInData("userSuppliedId"));
	}

	private void possiblyCreateMetadataGroup(String groupId, String childReference) {
		String metadataId = getLinkedRecordIdFromGroupByNameInData(groupId);
		if (recordDoesNotExistInStorage("metadata", metadataId)) {
			createMetadataGroup(metadataId, childReference);
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

	private void createMetadataGroup(String metadataId, String refToRecordInfo) {
		DataRecordGroup metadataGroup = groupFactory.factorMetadataGroup(dataDivider, metadataId,
				recordTypeId, refToRecordInfo, EXCLUDE_P_GROUP_CREATION);
		DataGroup dataGroupToStore = DataProvider.createGroupFromRecordGroup(metadataGroup);
		storeRecord(METADATA_GROUP, dataGroupToStore);
	}

	private void possiblyCreatePresentationGroups() {
		String presentationOf = getLinkedRecordIdFromGroupByNameInData(METADATA_ID);
		//
		createFormPresentation(presentationOf);
		createNewFormPresentation(getLinkedRecordIdFromGroupByNameInData("newMetadataId"));
		//
		// createOutputPresentations(presentationOf);
		// createAutocompletePresentation(presentationOf);
	}

	private void createFormPresentation(String presentationOf) {
		possiblyCreateAndStorePresentationForGroup(presentationOf, "presentationFormId", "input");

		// String presentationId = extractPresentationIdUsingNameInData("presentationFormId");
		// List<DataChild> metadataChildReferences = getMetadataChildReferencesFromMetadataGroup(
		// presentationOf);
		// usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
		// metadataChildReferences, INPUT_MODE);

	}

	private void createNewFormPresentation(String presentationOf) {
		possiblyCreateAndStorePresentationForGroup(presentationOf, "newPresentationFormId",
				"input");

		// String presentationId = extractPresentationIdUsingNameInData("newPresentationFormId");
		// List<DataChild> metadataChildReferences = readMetadataChildReferencesFromStorage(
		// presentationOf);
		// usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
		// metadataChildReferences, INPUT_MODE);
	}

	private void possiblyCreateAndStorePresentationForGroup(String presentationOf,
			String presentationId, String mode) {
		DataRecordLink presentationLink = recordGroup
				.getFirstChildOfTypeAndName(DataRecordLink.class, presentationId);
		List<DataGroup> childReferences = readMetadataChildReferencesFromStorage(presentationOf);

		DataRecordGroup pFormRecordGroup = pGroupFactory
				.factorPGroupWithIdDataDividerPresentationOfModeAndChildren(dataDivider,
						presentationOf, mode, childReferences);

		// if (recordDoesNotExistInStorage("presentation", pFormRecordGroup.getId())) {
		if (recordDoesNotExistInStorage("presentation", presentationLink.getLinkedRecordId())) {
			DataGroup pFormGroup = DataProvider.createGroupFromRecordGroup(pFormRecordGroup);

			recordCreator.createAndStoreRecord(authToken, "presentationGroup", pFormGroup);
		}
	}

	private String extractPresentationIdUsingNameInData(String presentationNameInData) {
		DataGroup presentationIdGroup = topLevelDataGroup
				.getFirstGroupWithNameInData(presentationNameInData);
		return presentationIdGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private List<DataGroup> readMetadataChildReferencesFromStorage(String presentationOf) {
		DataRecord metadataRecord = recordReader.readRecord(authToken, "metadata", presentationOf);
		DataGroup metadataGroup = metadataRecord.getDataGroup();
		return metadataGroup.getChildrenOfTypeAndName(DataGroup.class, "childReferences");

		// return dataRecord.getDataGroup().getFirstGroupWithNameInData("childReferences")
		// .getChildren();
	}

	private void usePGroupCreatorWithPresentationOfIdChildRefsAndMode(String presentationOf,
			String presentationId, List<DataChild> metadataChildReferences, String mode) {
		PresentationGroupCreator presentationGroupCreator = PresentationGroupCreator
				.withAuthTokenPresentationIdAndDataDivider(authToken, presentationId, dataDivider);
		presentationGroupCreator.setPresentationOfAndMode(presentationOf, mode);
		presentationGroupCreator.setMetadataChildReferences(metadataChildReferences);
		presentationGroupCreator.createPGroupIfNotAlreadyExist();
	}

	private void createOutputPresentations(String presentationOf) {
		String mode = "output";
		createViewPresentation(presentationOf, mode);
		createPresentationWithPresentationOfIdAndModeOnlyRecordInfoAsChild(presentationOf,
				"menuPresentationViewId", mode);
		createPresentationWithPresentationOfIdAndModeOnlyRecordInfoAsChild(presentationOf,
				"listPresentationViewId", mode);
	}

	private void createViewPresentation(String presentationOf, String mode) {
		List<DataChild> metadataChildReferences = readMetadataChildReferencesFromStorage(
				presentationOf);
		String presentationId = extractPresentationIdUsingNameInData("presentationViewId");
		usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
				metadataChildReferences, mode);
	}

	private void createPresentationWithPresentationOfIdAndModeOnlyRecordInfoAsChild(
			String presentationOf, String presentationIdToExtract, String mode) {
		String presentationId = extractPresentationIdUsingNameInData(presentationIdToExtract);
		DataRecord dataRecord = recordReader.readRecord(authToken, METADATA_GROUP, presentationOf);
		List<DataChild> metadataChildReferences = getRecordInfoAsMetadataChildReference(dataRecord);
		usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
				metadataChildReferences, mode);
	}

	private List<DataChild> getRecordInfoAsMetadataChildReference(DataRecord dataRecord) {
		List<DataChild> metadataChildReferences = new ArrayList<>();
		DataGroup childReferences = getChildReferences(dataRecord);
		for (DataChild DataChild : childReferences.getChildren()) {
			addChildIfRecordInfo(metadataChildReferences, DataChild);
		}
		return metadataChildReferences;
	}

	private DataGroup getChildReferences(DataRecord dataRecord) {
		DataGroup dataGroup = dataRecord.getDataGroup();
		return dataGroup.getFirstGroupWithNameInData("childReferences");
	}

	private void addChildIfRecordInfo(List<DataChild> metadataChildReferences,
			DataChild dataElement) {
		String linkedRecordId = getRefLinkedRecordId((DataGroup) dataElement);
		if (refIsRecordInfo(linkedRecordId)) {
			metadataChildReferences.add(dataElement);
		}
	}

	private String getRefLinkedRecordId(DataGroup dataElement) {
		DataGroup childReference = dataElement;
		DataGroup ref = childReference.getFirstGroupWithNameInData("ref");
		return ref.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private boolean refIsRecordInfo(String linkedRecordId) {
		return linkedRecordId.startsWith(RECORD_INFO);
	}

	private void storeRecord(String type, DataGroup dataGroup) {

		recordCreator.createAndStoreRecord(authToken, type, dataGroup);
	}

	private void createAutocompletePresentation(String presentationOf) {
		createPresentationWithPresentationOfIdAndModeOnlyRecordInfoAsChild(presentationOf,
				"autocompletePresentationView", INPUT_MODE);
	}
}