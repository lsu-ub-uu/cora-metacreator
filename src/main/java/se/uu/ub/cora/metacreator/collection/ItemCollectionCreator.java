package se.uu.ub.cora.metacreator.collection;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.RecordCreatorHelper;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class ItemCollectionCreator implements ExtendedFunctionality {

	private final String implementingTextType;
	private String authToken;
	private DataGroup dataGroup;

	public ItemCollectionCreator(String implementingTextType) {
		this.implementingTextType = implementingTextType;
	}

	public static ItemCollectionCreator forImplementingTextType(String implementingTextType) {
		return new ItemCollectionCreator(implementingTextType);
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		this.authToken = authToken;
		this.dataGroup = dataGroup;

		possiblyCreateItems(authToken, dataGroup);
		possiblyCreateTexts(authToken, dataGroup);
	}

	private void possiblyCreateItems(String authToken, DataGroup dataGroup) {
		DataGroup itemReferences = dataGroup
				.getFirstGroupWithNameInData("collectionItemReferences");
		for (DataElement child : itemReferences.getChildren()) {
			DataGroup item = (DataGroup) child;
			createItemIfMissing(authToken, item);
		}
	}

	private void createItemIfMissing(String authToken, DataGroup item) {
		String id = extractId(item);
		if (itemDoesNotExist(authToken, id)) {
			createItem(id);
		}
	}

	private String extractId(DataGroup child) {
		DataGroup ref = child;
		return ref.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private boolean itemDoesNotExist(String userId, String id) {
		RecordReader reader = SpiderInstanceProvider.getRecordReader();
		try {
			reader.readRecord(userId, "metadataCollectionItem", id);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void createItem(String id) {
		DataGroup item = DataGroupProvider.getDataGroupUsingNameInData("metadata");
		String dataDivider = DataCreatorHelper.extractDataDividerStringFromDataGroup(dataGroup);
		DataGroup recordInfo = DataCreatorHelper.createRecordInfoWithIdAndDataDivider(id,
				dataDivider);

		item.addChild(recordInfo);
		MetadataCompleterImp completer = new MetadataCompleterImp();
		completer.completeDataGroupWithLinkedTexts(item, "coraText");

		addAtomicValues(id, item);
		item.addAttributeByIdWithValue("type", "collectionItem");
		createRecord("genericCollectionItem", item);
	}

	private void addAtomicValues(String linkedRecordId, DataGroup item) {
		String nameInData = linkedRecordId.substring(0, linkedRecordId.indexOf("Item"));
		item.addChild(
				DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("nameInData", nameInData));
	}

	private void createRecord(String recordTypeToCreate, DataGroup dataGroupToCreate) {
		RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider
				.getRecordCreator(recordTypeToCreate);
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				dataGroupToCreate);
	}

	private void possiblyCreateTexts(String authToken, DataGroup dataGroup) {
		RecordCreatorHelper recordCreatorHelper = RecordCreatorHelper
				.withAuthTokenDataGroupAndImplementingTextType(authToken, dataGroup,
						implementingTextType);
		recordCreatorHelper.createTextsIfMissing();
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}
}
