package se.uu.ub.cora.metacreator.collection;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.record.SpiderRecordCreator;
import se.uu.ub.cora.spider.record.SpiderRecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class CollectionVarFromItemCollectionCreator implements ExtendedFunctionality {

	private String authToken;
	private DataGroup itemCollectionToCreateFrom;
	private String itemCollectionId;
	private String idForCollectionVariable;

	@Override
	public void useExtendedFunctionality(String authToken,
			DataGroup itemCollectionToCreateFrom) {
		this.authToken = authToken;
		this.itemCollectionToCreateFrom = itemCollectionToCreateFrom;
		extractIds();

		if (collectionVarDoesNotExist(idForCollectionVariable)) {
			DataGroup collectionVar = extractDataAndConstructCollectionVariable(
					itemCollectionToCreateFrom);
			createRecord("metadataCollectionVariable", collectionVar);
		}
	}

	private void extractIds() {
		itemCollectionId = extractIdFromItemCollection();
		idForCollectionVariable = itemCollectionId + "Var";
	}

	private boolean collectionVarDoesNotExist(String id) {
		SpiderRecordReader reader = SpiderInstanceProvider.getSpiderRecordReader();
		try {
			reader.readRecord(authToken, "metadataCollectionVariable", id);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private DataGroup extractDataAndConstructCollectionVariable(
			DataGroup itemCollectionToCreateFrom) {

		String nameInData = itemCollectionToCreateFrom.getFirstAtomicValueWithNameInData("nameInData");
		String dataDivider = DataCreatorHelper
				.extractDataDividerStringFromDataGroup(itemCollectionToCreateFrom);
		return constructCollectionVariable(idForCollectionVariable, nameInData, dataDivider);
	}

	private DataGroup constructCollectionVariable(String id, String nameInData,
			String dataDivider) {
		CollectionVariableConstructor constructor = new CollectionVariableConstructor();
		return constructor.constructCollectionVarWithIdNameInDataDataDividerAndRefCollection(id,
				nameInData, dataDivider, itemCollectionId);
	}

	private String extractIdFromItemCollection() {
		DataGroup recordInfo = itemCollectionToCreateFrom.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void createRecord(String recordTypeToCreate, DataGroup dataGroupToCreate) {
		SpiderRecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider
				.getSpiderRecordCreator();
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				dataGroupToCreate);
	}
}
