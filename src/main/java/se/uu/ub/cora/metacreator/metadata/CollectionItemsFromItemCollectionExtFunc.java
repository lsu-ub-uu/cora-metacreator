/*
 * Copyright 2017, 2022, 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.metadata;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

//CollectionItemsFromItemCollectionExtFunc
public class CollectionItemsFromItemCollectionExtFunc implements ExtendedFunctionality {
	private String authToken;
	private DataGroup dataGroup;

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		this.dataGroup = data.dataGroup;

		possiblyCreateItems(authToken, dataGroup);
		// possiblyCreateTexts(authToken, dataGroup);
	}

	private void possiblyCreateItems(String authToken, DataGroup dataGroup) {
		DataRecordGroup recordGroup = DataProvider.createRecordGroupFromDataGroup(dataGroup);
		// DataProvider.createRecordGroupUsingNameInData(authToken)
		// DataGroup itemReferences = dataGroup
		// .getFirstGroupWithNameInData("collectionItemReferences");
		// for (DataChild child : itemReferences.getChildren()) {
		// DataGroup item = (DataGroup) child;
		// createItemIfMissing(authToken, item);
		// }
	}

	// private void createItemIfMissing(String authToken, DataGroup item) {
	// String id = extractId(item);
	// if (itemDoesNotExist(authToken, id)) {
	// createItem(id);
	// }
	// }
	//
	// private String extractId(DataGroup ref) {
	// return ref.getFirstAtomicValueWithNameInData("linkedRecordId");
	// }
	//
	// private boolean itemDoesNotExist(String authToken, String id) {
	// RecordReader reader = SpiderInstanceProvider.getRecordReader();
	// try {
	// reader.readRecord(authToken, "metadataCollectionItem", id);
	// } catch (RecordNotFoundException e) {
	// return true;
	// }
	// return false;
	// }
	//
	// private void createItem(String id) {
	// DataGroup item = DataGroupProvider.getDataGroupUsingNameInData("metadata");
	// String dataDivider = DataCreatorHelperImp.extractDataDividerIdFromDataGroup(dataGroup);
	// DataGroup recordInfo = DataCreatorHelperImp
	// .createRecordInfoWithIdAndDataDividerAndValidationType(id, dataDivider,
	// "someValidationTypeId");
	//
	// item.addChild(recordInfo);
	// MetadataCompleterImp completer = new MetadataCompleterImp();
	// completer.completeDataGroupWithLinkedTexts(item, "coraText");
	//
	// addAtomicValues(id, item);
	// item.addAttributeByIdWithValue("type", "collectionItem");
	// createRecord("genericCollectionItem", item);
	// }
	//
	// private void addAtomicValues(String linkedRecordId, DataGroup item) {
	// String nameInData = linkedRecordId.substring(0, linkedRecordId.indexOf("Item"));
	// item.addChild(
	// DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("nameInData", nameInData));
	// }
	//
	// private void createRecord(String recordTypeToCreate, DataGroup dataGroupToCreate) {
	// RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider.getRecordCreator();
	// spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
	// dataGroupToCreate);
	// }
	//
	// private void possiblyCreateTexts(String authToken, DataGroup dataGroup) {
	// RecordCreatorHelper recordCreatorHelper = RecordCreatorHelper
	// .withAuthTokenDataGroupAndImplementingTextType(authToken, dataGroup, "text");
	// recordCreatorHelper.createTextsIfMissing();
	// }
}
