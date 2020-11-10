package se.uu.ub.cora.metacreator.recordtype;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.metacreator.TextConstructor;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.record.SpiderRecordCreator;
import se.uu.ub.cora.spider.record.SpiderRecordReader;
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
	private SpiderRecordReader spiderRecordReader;

	public RecordTypeCreator(String implementingTextType) {
		this.implementingTextType = implementingTextType;
	}

	public static RecordTypeCreator forImplementingTextType(String implementingTextType) {
		return new RecordTypeCreator(implementingTextType);
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		this.authToken = authToken;
		this.topLevelDataGroup = dataGroup;
		spiderRecordReader = SpiderInstanceProvider.getSpiderRecordReader();
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData(RECORD_INFO);
		recordTypeId = recordInfo.getFirstAtomicValueWithNameInData("id");

		possiblyCreateNecessaryTextsMetadataAndPresentations();
	}

	private void possiblyCreateNecessaryTextsMetadataAndPresentations() {
		extractDataDivider();
		possiblyCreateText("textId");
		possiblyCreateText("defTextId");
		possiblyCreateMetadataGroups();
		possiblyCreatePresentationGroups();
	}

	private void possiblyCreateText(String textIdToExtract) {
		String textId = getTextId(textIdToExtract);
		if (recordDoesNotExistInStorage(implementingTextType, textId)) {
			createText(textId);
		}
	}

	private String getTextId(String idToExtract) {
		return getLinkedRecordIdFromGroupByNameInData(idToExtract);
	}

	private String getLinkedRecordIdFromGroupByNameInData(String textIdToExtract) {
		DataGroup textGroup = topLevelDataGroup.getFirstGroupWithNameInData(textIdToExtract);
		return textGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private boolean recordDoesNotExistInStorage(String recordType, String presentationGroupId) {
		try {
			spiderRecordReader.readRecord(authToken, recordType, presentationGroupId);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void createText(String textId) {
		TextConstructor textConstructor = TextConstructor.withTextIdAndDataDivider(textId,
				dataDivider);
		DataGroup text = textConstructor.createText();
		storeRecord(implementingTextType, text);
	}

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
		MetadataGroupCreator groupCreator = MetadataGroupCreator
				.withIdAndNameInDataAndDataDivider(metadataId, recordTypeId, dataDivider);
		DataGroup metadataGroup = groupCreator.createGroup(childReference);
		metadataGroup.addChild(DataAtomicProvider
				.getDataAtomicUsingNameInDataAndValue("excludePGroupCreation", "true"));
		storeRecord(METADATA_GROUP, metadataGroup);
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
		List<DataElement> metadataChildReferences = getMetadataChildReferencesFromMetadataGroup(
				presentationOf);
		usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
				metadataChildReferences, INPUT_MODE);
	}

	private String extractPresentationIdUsingNameInData(String presentationNameInData) {
		DataGroup presentationIdGroup = topLevelDataGroup
				.getFirstGroupWithNameInData(presentationNameInData);
		return presentationIdGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private List<DataElement> getMetadataChildReferencesFromMetadataGroup(String presentationOf) {
		DataRecord dataRecord = spiderRecordReader.readRecord(authToken, METADATA_GROUP,
				presentationOf);
		return dataRecord.getDataGroup().getFirstGroupWithNameInData("childReferences")
				.getChildren();
	}

	private void usePGroupCreatorWithPresentationOfIdChildRefsAndMode(String presentationOf,
			String presentationId, List<DataElement> metadataChildReferences, String mode) {
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
		List<DataElement> metadataChildReferences = getMetadataChildReferencesFromMetadataGroup(
				presentationOf);
		String presentationId = extractPresentationIdUsingNameInData("presentationViewId");
		usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
				metadataChildReferences, mode);
	}

	private void createPresentationWithPresentationOfIdAndModeOnlyRecordInfoAsChild(
			String presentationOf, String presentationIdToExtract, String mode) {
		String presentationId = extractPresentationIdUsingNameInData(presentationIdToExtract);
		DataRecord dataRecord = spiderRecordReader.readRecord(authToken, METADATA_GROUP,
				presentationOf);
		List<DataElement> metadataChildReferences = getRecordInfoAsMetadataChildReference(
				dataRecord);
		usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
				metadataChildReferences, mode);
	}

	private void createNewFormPresentation(String presentationOf) {
		String presentationId = extractPresentationIdUsingNameInData("newPresentationFormId");
		List<DataElement> metadataChildReferences = getMetadataChildReferencesFromMetadataGroup(
				presentationOf);
		usePGroupCreatorWithPresentationOfIdChildRefsAndMode(presentationOf, presentationId,
				metadataChildReferences, INPUT_MODE);
	}

	private void extractDataDivider() {
		DataGroup dataDividerGroup = extractDataDividerFromMainDataGroup();
		dataDivider = dataDividerGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private DataGroup extractDataDividerFromMainDataGroup() {
		DataGroup recordInfoGroup = topLevelDataGroup.getFirstGroupWithNameInData(RECORD_INFO);
		return recordInfoGroup.getFirstGroupWithNameInData("dataDivider");
	}

	private List<DataElement> getRecordInfoAsMetadataChildReference(DataRecord dataRecord) {
		List<DataElement> metadataChildReferences = new ArrayList<>();
		DataGroup childReferences = getChildReferences(dataRecord);
		for (DataElement DataElement : childReferences.getChildren()) {
			addChildIfRecordInfo(metadataChildReferences, DataElement);
		}
		return metadataChildReferences;
	}

	private DataGroup getChildReferences(DataRecord dataRecord) {
		DataGroup dataGroup = dataRecord.getDataGroup();
		return dataGroup.getFirstGroupWithNameInData("childReferences");
	}

	private void addChildIfRecordInfo(List<DataElement> metadataChildReferences,
			DataElement dataElement) {
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
		SpiderRecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider
				.getSpiderRecordCreator();
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				dataGroupToStore);
	}

	private void createAutocompletePresentation(String presentationOf) {
		createPresentationWithPresentationOfIdAndModeOnlyRecordInfoAsChild(presentationOf,
				"autocompletePresentationView", INPUT_MODE);
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}
}
