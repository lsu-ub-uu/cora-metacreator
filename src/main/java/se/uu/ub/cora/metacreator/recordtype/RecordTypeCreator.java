package se.uu.ub.cora.metacreator.recordtype;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class RecordTypeCreator implements ExtendedFunctionality {

	private static final String RECORD_INFO = "recordInfo";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String METADATA_ID = "metadataId";
	private static final String METADATA_GROUP = "metadataGroup";
	private static final String INPUT_MODE = "input";
	private String authToken;
	private DataGroup topLevelDataGroup;
	private String dataDivider;
	private String implementingTextType;
	private String recordTypeId;
	private RecordReader recordReader;
	private RecordCreator recordCreator;
	private DataRecordGroup recordGroup;

	public RecordTypeCreator(String implementingTextType) {
		this.implementingTextType = implementingTextType;
		recordReader = SpiderInstanceProvider.getRecordReader();
		recordCreator = SpiderInstanceProvider.getRecordCreator();
	}

	public static RecordTypeCreator forImplementingTextType(String implementingTextType) {
		return new RecordTypeCreator(implementingTextType);
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		topLevelDataGroup = data.dataGroup;
		DataGroup recordInfo = topLevelDataGroup.getFirstGroupWithNameInData(RECORD_INFO);
		recordTypeId = recordInfo.getFirstAtomicValueWithNameInData("id");

		possiblyCreateNecessaryTextsMetadataAndPresentations();
	}

	private void possiblyCreateNecessaryTextsMetadataAndPresentations() {
		recordGroup = DataProvider.createRecordGroupFromDataGroup(topLevelDataGroup);
		recordGroup.getDataDivider();

		// extractDataDivider();
		// possiblyCreateText("textId");
		// possiblyCreateText("defTextId");

		// TO BE CONTINUE
		// possiblyCreateMetadataGroups();
		// possiblyCreatePresentationGroups();
	}

	// private void extractDataDivider() {
	//
	// DataGroup dataDividerGroup = extractDataDividerFromMainDataGroup();
	// dataDivider = dataDividerGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	// }

	// private void possiblyCreateText(String textIdToExtract) {
	// String textId = getTextId(textIdToExtract);
	// if (recordDoesNotExistInStorage(implementingTextType, textId)) {
	// createText(textId);
	// }
	// }

	// private String getTextId(String idToExtract) {
	// return getLinkedRecordIdFromGroupByNameInData(idToExtract);
	// }

	private String getLinkedRecordIdFromGroupByNameInData(String textIdToExtract) {
		DataGroup textGroup = topLevelDataGroup.getFirstGroupWithNameInData(textIdToExtract);
		return textGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private boolean recordDoesNotExistInStorage(String recordType, String presentationGroupId) {
		try {
			recordReader.readRecord(authToken, recordType, presentationGroupId);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	// private void createText(String textId) {
	// TextFactory textConstructor = TextFactoryImp.usingDataCreatorHelper(dataCreatorHelper);
	// DataGroup text = textConstructor.createTextUsingTextIdAndDataDividerId("someTextId",
	// "someDataDivider");
	// storeRecord(implementingTextType, text);
	// }

	private void possiblyCreateMetadataGroups() {
		possiblyCreateMetadataGroup(METADATA_ID, "recordInfoGroup");
		String recordInfoNewGroup = getIdForRecordInfoNew();
		possiblyCreateMetadataGroup("newMetadataId", recordInfoNewGroup);
	}

	private void possiblyCreateMetadataGroup(String metadataIdToExtract, String childReference) {
		String metadataId = getMetadataId(metadataIdToExtract);
		if (recordDoesNotExistInStorage(METADATA_GROUP, metadataId)) {
			createMetadataGroup(childReference, metadataId);
		}
	}

	private String getMetadataId(String metadataIdToExtract) {
		return getLinkedRecordIdFromGroupByNameInData(metadataIdToExtract);
	}

	private void createMetadataGroup(String childReference, String metadataId) {
		// MetadataGroupCreator groupCreator = MetadataGroupCreator
		// .withIdAndNameInDataAndDataDivider(metadataId, recordTypeId, dataDivider);
		// DataGroup metadataGroup = groupCreator.factorDataGroup(childReference);

		GroupFactory groupFactory = GroupFactory.withDataDividerAndValidationType(dataDivider,
				"someValidationType");
		DataRecordGroup metadataGroup = groupFactory.factorDataGroup(metadataId, "metadata",
				childReference, Optional.of("group"));
		metadataGroup.addChild(DataAtomicProvider
				.getDataAtomicUsingNameInDataAndValue("excludePGroupCreation", "true"));
		storeRecord(METADATA_GROUP, null);
		// storeRecord(METADATA_GROUP, metadataGroup);
	}

	private String getIdForRecordInfoNew() {
		return idIsAutoGenerated() ? "recordInfoAutogeneratedNewGroup" : "recordInfoNewGroup";
	}

	private boolean idIsAutoGenerated() {
		return "false"
				.equals(topLevelDataGroup.getFirstAtomicValueWithNameInData("userSuppliedId"));
	}

	private void possiblyCreatePresentationGroups() {
		String presentationOf = getPresentationOf(METADATA_ID);

		createFormPresentation(presentationOf);
		createNewFormPresentation(getPresentationOf("newMetadataId"));

		createOutputPresentations(presentationOf);
		createAutocompletePresentation(presentationOf);
	}

	private String getPresentationOf(String metadataId) {
		return getLinkedRecordIdFromGroupByNameInData(metadataId);
	}

	private void createFormPresentation(String presentationOf) {
		String presentationId = extractPresentationIdUsingNameInData("presentationFormId");
		List<DataChild> metadataChildReferences = getMetadataChildReferencesFromMetadataGroup(
				presentationOf);
		usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
				metadataChildReferences, INPUT_MODE);
	}

	private String extractPresentationIdUsingNameInData(String presentationNameInData) {
		DataGroup presentationIdGroup = topLevelDataGroup
				.getFirstGroupWithNameInData(presentationNameInData);
		return presentationIdGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private List<DataChild> getMetadataChildReferencesFromMetadataGroup(String presentationOf) {
		DataRecord dataRecord = recordReader.readRecord(authToken, METADATA_GROUP, presentationOf);
		return dataRecord.getDataGroup().getFirstGroupWithNameInData("childReferences")
				.getChildren();
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
		List<DataChild> metadataChildReferences = getMetadataChildReferencesFromMetadataGroup(
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

	private void createNewFormPresentation(String presentationOf) {
		String presentationId = extractPresentationIdUsingNameInData("newPresentationFormId");
		List<DataChild> metadataChildReferences = getMetadataChildReferencesFromMetadataGroup(
				presentationOf);
		usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
				metadataChildReferences, INPUT_MODE);
	}

	// private DataGroup extractDataDividerFromMainDataGroup() {
	// DataGroup recordInfoGroup = topLevelDataGroup.getFirstGroupWithNameInData(RECORD_INFO);
	// return recordInfoGroup.getFirstGroupWithNameInData("dataDivider");
	// }

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

	private void storeRecord(String recordTypeToCreate, DataGroup dataGroupToStore) {

		recordCreator.createAndStoreRecord(authToken, recordTypeToCreate, dataGroupToStore);
	}

	private void createAutocompletePresentation(String presentationOf) {
		createPresentationWithPresentationOfIdAndModeOnlyRecordInfoAsChild(presentationOf,
				"autocompletePresentationView", INPUT_MODE);
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}
}
